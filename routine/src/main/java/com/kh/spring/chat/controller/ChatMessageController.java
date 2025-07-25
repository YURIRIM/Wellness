package com.kh.spring.chat.controller;

import com.kh.spring.chat.model.service.ChatMessageService;
import com.kh.spring.chat.model.vo.ChatMessage;
import com.kh.spring.meeting.model.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                          HttpSession session) {
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo == null) {
            return "redirect:/user/login";
        }
        
        if (!meetingService.isUserParticipant(meetingNo, userNo)) {
            meetingService.joinMeeting(meetingNo, userNo);
        }
        
        List<ChatMessage> messages = chatMessageService.selectAllChatMessages(meetingNo);
        
        model.addAttribute("messages", messages);
        model.addAttribute("meetingNo", meetingNo);
        
        return "chat/chatRoom";
    }
    
    @PostMapping("/upload")
    @ResponseBody
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam("meetingNo") int meetingNo,
                                         HttpSession session) {
        
        Map<String, Object> result = new HashMap<>();
        
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
    
    @PostMapping("/read/{meetingNo}")
    @ResponseBody
    public Map<String, Object> markAsRead(@PathVariable("meetingNo") int meetingNo,
                                         HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        Integer userNo = (Integer) session.getAttribute("userNo");
        if (userNo != null) {
            int updateResult = chatMessageService.updateReadTime(meetingNo, userNo);
            result.put("success", updateResult > 0);
        } else {
            result.put("success", false);
        }
        
        return result;
    }
}