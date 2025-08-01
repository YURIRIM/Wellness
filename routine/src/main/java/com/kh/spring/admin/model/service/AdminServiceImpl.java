package com.kh.spring.admin.model.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kh.spring.admin.model.dao.AdminDao;
import com.kh.spring.notice.model.vo.Notice;
import com.kh.spring.user.model.vo.User;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private AdminDao adminDao;
    
    // ========== 공지사항 관리 ==========
    
    @Override
    public List<Notice> selectNoticeList() {
        return adminDao.selectNoticeList();
    }
    
    @Override
    public Notice selectNoticeByNum(int noticeNum) {
        return adminDao.selectNoticeByNum(noticeNum);
    }
    
    @Override
    public int insertNotice(Notice notice) {
        return adminDao.insertNotice(notice);
    }
    
    @Override
    public int updateNotice(Notice notice) {
        return adminDao.updateNotice(notice);
    }
    
    @Override
    public int deleteNotice(int noticeNum) {
        return adminDao.deleteNotice(noticeNum);
    }
    
    @Override
    public int selectNoticeCount() {
        return adminDao.selectNoticeCount();
    }
    
    @Override
    public List<Notice> selectRecentNotices(int limit) {
        return adminDao.selectRecentNotices(limit);
    }
    
    // ========== 회원 관리 ==========
    
    @Override
    public List<User> selectUserList() {
        return adminDao.selectUserList();
    }
    
    @Override
    public int selectUserCount() {
        return adminDao.selectUserCount();
    }
    
    @Override
    public List<User> selectRecentUsers(int limit) {
        return adminDao.selectRecentUsers(limit);
    }
    
    // ========== 통계 관련 ==========
    
    @Override
    public int selectGeneralUserCount() {
        return adminDao.selectGeneralUserCount();
    }
    
    @Override
    public int selectAdminCount() {
        return adminDao.selectAdminCount();
    }
    
    @Override
    public int selectActiveUserCount() {
        return adminDao.selectActiveUserCount();
    }
    
    @Override
    public int selectInactiveUserCount() {
        return adminDao.selectInactiveUserCount();
    }

	@Override
	public User selectUserByNo(int userNo) {
		return adminDao.selectUserByNo(userNo);
	}
}