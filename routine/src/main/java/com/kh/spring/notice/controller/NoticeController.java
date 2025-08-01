package com.kh.spring.notice.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.kh.spring.notice.model.service.NoticeService;
import com.kh.spring.notice.model.vo.Notice;

@Controller
@RequestMapping("/notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    @GetMapping("")
    public String noticeRoot() {
        return "redirect:/notice/list";
    }
    
    @GetMapping("/")
    public String noticeRootSlash() {
        return "redirect:/notice/list";
    }
    
    // 사용자용 - 공지사항 목록 페이지 (읽기 전용)
    @GetMapping("/list")
    public String noticeList(Model model) {
        List<Notice> noticeList = noticeService.selectNoticeList();
        int noticeCount = noticeService.selectNoticeCount();
        
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("noticeCount", noticeCount);
        
        return "notice/noticeList";
    }
    
    // 사용자용 - 공지사항 상세 페이지 (읽기 전용)
    @GetMapping("/detail")
    public String noticeDetail(@RequestParam("noticeNum") int noticeNum, Model model) {
        Notice notice = noticeService.selectNoticeByNum(noticeNum);
        
        if (notice != null) {
            model.addAttribute("notice", notice);
            return "notice/noticeDetail";
        } else {
            model.addAttribute("errorMsg", "해당 공지사항을 찾을 수 없습니다.");
            return "common/errorPage";
        }
    }
}