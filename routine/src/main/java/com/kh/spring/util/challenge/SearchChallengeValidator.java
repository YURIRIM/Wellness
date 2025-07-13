package com.kh.spring.util.challenge;

import java.sql.Timestamp;

import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.util.common.Regexp;

public class SearchChallengeValidator {

	//챌린지 유효성 검사
	public static boolean searchChallenge(SearchChallenge sc){
		if(sc.getOrderby() ==null) sc.setOrderby("");
		if(sc.getSearch() ==null) sc.setSearch("");
		if(sc.getSearchType() ==null) sc.setSearchType("");
		if(sc.getStatus() ==null) sc.setStatus("");
		if(sc.getPictureRequired() ==null) sc.setPictureRequired("");
		if(sc.getReplyRequired() ==null) sc.setReplyRequired("");
		
		if(!sc.getOrderby().matches(Regexp.SC_ORDERBY)
				|| !sc.getSearch().matches(Regexp.SC_SEARCH)
				|| !sc.getSearchType().matches(Regexp.SC_SEARCH_TYPE)
				|| !sc.getStatus().matches(Regexp.SC_STATUS)
				|| !sc.getPictureRequired().matches(Regexp.SC_PICTURE_REQUIRED)
				|| !sc.getReplyRequired().matches(Regexp.SC_REPLY_REQUIRED)
				)return false;
		
		int verifyCycle = sc.getVerifyCycle();
		if(verifyCycle<0 || verifyCycle>221) return false;
		else if(verifyCycle>127) {
			switch(verifyCycle) {
			case 201:
			case 202:
			case 203:
			case 211:
			case 212:
			case 221:
				break;
			default : return false;
			}
		}
		
		if(sc.getCurrentPage()<0) return false;
		
		Timestamp sd1 = sc.getStartDate1();
		Timestamp sd2 = sc.getStartDate1();
		Timestamp ed1 = sc.getEndDate1();
		Timestamp ed2 = sc.getEndDate1();
		
		//날짜가 14530529면 null로 변환
		if (sd1.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
		    sc.setStartDate1(null);
		}
		if (sd2.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
			sc.setStartDate2(null);
		}
		if (ed1.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
			sc.setEndDate1(null);
		}
		if (ed2.toLocalDateTime().toLocalDate().equals(Regexp.DOWNFALL)) {
			sc.setEndDate2(null);
		}
		
		if (sd1.compareTo(sd2) > 0) { //시작일1이 시작일2 보다 느리면 자리 바꾸기
		    sc.setStartDate1(sd1);
		    sc.setStartDate2(sd2);
		}
		if (ed1.compareTo(ed2) > 0) {
			sc.setEndDate1(ed1);
			sc.setEndDate2(ed2);
		}
		
		//categoryNo생략
		
		return true;
	}

}
