package com.kh.spring.meeting.controller;

import com.kh.spring.meeting.model.service.MeetingService;
import com.kh.spring.meeting.model.vo.Meeting;
import com.kh.spring.meeting.model.vo.MeetingPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/meeting")
public class MeetingController {
    
    @Autowired
    private MeetingService meetingService;
    
    // 모임 목록 페이지
    @GetMapping("/list")
    public String meetingList(Model model) {
        List<Meeting> meetingList = meetingService.selectAllMeetings();
        model.addAttribute("meetingList", meetingList);
        return "meeting/meetingList";
    }
    
    // 모임 생성 페이지
    @GetMapping("/create")
    public String createMeetingForm() {
        return "meeting/createMeeting";
    }
    
    // 모임 생성 (생성 후 채팅방 이동)
    @PostMapping("/create")
    public String createMeeting(@ModelAttribute Meeting meeting, 
                               HttpSession session, 
                               RedirectAttributes redirectAttributes) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo != null) {
            meeting.setUserNo(userNo);
        }
        
        int result = meetingService.insertMeeting(meeting);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "모임이 생성되었습니다! 첫 번째 대화를 시작해보세요.");
            return "redirect:/chat/room/" + meeting.getMeetingNo();
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "모임 생성에 실패했습니다.");
            return "redirect:/meeting/create";
        }
    }
    
    // 모임 상세보기
    @GetMapping("/detail/{meetingNo}")
    public String meetingDetail(@PathVariable("meetingNo") int meetingNo, 
                               Model model, 
                               HttpSession session) {
        
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        List<MeetingPart> participants = meetingService.selectMeetingPartsByMeetingNo(meetingNo);
        int participantCount = meetingService.selectMeetingPartCount(meetingNo);
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        boolean isParticipant = false;
        if (userNo != null) {
            isParticipant = meetingService.isUserParticipant(meetingNo, userNo);
        }
        
        model.addAttribute("meeting", meeting);
        model.addAttribute("participants", participants);
        model.addAttribute("participantCount", participantCount);
        model.addAttribute("isParticipant", isParticipant);
        
        return "meeting/meetingDetail";
    }
    
    // 모임 수정 페이지
    @GetMapping("/edit/{meetingNo}")
    public String editMeetingForm(@PathVariable("meetingNo") int meetingNo, 
                                 Model model, 
                                 HttpSession session) {
        
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null || meeting.getUserNo() != userNo) {
            return "redirect:/meeting/detail/" + meetingNo;
        }
        
        model.addAttribute("meeting", meeting);
        return "meeting/editMeeting";
    }
    
    // 모임 수정 처리
    @PostMapping("/edit")
    public String editMeeting(@ModelAttribute Meeting meeting, 
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        Meeting originalMeeting = meetingService.selectMeetingByNo(meeting.getMeetingNo());
        
        if (userNo == null || originalMeeting.getUserNo() != userNo) {
            redirectAttributes.addFlashAttribute("errorMsg", "수정 권한이 없습니다.");
            return "redirect:/meeting/detail/" + meeting.getMeetingNo();
        }
        
        int result = meetingService.updateMeeting(meeting);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "모임이 성공적으로 수정되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "모임 수정에 실패했습니다.");
        }
        
        return "redirect:/meeting/detail/" + meeting.getMeetingNo();
    }
    
    // 모임 삭제
    @PostMapping("/delete/{meetingNo}")
    public String deleteMeeting(@PathVariable("meetingNo") int meetingNo, 
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        
        if (userNo == null || meeting.getUserNo() != userNo) {
            redirectAttributes.addFlashAttribute("errorMsg", "삭제 권한이 없습니다.");
            return "redirect:/meeting/detail/" + meetingNo;
        }
        
        int result = meetingService.deleteMeeting(meetingNo);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "모임이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "모임 삭제에 실패했습니다.");
        }
        
        return "redirect:/meeting/list";
    }
    
    // 모임 탈퇴
    @PostMapping("/leave/{meetingNo}")
    @ResponseBody
    public String leaveMeeting(@PathVariable("meetingNo") int meetingNo, 
                              HttpSession session) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "fail";
        }
        
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        if (meeting.getUserNo() == userNo) {
            return "owner";
        }
        
        int result = meetingService.leaveMeeting(meetingNo, userNo);
        return result > 0 ? "success" : "fail";
    }
    
    // 내가 생성한 모임 목록
    @GetMapping("/my")
    public String myMeetings(Model model, HttpSession session) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }
        
        List<Meeting> myMeetings = meetingService.selectMeetingsByUser(userNo);
        model.addAttribute("myMeetings", myMeetings);
        
        return "meeting/myMeetings";
    }
    
    // 내가 참가한 모임 목록
    @GetMapping("/joined")
    public String joinedMeetings(Model model, HttpSession session) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }
        
        List<MeetingPart> joinedMeetings = meetingService.selectMeetingPartsByUserNo(userNo);
        model.addAttribute("joinedMeetings", joinedMeetings);
        
        return "meeting/joinedMeetings";
    }
}