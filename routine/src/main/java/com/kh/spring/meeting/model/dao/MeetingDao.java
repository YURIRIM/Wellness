package com.kh.spring.meeting.model.dao;

import com.kh.spring.meeting.model.vo.Meeting;
import com.kh.spring.meeting.model.vo.MeetingPart;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class MeetingDao {
    
    public int insertMeeting(SqlSessionTemplate sqlSession, Meeting meeting) {
        return sqlSession.insert("meetingMapper.insertMeeting", meeting);
    }
    
    public int updateMeeting(SqlSessionTemplate sqlSession, Meeting meeting) {
        return sqlSession.update("meetingMapper.updateMeeting", meeting);
    }
    
    public int deleteMeeting(SqlSessionTemplate sqlSession, int meetingNo) {
        return sqlSession.delete("meetingMapper.deleteMeeting", meetingNo);
    }
    
    public Meeting selectMeetingByNo(SqlSessionTemplate sqlSession, int meetingNo) {
        return sqlSession.selectOne("meetingMapper.selectMeetingByNo", meetingNo);
    }
    
    public List<Meeting> selectMeetingsByUser(SqlSessionTemplate sqlSession, int userNo) {
        return sqlSession.selectList("meetingMapper.selectMeetingsByUser", userNo);
    }
    
    public List<Meeting> selectAllMeetings(SqlSessionTemplate sqlSession) {
        return sqlSession.selectList("meetingMapper.selectAllMeetings");
    }
    
    public int insertMeetingPart(SqlSessionTemplate sqlSession, MeetingPart part) {
        return sqlSession.insert("meetingMapper.insertMeetingPart", part);
    }
    
    public int deleteMeetingPart(SqlSessionTemplate sqlSession, Map<String, Object> params) {
        return sqlSession.delete("meetingMapper.deleteMeetingPart", params);
    }
    
    public List<MeetingPart> selectMeetingPartsByMeetingNo(SqlSessionTemplate sqlSession, int meetingNo) {
        return sqlSession.selectList("meetingMapper.selectMeetingPartsByMeetingNo", meetingNo);
    }
    
    public List<MeetingPart> selectMeetingPartsByUserNo(SqlSessionTemplate sqlSession, int userNo) {
        return sqlSession.selectList("meetingMapper.selectMeetingPartsByUserNo", userNo);
    }
    
    public int selectMeetingPartCount(SqlSessionTemplate sqlSession, int meetingNo) {
        return sqlSession.selectOne("meetingMapper.selectMeetingPartCount", meetingNo);
    }
    
    public MeetingPart selectMeetingPart(SqlSessionTemplate sqlSession, Map<String, Object> params) {
        return sqlSession.selectOne("meetingMapper.selectMeetingPart", params);
    }
}