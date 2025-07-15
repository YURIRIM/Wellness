package com.kh.spring.util.challenge;

import com.kh.spring.challenge.model.vo.ProfileRequest;
import com.kh.spring.util.common.Regexp;

public class ProfileValidator {
	
	//프로파일 유효성 검사
	public static boolean profile(ProfileRequest p) throws Exception{
		if(p.getBio() == null
				|| !p.getBio().matches(Regexp.PROFIEL_BIO)
				|| p.getIsOpen() == null
				|| !p.getIsOpen().matches(Regexp.PROFILE_IS_OPEN)
				|| p.getWatermarkType() == null
				|| !p.getWatermarkType().matches(Regexp.PROFILE_WATERMARK_TYPE)
				)return false;
		
		return true;
	}
}
