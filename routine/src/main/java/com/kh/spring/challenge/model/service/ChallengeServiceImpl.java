package com.kh.spring.challenge.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.dao.AttachmentDao;
import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.FileSanitizer;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.challenge.ChallengeValidator;
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

	// 비동기 - 챌린지 메인에서 챌린지 리스트 조회
	@Override
	public void selectChal(HttpSession session, Model model, int currentPage) throws Exception {
		if (currentPage <= 0)
			throw new Exception("페이지 오류");

		// DB에서 페이지 긁어오기
		Map<String, Object> map = Map.of(
				"currentPage", currentPage
				, "showLimit", Regexp.CHAL_SHOW_LIMIT
				);
		
		List<Challenge> result = chalDao.selectChal(sqlSession, map);

		if(result==null ||result.isEmpty()) throw new Exception("챌린지 리스트 조회 불가");
		
		model.addAttribute(map);
		
	}

	// 챌린지 생성하기
	@Override
	@Transactional
	public void newChal(HttpSession session, Model model, Challenge chal, List<MultipartFile> files) throws Exception {
		User user = (User) session.getAttribute("loginUser");
		int result = 0;

		// challenge 유효성 확인
		if (!ChallengeValidator.challenge(chal))
			throw new Exception("challenge 유효성");
		
		//썸네일
		byte[] thumbnail = chal.getThumbnail();
		if(thumbnail!=null) {
			//썸네일 있으면 리사이즈 및 소독
			thumbnail = ResizeWebp.resizeWebp(thumbnail);
			if (Exiftool.exiftoolCheck()) {
				Exiftool.sanitizeMetadata(thumbnail);
			}
		}else {
			//썸네일 없으면 기본이미지 사용
			
		}

		// DB저장
		chal.setUserNo(user.getUserNo());
		result = chalDao.newChal(sqlSession, chal);

		// 실패하면 가세요라
		if (!(result > 0))
			throw new Exception("chall DB저장 실패");

		// 첨부 사진 없으면 볼일 끝났으니 끄져라.
		if (files == null || files.isEmpty())
			return;
		/*
		 * ----------------------------이 아래부터는 사진이 있어야만 발동---------------------------
		 */
		List<Attachment> atList = new ArrayList<>();

		// 사진 유효성 확인 및 attachment객체로 변환
		if (!FileSanitizer.attachmentSanitizer(files, atList))
			throw new Exception("attachment 유효성");

		for (Attachment at : atList) {
			// challNo넣기
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
