package com.kh.spring.qna.model.service;

import java.util.List;
import com.kh.spring.qna.model.vo.Qna;

public interface QnaService {
    
    // 문의사항 등록
    int insertQna(Qna qna);
    
    // 특정 사용자의 문의사항 목록 조회
    List<Qna> selectQnaListByUser(int userNo);
    
    // 문의사항 상세 조회
    Qna selectQnaByNo(int userQnaNo);
    
    // 전체 문의사항 목록 조회 (관리자용)
    List<Qna> selectAllQnaList();
    
    // 미답변 문의사항 목록 조회
    List<Qna> selectUnansweredQnaList();
    
    // 답변완료 문의사항 목록 조회
    List<Qna> selectAnsweredQnaList();
    
    // 전체 문의사항 개수 조회
    int selectQnaCount();
    
    // 미답변 문의사항 개수 조회
    int selectUnansweredQnaCount();
    
    // 답변완료 문의사항 개수 조회
    int selectAnsweredQnaCount();
    
    // 최근 문의사항 조회 (대시보드용)
    List<Qna> selectRecentQnas(int limit);
    
    // 문의사항 답변 상태 변경
    int updateQnaStatus(int userQnaNo, boolean status);
    
    // 문의사항 삭제
    int deleteQna(int userQnaNo);
    
    // 특정 사용자의 문의사항 개수 조회
    int selectQnaCountByUser(int userNo);
    
    // 문의사항 수정
    int updateQna(Qna qna);
}