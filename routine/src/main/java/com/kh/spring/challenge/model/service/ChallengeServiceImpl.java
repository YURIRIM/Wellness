package com.kh.spring.challenge.model.service;

import java.util.Base64;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChallengeCategoryDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.challenge.model.vo.ChallengeResponse;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchComment;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.FileSanitizer;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.challenge.ChallengeValidator;
import com.kh.spring.util.challenge.SearchChallengeValidator;
import com.kh.spring.util.common.BinaryAndBase64;
import com.kh.spring.util.common.Dummy;
import com.kh.spring.util.common.Regexp;

import jakarta.servlet.http.HttpSession;

@Service
public class ChallengeServiceImpl implements ChallengeService {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeDao chalDao;
	@Autowired
	private AttachmentDao atDao;
	@Autowired
	private ChallengeCategoryDao ccDao;
	@Autowired
	private ChallengeCommentService commentService;

	// controllerAdviser - CC조회
	@Override
	public List<ChallengeCategory> selectCCList() {
		return ccDao.selectCCList(sqlSession);
	}

	//챌린지 리스트 조회
	@Override
	public void selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception {
		model.addAttribute("searchChallenge", sc);//모달에 검색어 저장

		System.out.println("챌린지 검색어 : "+sc);
		
		//sc 유효성 확인
		if (!SearchChallengeValidator.searchChallenge(sc))
			throw new Exception("searchChallenge 유효성");
		
		//search trim()
		sc.setSearch(sc.getSearch().trim());

		// DB에서 페이지 긁어오기
		List<ChallengeResponse> result = chalDao.selectChal(sqlSession, sc);
		
		if (result == null || result.isEmpty())
			throw new Exception("챌린지 리스트 조회 불가");

		for (ChallengeResponse chal : result) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
			
			
			//썸네일 base64인코딩
			byte[] thumbnail = chal.getThumbnail();
			if(thumbnail!=null) {
				chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
				chal.setThumbnail(null);
			}
			
			//프로필 사진 base64인코딩
			byte[] profilePicture = chal.getProfilePicture();
			if(profilePicture!=null) {
				chal.setProfilePictureBase64(Base64.getEncoder().encodeToString(profilePicture));
				chal.setProfilePicture(null);
			}
		}

		// 모달에 넣기
		model.addAttribute("chalList", result);
	}

	// 챌린지 생성하기
	@Override
	@Transactional
	public void newChal(HttpSession session, Model model, ChallengeRequest chal, List<MultipartFile> files) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		if (user == null)
			user = Dummy.dummyUser(); // 로그인되지 않았으면 더미데이터
		int result = 0;
		
		System.out.println("새로운 챌린지 생성 : "+chal);

		// 제목 trim
		chal.setTitle(chal.getTitle().trim());

		// challenge 유효성 확인
		if (!ChallengeValidator.challenge(chal))
			throw new Exception("challenge 유효성");

		// 썸네일 있으면 Base64 url-safe 디코딩, 리사이즈 및 소독
		byte[] thumbnail = BinaryAndBase64.base64ToBinary(chal.getThumbnailBase64());
		if (thumbnail !=null) {
			//프로필 사진 있으면 thumbnail에 넣기
			chal.setThumbnail(thumbnail);
			chal.setThumbnailBase64(null);
		}

		// DB저장
		chal.setUserNo(user.getUserNo());
		result = chalDao.newChal(sqlSession, chal);

		// 실패하면 가세요라
		if (!(result > 0))
			throw new Exception("chall DB저장 실패");

		//------------------첨부사진 나와바리------------------
		for (MultipartFile file : files) {

			// 첨부 사진 없으면 끄져라.
			if (file == null || file.isEmpty())
				continue;

			// 사진 유효성 확인
			if (!FileSanitizer.attachmentSanitizer(file))
				throw new Exception("attachment 유효성");

			// Attachment 넣기
			Attachment at = Attachment.builder()
					.refNo(chal.getChalNo())
					.fileContent(file.getBytes())
					.fileName(file.getOriginalFilename())
				    .fileSize((int)file.getSize())
				    .build();
			
			System.out.println("새로운 챌린지 Attachment: "+at);

			// 사진 리사이즈
			at.setFileContent(ResizeWebp.resizeWebp(at.getFileContent()));

			// 메타데이터 소독
			if (Exiftool.EXIFTOOL) {
				at.setFileContent(Exiftool.sanitizeMetadata(at.getFileContent()));
			}
			
			// Attachment테이블에 넣자!
			result = atDao.insertAttachment(sqlSession, at);
			if (!(result > 0))
				throw new Exception("at DB저장 실패");
		}
	}
	
	//내가 참여/생성한 챌린지 보기
	@Override
	public void myChal(HttpSession session, Model model, SearchMyChallenge smc) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		model.addAttribute("searchChallenge", smc);//모달에 검색어 저장

		smc.setUserNo(user.getUserNo());
		System.out.println("챌린지 검색어 : "+smc);
		
		//유효성 검사
		if (!SearchChallengeValidator.searchMyChallenge(smc))
			throw new Exception("searchMyChallenge 유효성");
		
		//유형에 따라 DB에서 긁어오기
		List<ChallengeResponse> result =  chalDao.selectMyChal(sqlSession, smc);
		
		if (result == null || result.isEmpty())
			throw new Exception("챌린지 리스트 조회 불가");

		for (ChallengeResponse chal : result) {
			// 제목이 표시 제한 초과일 경우
			if (chal.getTitle().length() > Regexp.TITLE_SHOW_LIMIT) {
				chal.setTitle(chal.getTitle().substring(0, Regexp.TITLE_SHOW_LIMIT) + "⋯");
			}
			// 내용이 표시 제한 초과일 경우
			if (chal.getContent().length() > Regexp.CONTENT_SHOW_LIMIT) {
				chal.setContent(chal.getContent().substring(0, Regexp.CONTENT_SHOW_LIMIT) + "⋯");
			}
			
			
			//썸네일 base64인코딩
			byte[] thumbnail = chal.getThumbnail();
			if(thumbnail!=null) {
				chal.setThumbnailBase64(Base64.getEncoder().encodeToString(thumbnail));
				chal.setThumbnail(null);
			}
			
			//프로필 사진 정상화
			byte[] profilePicture = chal.getProfilePicture();
			if(profilePicture!=null) {
				chal.setProfilePictureBase64(Base64.getEncoder().encodeToString(profilePicture));
				chal.setProfilePicture(null);
			}
		}

		// 모달에 넣기
		model.addAttribute("chalList", result);
	}
	
	//챌린지 세부보기
	@Override
	public void chalDetail(int chalNo, Model model) throws Exception {
		ChallengeResponse chal = chalDao.chalDetail(sqlSession,chalNo);
		
		SearchComment sc = SearchComment.builder()
				.chalNo(chalNo)
				.currentPage(0).build();
		
		commentService.chalDetailComment(sc,model);
		model.addAttribute("chalDetail", chal);
	}
}
