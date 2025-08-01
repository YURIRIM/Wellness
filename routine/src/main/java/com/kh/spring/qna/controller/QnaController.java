package com.kh.spring.qna.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.qna.model.service.QnaService;
import com.kh.spring.qna.model.vo.Qna;
import com.kh.spring.answerqna.model.service.AnswerQnaService;
import com.kh.spring.answerqna.model.vo.AnswerQna;
import com.kh.spring.user.model.vo.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class QnaController {
    
    @Autowired
    private QnaService qnaService;
    
    @Autowired
    private AnswerQnaService answerQnaService;
    
    // 문의사항 목록 페이지
    @GetMapping("/qna/list")
    public String qnaList(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            List<Qna> qnaList = qnaService.selectQnaListByUser(loginUser.getUserNo());
            int qnaCount = qnaService.selectQnaCountByUser(loginUser.getUserNo());
            
            model.addAttribute("qnaList", qnaList);
            model.addAttribute("qnaCount", qnaCount);
            
            return "qna/list";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항 목록을 불러오는 중 오류가 발생했습니다.");
            return "redirect:/";
        }
    }
    
    // 문의사항 작성 페이지
    @GetMapping("/qna/write")
    public String qnaWriteForm(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        return "qna/write";
    }
    
    // 문의사항 등록 처리
    @PostMapping("/qna/write")
    public String qnaWrite(@ModelAttribute Qna qna, 
                          HttpServletRequest request, 
                          RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        // 입력값 검증
        if (qna.getUserQnaTitle() == null || qna.getUserQnaTitle().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "제목을 입력해주세요.");
            return "redirect:/qna/write";
        }
        
        if (qna.getUserQna() == null || qna.getUserQna().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "문의 내용을 입력해주세요.");
            return "redirect:/qna/write";
        }
        
        try {
            qna.setUserNo(loginUser.getUserNo());
            int result = qnaService.insertQna(qna);
            
            if (result > 0) {
                redirectAttributes.addFlashAttribute("alertMsg", "문의사항이 성공적으로 등록되었습니다.");
                return "redirect:/qna/list";
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "문의사항 등록에 실패했습니다.");
                return "redirect:/qna/write";
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항 등록 중 오류가 발생했습니다.");
            return "redirect:/qna/write";
        }
    }
    
    // 문의사항 상세 페이지
    @GetMapping("/qna/detail/{userQnaNo}")
    public String qnaDetail(@PathVariable int userQnaNo, 
                           Model model, 
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            Qna qna = qnaService.selectQnaByNo(userQnaNo);
            
            if (qna == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 문의사항입니다.");
                return "redirect:/qna/list";
            }
            
            // 본인이 작성한 문의사항인지 확인 (관리자는 모든 문의사항 조회 가능)
            if (qna.getUserNo() != loginUser.getUserNo() && !"ADMIN".equals(loginUser.getRole())) {
                redirectAttributes.addFlashAttribute("errorMsg", "접근 권한이 없습니다.");
                return "redirect:/qna/list";
            }
            
            // 답변 조회
            AnswerQna answer = answerQnaService.selectAnswerByQnaNo(userQnaNo);
            
            model.addAttribute("qna", qna);
            model.addAttribute("answer", answer);
            
            return "qna/detail";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항을 불러오는 중 오류가 발생했습니다.");
            return "redirect:/qna/list";
        }
    }
    
    // 문의사항 수정 페이지
    @GetMapping("/qna/edit/{userQnaNo}")
    public String qnaEditForm(@PathVariable int userQnaNo, 
                             Model model, 
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            Qna qna = qnaService.selectQnaByNo(userQnaNo);
            
            if (qna == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 문의사항입니다.");
                return "redirect:/qna/list";
            }
            
            // 본인이 작성한 문의사항인지 확인
            if (qna.getUserNo() != loginUser.getUserNo()) {
                redirectAttributes.addFlashAttribute("errorMsg", "수정 권한이 없습니다.");
                return "redirect:/qna/list";
            }
            
            // 이미 답변이 완료된 문의사항은 수정 불가
            if (qna.isStatus()) {
                redirectAttributes.addFlashAttribute("errorMsg", "답변이 완료된 문의사항은 수정할 수 없습니다.");
                return "redirect:/qna/detail/" + userQnaNo;
            }
            
            model.addAttribute("qna", qna);
            return "qna/edit";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항을 불러오는 중 오류가 발생했습니다.");
            return "redirect:/qna/list";
        }
    }
    
    // 문의사항 수정 처리
    @PostMapping("/qna/edit")
    public String qnaEdit(@ModelAttribute Qna qna, 
                         HttpServletRequest request,
                         RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            // 기존 문의사항 조회
            Qna existingQna = qnaService.selectQnaByNo(qna.getUserQnaNo());
            
            if (existingQna == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 문의사항입니다.");
                return "redirect:/qna/list";
            }
            
            // 권한 확인
            if (existingQna.getUserNo() != loginUser.getUserNo()) {
                redirectAttributes.addFlashAttribute("errorMsg", "수정 권한이 없습니다.");
                return "redirect:/qna/list";
            }
            
            // 답변 완료된 문의사항 수정 방지
            if (existingQna.isStatus()) {
                redirectAttributes.addFlashAttribute("errorMsg", "답변이 완료된 문의사항은 수정할 수 없습니다.");
                return "redirect:/qna/detail/" + qna.getUserQnaNo();
            }
            
            int result = qnaService.updateQna(qna);
            
            if (result > 0) {
                redirectAttributes.addFlashAttribute("alertMsg", "문의사항이 성공적으로 수정되었습니다.");
                return "redirect:/qna/detail/" + qna.getUserQnaNo();
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "문의사항 수정에 실패했습니다.");
                return "redirect:/qna/edit/" + qna.getUserQnaNo();
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMsg", "문의사항 수정 중 오류가 발생했습니다.");
            return "redirect:/qna/edit/" + qna.getUserQnaNo();
        }
    }
    
    // 문의사항 삭제 처리
    @PostMapping("/qna/delete")
    public String qnaDelete(@RequestParam("userQnaNo") int userQnaNo,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        if (loginUser == null) {
            redirectAttributes.addFlashAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        try {
            Qna qna = qnaService.selectQnaByNo(userQnaNo);
            
            if (qna == null) {
                redirectAttributes.addFlashAttribute("errorMsg", "존재하지 않는 문의사항입니다.");
                return "redirect:/qna/list";
            }
            
            // 권한 확인
            if (qna.getUserNo() != loginUser.getUserNo()) {
                redirectAttributes.addFlashAttribute("errorMsg", "삭제 권한이 없습니다.");
                return "redirect:/qna/list";
            }
            
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
        
        return "redirect:/qna/list";
    }
}