package com.kh.spring.challenge.model.service;

import java.util.Base64;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ProfileDao;
import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.challenge.model.vo.ProfileResponse;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.challenge.ProfileValidator;
import com.kh.spring.util.common.BinaryAndBase64;

import jakarta.servlet.http.HttpSession;


@Service
public class ProfileServiceImpl implements ProfileService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ProfileDao dao;
	
	@Override
	public int selectMyProfile(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		//로그인조차 하지 않았다니 어흐흑 마이깟
		if(loginUser==null) return 2;
		
		ProfileResponse userProfile = dao.selectMyProfile(sqlSession,loginUser.getUserNo());
		
		//신루틴의 긍휼한 축복을 받고도 프로필을 생성하지 않은 불경한 자
		if(userProfile==null) return 3;
		
		//사진은 base64형식으로 고쳐서 보내기. 바이너리는 브라우저에서 못 읽는다.
		if(userProfile.getPicture()!=null) {
			userProfile.setPictureBase64(Base64.getEncoder().encodeToString(userProfile.getPicture()));
			userProfile.setPicture(null);
		}
		if(userProfile.getWatermark()!=null) {
			userProfile.setWatermarkBase64(Base64.getEncoder().encodeToString(userProfile.getWatermark()));
			userProfile.setWatermark(null);
		}
		
		//프로필이 있으면 세션에 넣기
		session.setAttribute("myProfile", userProfile);
		return 1;
	}
	
	//프로필 생성
	@Override
	public void insertMyProfile(HttpSession session, ProfileRequest p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		p.setUserNo(loginUser.getUserNo());
		
		//유효성 검사
		if(!ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = BinaryAndBase64.base64ToBinary(p.getPictureBase64());
		
		if (picture !=null) {
			//프로필 사진 있으면 picture에 넣기
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		//워터마크 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] watermark = BinaryAndBase64.base64ToBinary(p.getWatermarkBase64());
		
		if (watermark!=null) {
			//워터마크 사진 있으면 watermark에 넣기
			p.setWatermark(watermark);
			p.setWatermarkBase64(null);
		}else {
			//워터마크가 없는데 개인 워터마크 쓸거에용 방지
			if(p.getWatermarkType().equals("C")) p.setWatermarkType("D");
		}
		
		int result = dao.insertMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("새로운 프로파일 생성 실패!");
		
		//세션 업데이트
		selectMyProfile(session);
	}
	
	
	//프로필 업데이트
	@Override
	public void updateMyProfile(HttpSession session, ProfileRequest p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		p.setUserNo(loginUser.getUserNo());
		
		//유효성 검사
		if(!ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = BinaryAndBase64.base64ToBinary(p.getPictureBase64());
		
		if (picture !=null) {
			//프로필 사진 있으면 picture에 넣기
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		//워터마크 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] watermark = BinaryAndBase64.base64ToBinary(p.getWatermarkBase64());
		
		if (watermark!=null) {
			//워터마크 사진 있으면 watermark에 넣기
			p.setWatermark(watermark);
			p.setWatermarkBase64(null);
		}else {
			//워터마크가 없는데 개인 워터마크 쓸거에용 방지
			if(p.getWatermarkType().equals("C")) p.setWatermarkType("D");
		}
		
		int result = dao.updateMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("프로파일 갱신 실패!");
		
		//세션 업데이트
		selectMyProfile(session);
	}
	


}
