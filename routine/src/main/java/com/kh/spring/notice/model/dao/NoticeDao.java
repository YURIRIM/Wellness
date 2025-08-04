package com.kh.spring.notice.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.kh.spring.notice.model.vo.Notice;

@Mapper
public interface NoticeDao {
    
    // 사용자용 - 전체 공지사항 조회
    List<Notice> selectNoticeList();
    
    // 사용자용 - 공지사항 상세 조회
    Notice selectNoticeByNum(int noticeNum);
    
    // 사용자용 - 전체 공지사항 개수 조회
    int selectNoticeCount();
}