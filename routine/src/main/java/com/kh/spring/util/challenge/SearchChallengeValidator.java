package com.kh.spring.util.challenge;

import java.sql.Timestamp;

import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.util.common.Regexp;

public class SearchChallengeValidator {

	//챌린지 유효성 검사
	public static boolean searchChallenge(SearchChallenge sc) throws Exception{
		if(sc.getOrderby() ==null
				|| !sc.getOrderby().matches(Regexp.SC_ORDERBY)
				|| sc.getSearch() ==null
				|| !sc.getSearch().matches(Regexp.SC_SEARCH)
				|| sc.getSearchType() ==null
				|| !sc.getSearchType().matches(Regexp.SC_SEARCH_TYPE)
				|| sc.getStatus() ==null
				|| !sc.getStatus().matches(Regexp.SC_STATUS)
				|| sc.getPictureRequired() ==null
				|| !sc.getPictureRequired().matches(Regexp.CHAL_PICTURE_REQUIRED)
				|| sc.getReplyRequired() ==null
				|| !sc.getReplyRequired().matches(Regexp.CHAL_REPLY_REQUIRED)
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
		
		Timestamp sd = sc.getStartDate();
		Timestamp ed = sc.getEndDate();
		
		//날짜가 14530529면 null로 변환
		if (sd.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
		    sc.setStartDate(null);
		}
		if (ed.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
			sc.setEndDate(null);
		}
		
		if (sd.compareTo(ed) > 0) { //끝일이 시작일보다 빠르면 자리 교환
		    sc.setStartDate(ed);
		    sc.setEndDate(sd);
		}
		
		//categoryNo생략
		
		return true;
	}

}
