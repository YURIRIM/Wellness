package com.kh.spring.util.challenge;

import java.time.LocalDate;

import com.kh.spring.challenge.model.vo.SearchChallenge;
import com.kh.spring.challenge.model.vo.SearchMyChallenge;
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
		
		LocalDate sd1 = sc.getStartDate1();
		LocalDate sd2 = sc.getStartDate2();
		LocalDate ed1 = sc.getEndDate1();
		LocalDate ed2 = sc.getEndDate2();
		
		if (sd1!=null && sd2!=null && sd1.compareTo(sd2) > 0) {
			//시작일1이 시작일2 보다 느리면 자리 바꾸기
		    sc.setStartDate1(sd2);
		    sc.setStartDate2(sd1);
		}
		if (ed1!=null && ed2!=null && ed1.compareTo(ed2) > 0) {
			sc.setEndDate1(ed2);
			sc.setEndDate2(ed1);
		}
		
		//categoryNo생략
		
		return true;
	}
	
	
	//챌린지 유효성 검사
	public static boolean searchMyChallenge(SearchMyChallenge smc){
		if(smc.getSearchType()==null
				|| !smc.getSearchType().matches(Regexp.SEARCH_MY_CHAL)
				)return false;
		if(smc.getCurrentPage()<0) return false;
		
		return true;
	}
}
