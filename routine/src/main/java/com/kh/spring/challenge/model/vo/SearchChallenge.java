package com.kh.spring.challenge.model.vo;

import java.time.LocalDate;

import com.kh.spring.util.common.Regexp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchChallenge {
	private int currentPage; //현재 페이지
	private final int showLimit = Regexp.CHAL_SHOW_LIMIT; //한 페이지에서 보여줄 게시글 수
	private String orderby; //정렬 방식. '빈문자열':최신순, P:참여인원 많은 순, S:성공률 순
	private String search; //검색어
	private String searchType;//검색 대상(T:제목 C:내용 N:닉네임 '빈문자열':전부)
	
	private int categoryNo;
	private int verifyCycle;
	private LocalDate startDate1;
    private LocalDate startDate2;
    private LocalDate endDate1;
    private LocalDate endDate2;
    private String status; //Y:활성화 N:종료됨 '빈문자열':선택안함
    private String pictureRequired; //I:필수(도용 불가) Y:필수 O:선택 N:불가 '빈문자열':선택안함
    private String replyRequired; //Y:필수 O:선택 N:불가 '빈문자열':선택안함
}
