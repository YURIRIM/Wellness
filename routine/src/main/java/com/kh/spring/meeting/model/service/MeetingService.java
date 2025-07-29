package com.kh.spring.meeting.model.service;

import com.kh.spring.meeting.model.vo.Meeting;
import com.kh.spring.meeting.model.vo.MeetingPart;
import java.util.List;

public interface MeetingService {
    
    int insertMeeting(Meeting meeting);
    int updateMeeting(Meeting meeting);
    int deleteMeeting(int meetingNo);
    Meeting selectMeetingByNo(int meetingNo);
    List<Meeting> selectMeetingsByUser(int userNo);
    List<Meeting> selectAllMeetings();
    
    int joinMeeting(int meetingNo, int userNo);
    int leaveMeeting(int meetingNo, int userNo);
    List<MeetingPart> selectMeetingPartsByMeetingNo(int meetingNo);
    List<MeetingPart> selectMeetingPartsByUserNo(int userNo);
    int selectMeetingPartCount(int meetingNo);
    boolean isUserParticipant(int meetingNo, int userNo);
}