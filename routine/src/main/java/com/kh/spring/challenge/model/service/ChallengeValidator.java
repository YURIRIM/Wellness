package com.kh.spring.challenge.model.service;

import com.kh.spring.challenge.model.vo.Challenge;
import com.kh.spring.common.Regexp;

public class ChallengeValidator {

	//챌린지 유효성 검사
	public static boolean challenge(Challenge chal) throws Exception{
		if(chal.getTitle() == null 
				|| !chal.getTitle().matches(Regexp.CHAL_TITLE)
				|| chal.getContent() == null
				|| !chal.getContent().matches(Regexp.CHAL_CONTENT)
				|| chal.getPictureRequired() ==null
				|| !chal.getPictureRequired().matches(Regexp.CHAL_PICTURE_REQUIRED)
				|| chal.getReplyRequired() ==null
				|| !chal.getReplyRequired().matches(Regexp.CHAL_REPLY_REQUIRED)
				
				
				) {
			return false;
		}
		
		
		
		return true;
	}

}
