package com.kh.spring.notice.model.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kh.spring.notice.model.dao.NoticeDao;
import com.kh.spring.notice.model.vo.Notice;

@Service
public class NoticeServiceImpl implements NoticeService {
    
    @Autowired
    private NoticeDao noticeDao;
    
    @Override
    public List<Notice> selectNoticeList() {
        return noticeDao.selectNoticeList();
    }
    
    @Override
    public Notice selectNoticeByNum(int noticeNum) {
        return noticeDao.selectNoticeByNum(noticeNum);
    }
    
    @Override
    public int selectNoticeCount() {
        return noticeDao.selectNoticeCount();
    }
}