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
import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.challenge.model.vo.ChallengeCategory;
import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.FileSanitizer;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.challenge.ChallengeValidator;
import com.kh.spring.util.challenge.SearchChallengeValidator;
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

	// controllerAdviser - CC조회
	@Override
	public List<ChallengeCategory> selectCCList() {
		return ccDao.selectCCList(sqlSession);
	}

	// 비동기 - 챌린지 메인에서 챌린지 리스트 조회
	@Override
	public void selectChal(HttpSession session, Model model, SearchChallenge sc) throws Exception {

		// challenge 유효성 확인
		if (!SearchChallengeValidator.searchChallenge(sc))
			throw new Exception("searchChallenge 유효성");
		
		//search trim()
		sc.setSearch(sc.getSearch().trim());

		// DB에서 페이지 긁어오기
		List<Challenge> result = chalDao.selectChal(sqlSession, sc);
		if (result == null || result.isEmpty())
			throw new Exception("챌린지 리스트 조회 불가");

		for (Challenge chal : result) {
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
			}
		}

		// 모달에 넣기
		model.addAttribute("chalList", result);
	}

	// 챌린지 생성하기
	@Override
	@Transactional
	public void newChal(HttpSession session, Model model, Challenge chal, List<MultipartFile> files) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		if (user == null)
			user = Dummy.dummyUser(); // 로그인되지 않았으면 더미데이터
		int result = 0;

		// 제목 trim
		chal.setTitle(chal.getTitle().trim());

		// challenge 유효성 확인
		if (!ChallengeValidator.challenge(chal))
			throw new Exception("challenge 유효성");

		// 썸네일 있으면 바이너리 변환, 리사이즈 및 소독
		byte[] thumbnail = null;
		if (chal.getThumbnailBase64() != null) {
			thumbnail = Base64.getDecoder().decode(chal.getThumbnailBase64());
			thumbnail = ResizeWebp.resizeWebp(thumbnail);
			if (Exiftool.exiftoolCheck()) {
				thumbnail = Exiftool.sanitizeMetadata(thumbnail);
			}
			chal.setThumbnail(thumbnail);
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

			// 사진 유효성 확인 및 attachment객체로 변환
			Attachment at = new Attachment();
			if (!FileSanitizer.attachmentSanitizer(file, at))
				throw new Exception("attachment 유효성");

			// chalNo넣기
			at.setRefNo(result);

			// 사진 리사이즈
			at.setFile(ResizeWebp.resizeWebp(at.getFile()));

			// 메타데이터 소독
			if (Exiftool.exiftoolCheck()) {// exiftool이 있어요!
				at.setFile(Exiftool.sanitizeMetadata(at.getFile()));
			}
			
			// Attachment테이블에 넣자!
			result = atDao.insertAttachment(sqlSession, at);
			if (!(result > 0))
				throw new Exception("at DB저장 실패");
		}
	}
}
