package com.kh.spring.qna.model.dao;

import java.util.List;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.spring.qna.model.vo.Qna;

@Repository
public class QnaDao {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    // 문의사항 등록
    public int insertQna(Qna qna) {
        return sqlSession.insert("qnaMapper.insertQna", qna);
    }
    
    // 특정 사용자의 문의사항 목록 조회
    public List<Qna> selectQnaListByUser(int userNo) {
        return sqlSession.selectList("qnaMapper.selectQnaListByUser", userNo);
    }
    
    // 문의사항 상세 조회
    public Qna selectQnaByNo(int userQnaNo) {
        return sqlSession.selectOne("qnaMapper.selectQnaByNo", userQnaNo);
    }
    
    // 전체 문의사항 목록 조회 (관리자용)
    public List<Qna> selectAllQnaList() {
        return sqlSession.selectList("qnaMapper.selectAllQnaList");
    }
    
    // 미답변 문의사항 목록 조회
    public List<Qna> selectUnansweredQnaList() {
        return sqlSession.selectList("qnaMapper.selectUnansweredQnaList");
    }
    
    // 답변완료 문의사항 목록 조회
    public List<Qna> selectAnsweredQnaList() {
        return sqlSession.selectList("qnaMapper.selectAnsweredQnaList");
    }
    
    // 전체 문의사항 개수 조회
    public int selectQnaCount() {
        return sqlSession.selectOne("qnaMapper.selectQnaCount");
    }
    
    // 미답변 문의사항 개수 조회
    public int selectUnansweredQnaCount() {
        return sqlSession.selectOne("qnaMapper.selectUnansweredQnaCount");
    }
    
    // 답변완료 문의사항 개수 조회
    public int selectAnsweredQnaCount() {
        return sqlSession.selectOne("qnaMapper.selectAnsweredQnaCount");
    }
    
    // 최근 문의사항 조회 (대시보드용)
    public List<Qna> selectRecentQnas(int limit) {
        return sqlSession.selectList("qnaMapper.selectRecentQnas", limit);
    }
    
    // 문의사항 답변 상태 변경
    public int updateQnaStatus(int userQnaNo, boolean status) {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("userQnaNo", userQnaNo);
        params.put("status", status ? 1 : 0);
        return sqlSession.update("qnaMapper.updateQnaStatus", params);
    }
    
    // 문의사항 수정
    public int updateQna(Qna qna) {
        return sqlSession.update("qnaMapper.updateQna", qna);
    }
    
    // 문의사항 삭제
    public int deleteQna(int userQnaNo) {
        return sqlSession.delete("qnaMapper.deleteQna", userQnaNo);
    }
    
    // 특정 사용자의 문의사항 개수 조회
    public int selectQnaCountByUser(int userNo) {
        return sqlSession.selectOne("qnaMapper.selectQnaCountByUser", userNo);
    }
}