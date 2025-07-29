package com.kh.spring.common.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor	
@AllArgsConstructor
@Data
public class PageInfo {
	
	private int currentPage; //현재 페이지
	private int listCount; // 총 게시물 개수
	private int boardLimit; // 한 화면에 보여질 개수
	private int pageLimit; // 페이징바 하단에 표시될 페이징바 개수
	
	private int maxPage; //최대페이지 개수
	private int startPage; //페이징바 시작 수
	private int endPage;  // 페이장바 끝 수
	
	
	
}
     