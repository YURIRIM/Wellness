package com.kh.spring.chat.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.chat.model.service.ChatMessageService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.meeting.model.service.MeetingService;
import com.kh.spring.user.model.vo.User; // User VO 임포트 추가!

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatMessageController {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private MeetingService meetingService;
    
    @GetMapping("/room/{meetingNo}")
    public String chatRoom(@PathVariable("meetingNo") int meetingNo, 
                          Model model, 
                          HttpSession session,
                          RedirectAttributes redirectAttributes) { 
        
        User loginUser = (User) session.getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login"; 
        }
        
        int userNo = loginUser.getUserNo();
        
        if (!meetingService.isUserParticipant(meetingNo, userNo)) {
            meetingService.joinMeeting(meetingNo, userNo);
        }
        
        List<ChatMessage> messages = chatMessageService.selectAllChatMessages(meetingNo);
        
        model.addAttribute("messages", messages);
        model.addAttribute("meetingNo", meetingNo);
        model.addAttribute("userNick", loginUser.getNick()); 
        
        return "chat/chatRoom";
    }
    
    @PostMapping("/read/{meetingNo}")
    @ResponseBody
    public Map<String, Object> markAsRead(@PathVariable("meetingNo") int meetingNo,
                                         HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        User loginUser = (User) session.getAttribute("loginUser");
        
        if (loginUser != null) {
            int userNo = loginUser.getUserNo();
            int updateResult = chatMessageService.updateReadTime(meetingNo, userNo);
            result.put("success", updateResult > 0);
        } else {
            result.put("success", false);
            result.put("message", "로그인되지 않았습니다.");
        }
        
        return result;
    }

    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("meetingNo") int meetingNo,
                                         HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            result.put("status", "error");
            result.put("message", "로그인이 필요합니다.");
            return result;
        }
        int userNo = loginUser.getUserNo(); 
        
        try {
            if (file.isEmpty()) {
                result.put("status", "error");
                result.put("message", "파일이 선택되지 않았습니다.");
                return result;
            }
            
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String savedFileName = UUID.randomUUID().toString() + extension;
            
            String uploadDir = session.getServletContext().getRealPath("/resources/uploads/chat/");
            File uploadPath = new File(uploadDir);
            
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }
            
            File savedFile = new File(uploadPath, savedFileName);
            file.transferTo(savedFile);
            
            String fileUrl = "/routine/resources/uploads/chat/" + savedFileName;
            
            String messageType = "file";
            String contentType = file.getContentType();
            
            if (contentType != null) {
                if (contentType.startsWith("image/")) {
                    messageType = "image";
                } else if (contentType.startsWith("video/")) {
                    messageType = "video";
                } else if (contentType.startsWith("audio/")) {
                    messageType = "audio";
                }
            }
            

            result.put("status", "success");
            result.put("fileUrl", fileUrl);
            result.put("fileName", originalFileName);
            result.put("messageType", messageType);
            result.put("fileSize", file.getSize());
            
        } catch (IOException e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "파일 업로드 중 오류가 발생했습니다.");
        }
        
        return result;
    }
    
    @GetMapping("/recent/{meetingNo}")
    @ResponseBody
    public List<ChatMessage> getRecentMessages(@PathVariable("meetingNo") int meetingNo,
                                              @RequestParam(value = "limit", defaultValue = "20") int limit) {
        return chatMessageService.selectRecentChatMessages(meetingNo, limit);
    }
}