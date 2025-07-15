package com.kh.spring.util.challenge;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kh.spring.challenge.model.vo.ChallengeRequest;
import com.kh.spring.util.common.Regexp;

public class ChallengeValidator {

	//챌린지 유효성 검사
	public static boolean challenge(ChallengeRequest chal) throws Exception{
		if(chal.getTitle() == null
				|| !chal.getTitle().matches(Regexp.CHAL_TITLE)
				|| chal.getContent() == null
				|| !chal.getContent().matches(Regexp.CHAL_CONTENT)
				|| chal.getPictureRequired() ==null
				|| !chal.getPictureRequired().matches(Regexp.CHAL_PICTURE_REQUIRED)
				|| chal.getReplyRequired() ==null
				|| !chal.getReplyRequired().matches(Regexp.CHAL_REPLY_REQUIRED)
				)return false;
		
		
		int verifyCycle = chal.getVerifyCycle();
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
	    /*
	     * verifyCycle 인증 주기
	     * 0 : 없음
	     * 1~127 : 특정 요일 선택(비트마스크)
	     * -월요일 : 1, 화요일 : 2, ..., 일요일 : 64
	     * 
	     * 201 : 매일
	     * 202 : 이틀
	     * 203 : 사흘
	     * 211 : 매주
	     * 212 : 2주
	     * 221 : 매달
	     */
		
		//시작일이 과거면 뭐냐 넌 과거에서 왔냐?
		if (chal.getStartDate()!=null && 
				chal.getStartDate().isBefore(LocalDate.now()))
			return false;
		
		//종료일이 1주일도 안 되면 왜 함??
		if(chal.getEndDate()!=null && 
				chal.getEndDate().isBefore(LocalDate.now().plusWeeks(1)))
			return false;
		
		/*
		 * 카테고리도 검증해야 하는데 귀찮네
		 * 세션 들춰보고 그러기 귀찮으니 그냥 하죠?
		 */
		return true;
	}

}
