package com.kh.spring.challenge.model.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.ChallengeCommentResponse;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.FileSanitizer;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.common.UuidUtil;

import jakarta.servlet.http.HttpSession;


@Service
public class AttachmentServiceImpl implements AttachmentService {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private AttachmentDao dao;

	//비동기 - summernote에서 사진 넣기
	@Override
	public String insertAtChal(HttpSession session, MultipartFile file) throws Exception {

		// 첨부 사진 없으면 끄져라.
		if (file == null || file.isEmpty()) throw new Exception("사진이 없어요..");

		// 사진 유효성 확인
		if (!FileSanitizer.attachmentSanitizer(file))
			throw new Exception("attachment 유효성");

		//uuid발급하기
		Map<String, Object> uuidMap = UuidUtil.createUUID();
		
		// Attachment 넣기
		Attachment at = Attachment.builder()
				.uuid((byte[])uuidMap.get("uuidRaw"))
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
		
		return (String)uuidMap.get("uuid");
	}

	@Override
	public ResponseEntity<byte[]> selectAtChal(HttpSession session, String uuidStr) throws Exception {
		byte[] uuid = UuidUtil.strToByteArr(uuidStr);
		Attachment at = dao.selectAtChal(sqlSession,uuid);
		
	    if (at == null || at.getFileContent() == null) {
	    	//사진 없으면 404
	        return ResponseEntity.notFound().build();
	    }

	    //Content-Type/MIME
	    String contentType = "image/webp";
	    
	    //Content-Disposition: inline; filename= ~
	    String fileName = at.getFileName();
	    if (fileName == null || fileName.isBlank()) {
	        fileName = uuidStr + ".webp";
	    }

	    return ResponseEntity.ok()
	            .header("Content-Type", contentType)
	            .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
	            .body(at.getFileContent());
	}
	
	
	//댓글 사진 조회
	@Override
	public ResponseEntity<byte[]> selectAtComment(List<ChallengeCommentResponse> ccs) throws Exception {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ZipOutputStream zos = new ZipOutputStream(baos);

	    for (ChallengeCommentResponse cc : ccs) {
	        int commentNo = cc.getCommentNo();
	        Attachment at = dao.selectAtComment(sqlSession, commentNo);

	        //사진 있나요??
	        if (at != null && at.getFileContent() != null 
	            && "Y".equalsIgnoreCase(at.getStatus())) {

	            //확장자는 webp
	            String originalName = at.getFileName();
	            String extension = ".webp";
	            if (originalName != null && originalName.contains(".")) {
	                extension = originalName.substring(originalName.lastIndexOf("."));
	            }

	            //파일 이름을 commentNo_안아줘요.webp 로 변경
	            String zipFileName = commentNo + "_" + 
	                                 (originalName != null ? originalName : "unnamed" + extension);

	            //ZIP 엔트리 추가
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

	
	//비동기 - 댓글 사진 업로드
	@Override
	public String insertAtComment(HttpSession session, MultipartFile file, int chalNo) {
		//uuid반환
		return null;
	}

}
