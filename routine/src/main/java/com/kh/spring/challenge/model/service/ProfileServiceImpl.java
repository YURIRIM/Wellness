package com.kh.spring.challenge.model.service;

import java.util.Base64;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.spring.challenge.model.dao.ProfileDao;
import com.kh.spring.challenge.model.vo.Profile;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.util.attachment.Exiftool;
import com.kh.spring.util.attachment.ResizeWebp;
import com.kh.spring.util.challenge.ProfileValidator;
import com.kh.spring.util.common.BinaryAndBase64;
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
	
	@Override
	public void insertMyProfile(HttpSession session, Profile p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		p.setUserNo(loginUser.getUserNo());
		
		System.out.println("프로필 추가됨 : "+p);
		
		//유효성 검사
		if(!ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = BinaryAndBase64.Base64ToBinary(p.getPictureBase64());
		
		if (picture !=null) {
			//프로필 사진 있으면 picture에 넣기
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		//워터마크 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] watermark = BinaryAndBase64.Base64ToBinary(p.getWatermarkBase64());
		
		if (watermark!=null) {
			//워터마크 사진 있으면 watermark에 넣기
			p.setWatermark(watermark);
			p.setWatermarkBase64(null);
		}
		
		int result = dao.insertMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("새로운 프로파일 생성 실패!");
	}
	
	@Override
	public void updateMyProfile(HttpSession session, Profile p) throws Exception {
		User loginUser = (User)session.getAttribute("loginUser");
		if(loginUser==null) throw new Exception("로그인은 하고 와라 애송이");
		
		//유효성 검사
		if(ProfileValidator.profile(p)) throw new Exception("유효성 오류");
		
		p.setUserNo(loginUser.getUserNo());
		
		//프로필 사진 있으면 Base64 디코딩, 리사이즈 및 소독
		byte[] picture = null;
		if (p.getPictureBase64() != null && !p.getPictureBase64().equals("")) {
			picture = Base64.getDecoder().decode(p.getPictureBase64());
			picture = ResizeWebp.resizeWebp(picture);
			if (Exiftool.EXIFTOOL) {// exiftool이 있어요!
				picture = Exiftool.sanitizeMetadata(picture);
			}
			p.setPicture(picture);
			p.setPictureBase64(null);
		}
		
		int result = dao.updateMyProfile(sqlSession,p);
		
		if(!(result>0)) throw new Exception("프로파일 갱신 실패!");
	}
	


}
