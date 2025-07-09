package com.kh.spring.challenge.model.vo;

import java.sql.Timestamp;

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
	private String orderby; //정렬 방식
	
	private int categoryNo;
	private String title;
	private String content;
	private int verifyCycle;
    private Timestamp startDate;
    private Timestamp endDate;
    private String status; //Y:활성화 N:종료됨 D:삭제됨
    private String pictureRequired; //I:필수(도용 불가) Y:필수 O:선택 N:불가
    private String replyRequired; //Y:필수 O:선택 N:불가
    
    private String nick; //member
}
