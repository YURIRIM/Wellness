package com.kh.spring.admin.model.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.kh.spring.notice.model.vo.Notice;
import com.kh.spring.user.model.vo.User;

@Mapper
public interface AdminDao {
    
    // ========== 공지사항 관리 ==========
    
    // 관리자용 - 전체 공지사항 조회
    List<Notice> selectNoticeList();
    
    // 관리자용 - 공지사항 상세 조회
    Notice selectNoticeByNum(int noticeNum);
    
    // 관리자용 - 공지사항 등록
    int insertNotice(Notice notice);
    
    // 관리자용 - 공지사항 수정
    int updateNotice(Notice notice);
    
    // 관리자용 - 공지사항 삭제
    int deleteNotice(int noticeNum);
    
    // 관리자용 - 전체 공지사항 개수 조회
    int selectNoticeCount();
    
    // 관리자용 - 최근 공지사항 조회
    List<Notice> selectRecentNotices(int limit);
    
    // ========== 회원 관리 ==========
    
    // 관리자용 - 전체 회원 조회
    List<User> selectUserList();
    
    // 관리자용 - 회원 상세 조회 (userNo 기준)
    User selectUserByNo(int userNo);
    
    // 관리자용 - 전체 회원 개수 조회
    int selectUserCount();
    
    // 관리자용 - 최근 가입 회원 조회
    List<User> selectRecentUsers(int limit);
    
    // ========== 통계 관련 ==========
    
    // 관리자용 - 일반 회원 수
    int selectGeneralUserCount();
    
    // 관리자용 - 관리자 수
    int selectAdminCount();
    
    // 관리자용 - 활성 회원 수
    int selectActiveUserCount();
    
    // 관리자용 - 비활성 회원 수
    int selectInactiveUserCount();
}