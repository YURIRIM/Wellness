package com.kh.spring.util.challenge;

import java.util.regex.Pattern;

import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.util.common.Regexp;

public class ChallengeCommentValidator {

	//챌린지 인증댓글 유효성 검사
	public static boolean challengeComment(ChallengeCommentRequest ccr) throws Exception{
		Pattern p = Pattern.compile(Regexp.CHAL_COMMENT_REPLY, Pattern.DOTALL);
		if(ccr.getReply() == null
				|| !p.matcher(ccr.getReply()).matches()
				) return false;
		return true;
	}

}
