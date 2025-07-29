package com.kh.spring.common.template;

import com.kh.spring.common.model.vo.PageInfo;

public class Pagination {
	
	public static PageInfo getPageInfo(int listCount,
									   int currentPage,
									   int pageLimit,
									   int boardLimit) {
		
		int maxPage =(int)Math.ceil((double)listCount/boardLimit);
		int startPage=(currentPage-1)/pageLimit*pageLimit+1;
		int endPage=startPage + pageLimit -1;
		
		//endPage 가 maxPage 보다 클 때 처리하기
		if(maxPage<endPage) { 
			endPage=maxPage;
		}
		
		//처리된 페이지 정보 객체에 담아서 반환
		PageInfo pi = new PageInfo(currentPage,listCount,boardLimit,pageLimit,maxPage,startPage,endPage);
		System.out.println(pi);
		
		
		return pi;
	}
	
	//VO : PageInfo 에서 가져와야함
	//페이징 처리 하기 위한 메소드 
	
}
