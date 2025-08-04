package com.kh.spring.qna.model.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kh.spring.qna.model.dao.QnaDao;
import com.kh.spring.qna.model.vo.Qna;

@Service
@Transactional
public class QnaServiceImpl implements QnaService {
    
    @Autowired
    private QnaDao qnaDao;
    
    @Override
    public int insertQna(Qna qna) {
        return qnaDao.insertQna(qna);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Qna> selectQnaListByUser(int userNo) {
        return qnaDao.selectQnaListByUser(userNo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Qna selectQnaByNo(int userQnaNo) {
        return qnaDao.selectQnaByNo(userQnaNo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Qna> selectAllQnaList() {
        return qnaDao.selectAllQnaList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Qna> selectUnansweredQnaList() {
        return qnaDao.selectUnansweredQnaList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Qna> selectAnsweredQnaList() {
        return qnaDao.selectAnsweredQnaList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int selectQnaCount() {
        return qnaDao.selectQnaCount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int selectUnansweredQnaCount() {
        return qnaDao.selectUnansweredQnaCount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public int selectAnsweredQnaCount() {
        return qnaDao.selectAnsweredQnaCount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Qna> selectRecentQnas(int limit) {
        return qnaDao.selectRecentQnas(limit);
    }
    
    @Override
    public int updateQnaStatus(int userQnaNo, boolean status) {
        return qnaDao.updateQnaStatus(userQnaNo, status);
    }
    
    @Override
    public int deleteQna(int userQnaNo) {
        return qnaDao.deleteQna(userQnaNo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int selectQnaCountByUser(int userNo) {
        return qnaDao.selectQnaCountByUser(userNo);
    }
    
    @Override
    public int updateQna(Qna qna) {
        return qnaDao.updateQna(qna);
    }
}