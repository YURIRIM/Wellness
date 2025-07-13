package com.kh.spring.challenge.model.service;

import java.util.Base64;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ProfileDao;
import com.kh.spring.challenge.model.vo.Profile;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.common.JwtUtil;

import jakarta.servlet.http.HttpSession;


@Service
public class ProfileServiceImpl implements ProfileService{
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private ProfileDao dao;
	@Autowired
	private JwtUtil jwt;
	
	@Override
	public String selectMyProfile(HttpSession session) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		
		//로그인조차 하지 않았다니 어흐흑 마이깟
		if(loginUser==null) return "2";
		
		Profile userProfile = dao.selectMyProfile(sqlSession,loginUser.getUserNo());
		
		//신루틴의 긍휼한 축복을 받고도 프로필을 생성하지 않은 불경한 자
		if(userProfile==null) return "3";
		
		//프로필사진 base64 인코딩
		byte[] picture = userProfile.getPicture();
		if(picture!=null) {
			userProfile.setPictureBase64(Base64.getEncoder().encodeToString(picture));
		}
		
		//프로필이 있으면 jwt토큰에 넣어 보내기
		return jwt.userProfileToken(userProfile);
	}

}
