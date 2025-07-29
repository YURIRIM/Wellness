package com.kh.spring.challenge.model.vo;

import com.kh.spring.util.common.Regexp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SearchMyChallenge {
	private int userNo; //해당 회원
	private int currentPage; //현재 페이지
	private final int showLimit = Regexp.CHAL_SHOW_LIMIT; //한 페이지에서 보여줄 게시글 수
	private String searchType;//검색 대상(O:내가 생성한 챌린지 J:내가 참여한 챌린지)
}