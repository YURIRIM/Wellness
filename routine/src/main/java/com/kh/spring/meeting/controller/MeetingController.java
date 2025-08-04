package com.kh.spring.meeting.controller;

import com.kh.spring.meeting.model.service.MeetingService;
import com.kh.spring.meeting.model.vo.Meeting;
import com.kh.spring.meeting.model.vo.MeetingPart;
import com.kh.spring.user.model.vo.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${api.kakao.js.key}") 
    private String kakaoJsApiKey;

    // 모임 목록 페이지
    @GetMapping("/list")
    public String meetingList(Model model) {
        List<Meeting> meetingList = meetingService.selectAllMeetings();
        model.addAttribute("meetingList", meetingList);
        return "meeting/meetingList";
    }
    
    // 모임 생성 페이지
    @GetMapping("/create")
    public String createMeetingForm(Model model) { 
        model.addAttribute("kakaoJsApiKey", kakaoJsApiKey); 
        return "meeting/createMeeting";
    }
    
    // 모임 생성 (생성 후 채팅방 이동)
    @PostMapping("/create")
    public String createMeeting(@ModelAttribute Meeting meeting, 
                               HttpSession session, 
                               RedirectAttributes redirectAttributes) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        
        if (loginUser == null) { 
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        int userNo = loginUser.getUserNo(); 

        meeting.setUserNo(userNo);
        
        try {
            int result = meetingService.insertMeeting(meeting);
            
            if (result > 0) {
                redirectAttributes.addFlashAttribute("alertMsg", "모임이 생성되었습니다! 첫 번째 대화를 시작해보세요.");
                return "redirect:/chat/room/" + meeting.getMeetingNo();
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "모임 생성에 실패했습니다.");
                return "redirect:/meeting/create";
            }
        } catch (Exception e) {
            System.err.println("모임 생성 오류: " + e.getMessage());
            System.err.println("사용자 번호: " + userNo);
            e.printStackTrace(); 
            
            redirectAttributes.addFlashAttribute("errorMsg", "모임 생성 중 오류가 발생했습니다. 로그인 상태를 확인해주세요.");
            return "redirect:/meeting/create";
        }
    }
    
    // 모임 상세보기
    @GetMapping("/detail/{meetingNo}")
    public String meetingDetail(@PathVariable("meetingNo") int meetingNo, 
                               Model model, 
                               HttpSession session) {
        
        model.addAttribute("kakaoJsApiKey", kakaoJsApiKey);

        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        List<MeetingPart> participants = meetingService.selectMeetingPartsByMeetingNo(meetingNo);
        int participantCount = meetingService.selectMeetingPartCount(meetingNo);
        
        User loginUser = (User) session.getAttribute("loginUser");
        
        int currentUserNo = 0; 
        boolean isLoggedIn = (loginUser != null); 
        
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo(); 
        }
        
        boolean isParticipant = false;
        if (isLoggedIn) { 
            isParticipant = meetingService.isUserParticipant(meetingNo, currentUserNo);
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
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        
        model.addAttribute("kakaoJsApiKey", kakaoJsApiKey);

        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        
        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0; 
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }

        if (!isLoggedIn) { 
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login"; 
        }
        
        if (meeting == null || meeting.getUserNo() != currentUserNo) {
            redirectAttributes.addFlashAttribute("errorMsg", "모임을 수정할 권한이 없습니다. 본인이 생성한 모임인지 확인해주세요.");
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
        
        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0;
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }
        
        Meeting originalMeeting = meetingService.selectMeetingByNo(meeting.getMeetingNo());
        
        if (!isLoggedIn) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        if (originalMeeting == null || originalMeeting.getUserNo() != currentUserNo) {
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
        
        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0;
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }
        
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        
        if (!isLoggedIn) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        if (meeting == null || meeting.getUserNo() != currentUserNo) {
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
        
        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0;
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }

        if (!isLoggedIn) { 
            return "fail"; 
        }
        
        Meeting meeting = meetingService.selectMeetingByNo(meetingNo);
        if (meeting != null && meeting.getUserNo() == currentUserNo) {
            return "owner"; 
        }
        
        int result = meetingService.leaveMeeting(meetingNo, currentUserNo);
        return result > 0 ? "success" : "fail";
    }
    
    // 내가 생성한 모임 목록
    @GetMapping("/my")
    public String myMeetings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0;
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }

        if (!isLoggedIn) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }

        List<Meeting> myMeetings = meetingService.selectMeetingsByUser(currentUserNo);
        model.addAttribute("myMeetings", myMeetings);

        return "meeting/myMeetings";
    }

    // 내가 참가한 모임 목록
    @GetMapping("/joined")
    public String joinedMeetings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        User loginUser = (User) session.getAttribute("loginUser");
        int currentUserNo = 0;
        boolean isLoggedIn = (loginUser != null);
        if (isLoggedIn) {
            currentUserNo = loginUser.getUserNo();
        }

        if (!isLoggedIn) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        List<Meeting> joinedMeetings = meetingService.selectMeetingPartsByUserNo(currentUserNo); 
        model.addAttribute("joinedMeetings", joinedMeetings);

        return "meeting/joinedMeetings";
    }
    
    
    
    
    
    
    
}