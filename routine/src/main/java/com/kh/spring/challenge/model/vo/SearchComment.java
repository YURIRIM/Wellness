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
public class SearchComment {
	private int chalNo;
	private int currentPage; //현재 페이지
	private final int showLimit = Regexp.COMMENT_SHOW_LIMIT; //한 페이지에서 보여줄 댓글 수
}
