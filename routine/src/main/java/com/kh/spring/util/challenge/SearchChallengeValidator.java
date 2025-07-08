package com.kh.spring.util.challenge;

import java.sql.Timestamp;

import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.util.common.Regexp;

public class SearchChallengeValidator {

	//챌린지 유효성 검사
	public static boolean searchChallenge(SearchChallenge sc) throws Exception{
		if(sc.getTitle() == null 
				|| !sc.getTitle().matches(Regexp.CHAL_TITLE)
				|| sc.getContent() == null
				|| !sc.getContent().matches(Regexp.CHAL_CONTENT)
				|| sc.getPictureRequired() ==null
				|| !sc.getPictureRequired().matches(Regexp.CHAL_PICTURE_REQUIRED)
				|| sc.getReplyRequired() ==null
				|| !sc.getReplyRequired().matches(Regexp.CHAL_REPLY_REQUIRED)
				|| sc.getStatus() == null
				|| !sc.getStatus().matches(Regexp.SC_STATUS)
				)return false;
		
		if(sc.getVerifyCycle()<0
				|| sc.getVerifyCycle()>221) return false;
		switch(sc.getVerifyCycle()) {
		case 201:
		case 202:
		case 203:
		case 211:
		case 212:
		case 221:
			break;
		default : return false;
		}
		
		if(sc.getCurrentPage()<0) return false;
		
		Timestamp a = sc.getStartDate();
		Timestamp b = sc.getEndDate();
		if (a.compareTo(b) > 0) { //끝일이 시작일보다 빠르면 자리 교환
		    Timestamp temp = a;
		    a = b;
		    b = temp;
		}
		
		//categoryNo생략
		
		return true;
	}

}
