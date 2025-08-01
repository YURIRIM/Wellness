package com.kh.spring.answerqna.model.vo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AnswerQna {

    private int answerNo;      // 답변번호 
    private int userQnaNo;     // 문의사항번호
    private int userNo;        // 사용자번호 
    private String answerQna;  // 문의사항 답변
    private String adminName;  // 관리자이름
    private Date createdate;   // 작성일
}