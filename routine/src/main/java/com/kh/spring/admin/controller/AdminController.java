package com.kh.spring.admin.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.kh.spring.admin.model.service.AdminService;
import com.kh.spring.notice.model.vo.Notice;
import com.kh.spring.user.model.vo.User;
import com.kh.spring.qna.model.vo.Qna;
import com.kh.spring.answerqna.model.vo.AnswerQna;
import com.kh.spring.qna.model.service.QnaService;
import com.kh.spring.answerqna.model.service.AnswerQnaService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private QnaService qnaService;
    
    @Autowired
    private AnswerQnaService answerQnaService;
    
    @GetMapping("")
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/")
    public String adminRootSlash() {
        return "redirect:/admin/dashboard";
    }
    
    // 관리자 권한 체크 메소드
    private boolean checkAdminAuth(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        return loginUser != null && "ADMIN".equals(loginUser.getRole());
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        try {
            // 기존 대시보드 정보 조회
            int totalNoticeCount = adminService.selectNoticeCount();
            int totalUserCount = adminService.selectUserCount();
            
            List<Notice> recentNotices = adminService.selectRecentNotices(5);
            List<User> recentUsers = adminService.selectRecentUsers(5);
            
            int totalQnaCount = qnaService.selectQnaCount();
            int unansweredQnaCount = qnaService.selectUnansweredQnaCount();
            List<Qna> recentQnas = qnaService.selectRecentQnas(5);

            model.addAttribute("totalNoticeCount", totalNoticeCount);
            model.addAttribute("totalUserCount", totalUserCount);
            model.addAttribute("recentNotices", recentNotices);
            model.addAttribute("recentUsers", recentUsers);
            
            model.addAttribute("totalQnaCount", totalQnaCount);
            model.addAttribute("unansweredQnaCount", unansweredQnaCount);
            model.addAttribute("recentQnas", recentQnas);
            
        } catch (Exception e) {

        }
        
        return "admin/dashboard";
    }
    
    // ========== 공지사항 관리 ==========
    
    // 관리자용 - 공지사항 목록 페이지
    @GetMapping("/notice/list")
    public String adminNoticeList(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        List<Notice> noticeList = adminService.selectNoticeList();
        int noticeCount = adminService.selectNoticeCount();
        
        model.addAttribute("noticeList", noticeList);
        model.addAttribute("noticeCount", noticeCount);
        
        return "admin/adminNoticeList";
    }
    
    // 관리자용 - 공지사항 상세 페이지
    @GetMapping("/notice/detail")
    public String adminNoticeDetail(@RequestParam("noticeNum") int noticeNum, 
                                  HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        Notice notice = adminService.selectNoticeByNum(noticeNum);
        
        if (notice != null) {
            model.addAttribute("notice", notice);
            return "admin/adminNoticeDetail";
        } else {
            model.addAttribute("errorMsg", "해당 공지사항을 찾을 수 없습니다.");
            return "common/errorPage";
        }
    }
    
    // 관리자용 - 공지사항 등록 페이지
    @GetMapping("/notice/write")
    public String adminNoticeWriteForm(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        return "admin/adminNoticeWrite";
    }
    
    // 관리자용 - 공지사항 등록 처리
    @PostMapping("/notice/write")
    public String adminNoticeWrite(@ModelAttribute Notice notice, 
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        int result = adminService.insertNotice(notice);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "공지사항이 성공적으로 등록되었습니다.");
            return "redirect:/admin/notice/list";
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "공지사항 등록에 실패했습니다.");
            return "redirect:/admin/notice/write";
        }
    }
    
    // 관리자용 - 공지사항 수정 페이지
    @GetMapping("/notice/edit")
    public String adminNoticeEditForm(@RequestParam("noticeNum") int noticeNum, 
                                    HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        Notice notice = adminService.selectNoticeByNum(noticeNum);
        
        if (notice != null) {
            model.addAttribute("notice", notice);
            return "admin/adminNoticeEdit";
        } else {
            model.addAttribute("errorMsg", "해당 공지사항을 찾을 수 없습니다.");
            return "common/errorPage";
        }
    }
    
    // 관리자용 - 공지사항 수정 처리
    @PostMapping("/notice/edit")
    public String adminNoticeEdit(@ModelAttribute Notice notice, 
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        int result = adminService.updateNotice(notice);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "공지사항이 성공적으로 수정되었습니다.");
            return "redirect:/admin/notice/detail?noticeNum=" + notice.getNoticeNum();
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "공지사항 수정에 실패했습니다.");
            return "redirect:/admin/notice/edit?noticeNum=" + notice.getNoticeNum();
        }
    }
    
    // 관리자용 - 공지사항 삭제 처리
    @PostMapping("/notice/delete")
    public String adminNoticeDelete(@RequestParam("noticeNum") int noticeNum, 
                                  HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        int result = adminService.deleteNotice(noticeNum);
        
        if (result > 0) {
            redirectAttributes.addFlashAttribute("alertMsg", "공지사항이 성공적으로 삭제되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "공지사항 삭제에 실패했습니다.");
        }
        
        return "redirect:/admin/notice/list";
    }
    
    // ========== 회원 관리 ==========
    
 // 관리자용 - 회원 목록 페이지
    @GetMapping("/user/list")
    public String adminUserList(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        List<User> userList = adminService.selectUserList();
        int userCount = adminService.selectUserCount();
        
        // 통계 데이터 추가
        int activeUserCount = adminService.selectActiveUserCount();
        int inactiveUserCount = adminService.selectInactiveUserCount();
        int adminCount = adminService.selectAdminCount();
        
        model.addAttribute("userList", userList);
        model.addAttribute("userCount", userCount);
        model.addAttribute("activeUserCount", activeUserCount);
        model.addAttribute("inactiveUserCount", inactiveUserCount);
        model.addAttribute("adminCount", adminCount);
        
        return "admin/adminUserList";
    }
  
    // ========== Q&A 관리 ==========
    
    @GetMapping("/adminQnaList")
    public String adminQnaList(Model model) {
        List<Qna> qnaList = qnaService.selectAllQnaList();
        int qnaCount = qnaService.selectQnaCount();
        
        model.addAttribute("qnaList", qnaList);
        model.addAttribute("qnaCount", qnaCount);
        
        return "admin/adminQnaList";
    }

    
    // 관리자용 - 문의사항 목록 페이지
    @GetMapping("/qna/list")
    public String adminQnaList(@RequestParam(value = "status", required = false) String status,
                              HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        List<Qna> qnaList;
        int qnaCount;
        
        if ("unanswered".equals(status)) {
            // 미답변 문의사항만 조회
            qnaList = qnaService.selectUnansweredQnaList();
            qnaCount = qnaService.selectUnansweredQnaCount();
            model.addAttribute("currentStatus", "unanswered");
        } else if ("answered".equals(status)) {
            // 답변완료 문의사항만 조회
            qnaList = qnaService.selectAnsweredQnaList();
            qnaCount = qnaService.selectAnsweredQnaCount();
            model.addAttribute("currentStatus", "answered");
        } else {
            // 전체 문의사항 조회
            qnaList = qnaService.selectAllQnaList();
            qnaCount = qnaService.selectQnaCount();
            model.addAttribute("currentStatus", "all");
        }
        
        model.addAttribute("qnaList", qnaList);
        model.addAttribute("qnaCount", qnaCount);
        
        return "admin/adminQnaList";
    }
    
    // 관리자용 - 문의사항 상세 페이지 (답변 작성/보기)
    @GetMapping("/qna/detail")
    public String adminQnaDetail(@RequestParam("userQnaNo") int userQnaNo,
                                HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        // 문의사항 조회
        Qna qna = qnaService.selectQnaByNo(userQnaNo);
        if (qna == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "해당 문의사항을 찾을 수 없습니다.");
            return "redirect:/admin/qna/list";
        }
        
        // 답변 조회 (있다면)
        AnswerQna answer = answerQnaService.selectAnswerByQnaNo(userQnaNo);
        
        // 문의자 정보 조회
        User qnaUser = adminService.selectUserByNo(qna.getUserNo());
        
        model.addAttribute("qna", qna);
        model.addAttribute("answer", answer);
        model.addAttribute("qnaUser", qnaUser);
        
        return "admin/adminQnaDetail";
    }
    
 // 관리자용 - 답변 작성 처리
    @PostMapping("/qna/answer")
    public String createAnswer(@RequestParam("userQnaNo") int userQnaNo,
                              @RequestParam("answerQna") String answerContent,
                              HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        // 로그인한 관리자 정보 가져오기
        User loginAdmin = (User) session.getAttribute("loginUser");
        
        // 문의사항 정보 조회 
        Qna qna = qnaService.selectQnaByNo(userQnaNo);
        if (qna == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "해당 문의사항을 찾을 수 없습니다.");
            return "redirect:/admin/qna/list";
        }
        
        // 답변 객체 생성
        AnswerQna answerQna = new AnswerQna();
        answerQna.setUserQnaNo(userQnaNo);
        answerQna.setUserNo(qna.getUserNo()); // 문의자의 USER_NO 설정
        answerQna.setAnswerQna(answerContent);
        answerQna.setAdminName(loginAdmin.getName()); // 관리자 이름 설정
        
        try {
            // 답변 등록
            int answerResult = answerQnaService.insertAnswer(answerQna);
            
            if (answerResult > 0) {
                // 문의사항 상태를 답변완료로 변경
                int statusResult = qnaService.updateQnaStatus(userQnaNo, true);
                
                if (statusResult > 0) {
                    redirectAttributes.addFlashAttribute("alertMsg", "답변이 성공적으로 등록되었습니다.");
                } else {
                    redirectAttributes.addFlashAttribute("alertMsg", "답변은 등록되었으나 상태 변경에 실패했습니다.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "답변 등록에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "답변 등록 중 오류가 발생했습니다.");
        }
        
        return "redirect:/admin/qna/detail?userQnaNo=" + userQnaNo;
    }
    
    // 관리자용 - 문의사항 삭제
    @PostMapping("/qna/delete")
    public String deleteQna(@RequestParam("userQnaNo") int userQnaNo,
                           HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAdminAuth(session)) {
            redirectAttributes.addFlashAttribute("errorMsg", "관리자 권한이 필요합니다.");
            return "redirect:/";
        }
        
        try {
            // 답변이 있다면 먼저 삭제
            AnswerQna existingAnswer = answerQnaService.selectAnswerByQnaNo(userQnaNo);
            if (existingAnswer != null) {
                answerQnaService.deleteAnswer(existingAnswer.getAnswerNo());
            }
            
            // 문의사항 삭제
            int result = qnaService.deleteQna(userQnaNo);
            
            if (result > 0) {
                redirectAttributes.addFlashAttribute("alertMsg", "문의사항이 성공적으로 삭제되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "문의사항 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/admin/qna/list";
    }
 
    
}