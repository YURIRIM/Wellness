package com.kh.spring.util.challenge;

import com.kh.spring.challenge.model.vo.ChallengeCommentRequest;
import com.kh.spring.util.common.Regexp;

public class ChallengeCommentValidator {

	//챌린지 인증댓글 유효성 검사
	public static boolean challengeComment(ChallengeCommentRequest ccr) throws Exception{
		if(ccr.getReply() == null
				|| !ccr.getReply().matches(Regexp.CHAL_COMMENT_REPLY)
				)return false;

		return true;
	}

}
