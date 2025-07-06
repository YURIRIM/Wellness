package com.kh.spring.challenge.model.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.kh.spring.challenge.model.dao.ChallengeDao;
import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ChallengeValidator;
import com.kh.spring.util.challenge.FileSanitizer;
import com.kh.spring.util.challenge.ResizeWebp;

import jakarta.servlet.http.HttpSession;

@Service
public class ChallengeServiceImpl implements ChallengeService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ChallengeDao dao;
	
	//챌린지 생성하기
	@Override
	@Transactional
	public void newChal(HttpSession session, Model model
			, Challenge chal ,List<MultipartFile> files) throws Exception {
		User user = (User)session.getAttribute("loginUser");
		int result =0;
		
		//challenge 유효성 확인
		if(!ChallengeValidator.challenge(chal)) throw new Exception("challenge 유효성");
		
		//DB저장
		chal.setUserNo(user.getUserNo());
		result = dao.newChal(sqlSession, chal);
		
		//실패하면 가세요라
		if(!(result>0)) throw new Exception("chall DB저장 실패");
		
		//첨부 사진 없으면 볼일 끝났으니 끄져라.
		if(files==null || files.isEmpty()) return;
		
		//첨부 사진이 있어요!
		List<Attachment> atList = new ArrayList<>();
		
		//사진 유효성 확인 및 attachment객체로 변환
		if(!FileSanitizer.attachmentSanitizer(files,atList)) throw new Exception("attachment 유효성");
		
		for(Attachment at : atList) {
			//challNo넣기
			at.setChalNo(result);
			
			//사진 메타데이터 검증
			
			//사진 리사이즈
			at.setFile(ResizeWebp.resizeWebp(at.getFile()));
			
			//메타데이터에 꼬리표 넣기
		}
			
		
		

		
		
	}
}
