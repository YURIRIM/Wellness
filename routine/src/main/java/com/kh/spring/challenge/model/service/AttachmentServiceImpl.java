package com.kh.spring.challenge.model.service;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.ProfileResponse;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.FileSanitizer;
import com.kh.spring.util.attachment.ReadMetadata;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.attachment.Watermark;
import com.kh.spring.util.common.UuidUtil;

import jakarta.servlet.http.HttpSession;

@Service
public class AttachmentServiceImpl implements AttachmentService {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private AttachmentDao dao;
	@Autowired
	private ChallengeDao chalDao;
	@Autowired
	private ProfileService profileService;

	// 비동기 - summernote에서 사진 넣기
	@Override
	public String insertAtChal(HttpSession session, MultipartFile file) throws Exception {

		// 첨부 사진 없으면 끄져라.
		if (file == null || file.isEmpty())
			throw new Exception("사진이 없어요..");

		// 사진 유효성 확인
		if (!FileSanitizer.attachmentSanitizer(file))
			throw new Exception("attachment 유효성");

		// uuid발급하기
		Map<String, Object> uuidMap = UuidUtil.createUUID();

		// Attachment 넣기
		Attachment at = Attachment.builder()
				.uuid((byte[]) uuidMap.get("uuidRaw"))
				.fileContent(file.getBytes())
				.fileName(file.getOriginalFilename())
				.fileSize((int) file.getSize())
				.build();

		// 사진 리사이즈
		at.setFileContent(ResizeWebp.resizeWebp(at.getFileContent()));

		// 메타데이터 소독
		if (Exiftool.EXIFTOOL) {
			at.setFileContent(Exiftool.sanitizeMetadata(at.getFileContent()));
		}

		// Attachment테이블에 넣자!
		int result = dao.insertAtChal(sqlSession, at);
		if (!(result > 0))
			throw new Exception("at DB저장 실패");
		
		return (String) uuidMap.get("uuid");
	}

	// challenge content의 img 태그가 요청하는 경로
	@Override
	public ResponseEntity<byte[]> selectAtChal(HttpSession session, String uuidStr) throws Exception {
		if (uuidStr == null || uuidStr.isEmpty())
			return ResponseEntity.notFound().build();

		byte[] uuid = UuidUtil.strToByteArr(uuidStr);
		Attachment at = dao.selectAtChal(sqlSession, uuid);

		if (at == null || at.getFileContent() == null)
			return ResponseEntity.notFound().build();

		// Content-Type/MIME
		String contentType = "image/webp";

		// Content-Disposition: inline; filename= ~
		String fileName = at.getFileName();
		if (fileName == null || fileName.isBlank()) {
			fileName = uuidStr + ".webp";
		}

		// UTF-8 퍼센트 인코딩 (공백은 "%20"으로)
		fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
				.replaceAll("\\+", "%20");

		// Content-Disposition 작성 (filename* 사용!)
		fileName = "inline; filename*=UTF-8''" + fileName;

		return ResponseEntity.ok()
				.header("Content-Type", contentType)
				.header("Content-Disposition", fileName)
				.body(at.getFileContent());
	}

	//비동기 - 댓글 사진 조회
	@Override
	public ResponseEntity<byte[]> selectAtComment(List<Integer> commentNos) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		
		List<Attachment> atts = dao.selectAtComment(sqlSession, commentNos);
		
		for(Attachment at : atts) {
			// 사진 있나요??
			if (at != null && at.getFileContent()!=null) {
				// 확장자는 webp
				String originalName = at.getFileName();
				String extension = ".webp";
				if (originalName != null && originalName.contains(".")) {
					extension = originalName.substring(originalName.lastIndexOf("."));
				}
	
				// 파일 이름을 commentNo_안아줘요.webp 로 변경
				String zipFileName = at.getRefNo() + "_" + (originalName != null ? originalName : "unnamed" + extension);
	
				// ZIP 엔트리 추가
				ZipEntry entry = new ZipEntry(zipFileName);
				zos.putNextEntry(entry);
				zos.write(at.getFileContent());
				zos.closeEntry();
			}
		}

		zos.close();
		byte[] zipBytes = baos.toByteArray();
		
		return ResponseEntity.ok()
				.header("Content-Type", "application/zip")
				.header("Content-Disposition", "attachment; filename=\"comment_images.zip\"")
				.body(zipBytes);
	}

	// 비동기 - 댓글 사진 업로드
	@Override
	public ResponseEntity<String> insertAtComment(HttpSession session, MultipartFile file, int chalNo) throws Exception {
		// 파일 없는데 뭐임?
		if (file == null || file.isEmpty())
			throw new Exception("없잖아! 날 속였어!");

		// 사진 유효성 확인
		if (!FileSanitizer.attachmentSanitizer(file))
			throw new Exception("attachment 유효성");

		// uuid발급하기
		Map<String, Object> uuidMap = UuidUtil.createUUID();

		// Attachment 넣기
		Attachment at = Attachment.builder()
				.uuid((byte[]) uuidMap.get("uuidRaw"))
				.fileContent(file.getBytes())
				.fileName(file.getOriginalFilename())
				.fileSize((int) file.getSize())
				.build();

		// 사진 메타데이터 검사
		String pictureRequired = chalDao.selectRequired(sqlSession, chalNo).getPictureRequired();
		if (pictureRequired.equals("I")) {
			int metaInspect = ReadMetadata.inspectAttachment(at);
			switch (metaInspect) {
			case 2:
				return ResponseEntity.status(400).build(); // 돚거는 가세요라
			case 0:
				throw new Exception("개발자도 알 수 없는 오류");
			}
		}
		
		// 사진 리사이즈
		at.setFileContent(ResizeWebp.resizeWebp(at.getFileContent()));
		
		//webp로 확장자 이름 변경
		String newName = FilenameUtils.getBaseName(at.getFileName()) + ".webp";
		at.setFileName(newName);
		
		// 메타데이터 계엄령
		if (Exiftool.EXIFTOOL)
			at.setFileContent(Exiftool.sanitizeMetadata(at.getFileContent()));
		
		// 낙인 찍기
		ProfileResponse myProfile = (ProfileResponse) session.getAttribute("myProfile");
		if (myProfile == null) {
			// myProfile이 없어? 없으면 코딩생활 끝나냐?
			profileService.selectMyProfile(session);
			myProfile = (ProfileResponse) session.getAttribute("myProfile");
		}

		// 워터마크 타입에 따라 낙인찍기
		switch (myProfile.getWatermarkType()) {
		case "C":// 개인
			byte[] watermark = Base64.getDecoder().decode(myProfile.getWatermarkBase64());
			at.setFileContent(Watermark.customStigma(at.getFileContent(), watermark));
			break;
		case "D":// 디폴트
			User loginUser = (User) session.getAttribute("loginUser");
			at.setFileContent(Watermark.defaultStigma(at.getFileContent(), loginUser.getNick()));
			break;
		}

		// Attachment테이블에 넣자!
		int result = dao.insertAtComment(sqlSession, at);
		if (!(result > 0))
			throw new Exception("at DB저장 실패");

		// uuid반환
		return ResponseEntity.ok((String) uuidMap.get("uuid"));
	}

	//비동기 - 디폴트 이미지 조회
	@Override
	public ResponseEntity<byte[]> defaultImg(String filename) throws Exception {
        Resource imgFile = new ClassPathResource("static/img/" + filename);

        if (!imgFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType = Files.probeContentType(imgFile.getFile().toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(bytes);
	}
}
