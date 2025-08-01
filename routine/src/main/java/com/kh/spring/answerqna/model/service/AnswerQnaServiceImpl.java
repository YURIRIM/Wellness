package com.kh.spring.answerqna.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kh.spring.answerqna.model.dao.AnswerQnaDao;
import com.kh.spring.answerqna.model.vo.AnswerQna;

@Service
public class AnswerQnaServiceImpl implements AnswerQnaService {
    
    @Autowired
    private AnswerQnaDao answerQnaDao;
    
    @Override
    public int insertAnswer(AnswerQna answerQna) {
        return answerQnaDao.insertAnswer(answerQna);
    }
    
    @Override
    public AnswerQna selectAnswerByQnaNo(int userQnaNo) {
        return answerQnaDao.selectAnswerByQnaNo(userQnaNo);
    }
    
    @Override
    public AnswerQna selectAnswerByNo(int answerNo) {
        return answerQnaDao.selectAnswerByNo(answerNo);
    }
    
    @Override
    public int deleteAnswer(int answerNo) {
        return answerQnaDao.deleteAnswer(answerNo);
    }
    
    @Override
    public int deleteAnswerByQnaNo(int userQnaNo) {
        return answerQnaDao.deleteAnswerByQnaNo(userQnaNo);
    }
}