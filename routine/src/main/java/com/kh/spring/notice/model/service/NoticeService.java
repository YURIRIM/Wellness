package com.kh.spring.notice.model.service;

import java.util.List;
import com.kh.spring.notice.model.vo.Notice;

public interface NoticeService {
    
    // 사용자용 - 전체 공지사항 조회
    List<Notice> selectNoticeList();
    
    // 사용자용 - 공지사항 상세 조회
    Notice selectNoticeByNum(int noticeNum);
    
    // 사용자용 - 전체 공지사항 개수 조회
    int selectNoticeCount();
}