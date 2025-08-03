package com.kh.spring.answerqna.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.kh.spring.answerqna.model.vo.AnswerQna;

@Repository
public class AnswerQnaDao {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    // 답변 등록
    public int insertAnswer(AnswerQna answerQna) {
        return sqlSession.insert("answerQnaMapper.insertAnswer", answerQna);
    }
    
    // 문의사항 번호로 답변 조회
    public AnswerQna selectAnswerByQnaNo(int userQnaNo) {
        return sqlSession.selectOne("answerQnaMapper.selectAnswerByQnaNo", userQnaNo);
    }
    
    // 답변 번호로 답변 조회
    public AnswerQna selectAnswerByNo(int answerNo) {
        return sqlSession.selectOne("answerQnaMapper.selectAnswerByNo", answerNo);
    }
    
    // 답변 삭제
    public int deleteAnswer(int answerNo) {
        return sqlSession.delete("answerQnaMapper.deleteAnswer", answerNo);
    }
    
    // 문의사항 번호로 답변 삭제
    public int deleteAnswerByQnaNo(int userQnaNo) {
        return sqlSession.delete("answerQnaMapper.deleteAnswerByQnaNo", userQnaNo);
    }
}