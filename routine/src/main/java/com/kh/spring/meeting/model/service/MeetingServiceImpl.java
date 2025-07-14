package com.kh.spring.meeting.model.service;

import com.kh.spring.meeting.model.dao.MeetingDao;
import com.kh.spring.meeting.model.vo.Meeting;
import com.kh.spring.meeting.model.vo.MeetingPart;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class MeetingServiceImpl implements MeetingService {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    @Autowired
    private MeetingDao meetingDao;
    
    @Override
    public int insertMeeting(Meeting meeting) {
        return meetingDao.insertMeeting(sqlSession, meeting);
    }
    
    @Override
    public int updateMeeting(Meeting meeting) {
        return meetingDao.updateMeeting(sqlSession, meeting);
    }
    
    @Override
    public int deleteMeeting(int meetingNo) {
        return meetingDao.deleteMeeting(sqlSession, meetingNo);
    }
    
    @Override
    public Meeting selectMeetingByNo(int meetingNo) {
        return meetingDao.selectMeetingByNo(sqlSession, meetingNo);
    }
    
    @Override
    public List<Meeting> selectMeetingsByUser(int userNo) {
        return meetingDao.selectMeetingsByUser(sqlSession, userNo);
    }
    
    @Override
    public List<Meeting> selectAllMeetings() {
        return meetingDao.selectAllMeetings(sqlSession);
    }
    
    @Override
    public int joinMeeting(int meetingNo, int userNo) {
        MeetingPart part = new MeetingPart();
        part.setMeetingNo(meetingNo);
        part.setUserNo(userNo);
        return meetingDao.insertMeetingPart(sqlSession, part);
    }
    
    @Override
    public int leaveMeeting(int meetingNo, int userNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingNo", meetingNo);
        params.put("userNo", userNo);
        return meetingDao.deleteMeetingPart(sqlSession, params);
    }
    
    @Override
    public List<MeetingPart> selectMeetingPartsByMeetingNo(int meetingNo) {
        return meetingDao.selectMeetingPartsByMeetingNo(sqlSession, meetingNo);
    }
    
    @Override
    public List<MeetingPart> selectMeetingPartsByUserNo(int userNo) {
        return meetingDao.selectMeetingPartsByUserNo(sqlSession, userNo);
    }
    
    @Override
    public int selectMeetingPartCount(int meetingNo) {
        return meetingDao.selectMeetingPartCount(sqlSession, meetingNo);
    }
    
    @Override
    public boolean isUserParticipant(int meetingNo, int userNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("meetingNo", meetingNo);
        params.put("userNo", userNo);
        MeetingPart part = meetingDao.selectMeetingPart(sqlSession, params);
        return part != null;
    }
}