package com.kh.spring.answerqna.model.service;

import com.kh.spring.answerqna.model.vo.AnswerQna;

public interface AnswerQnaService {
    
    // 답변 등록
    int insertAnswer(AnswerQna answerQna);
    
    // 문의사항 번호로 답변 조회
    AnswerQna selectAnswerByQnaNo(int userQnaNo);
    
    // 답변 번호로 답변 조회
    AnswerQna selectAnswerByNo(int answerNo);
    
    // 답변 삭제
    int deleteAnswer(int answerNo);
    
    // 문의사항 번호로 답변 삭제
    int deleteAnswerByQnaNo(int userQnaNo);
}