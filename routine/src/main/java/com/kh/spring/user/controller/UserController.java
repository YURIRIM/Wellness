package com.kh.spring.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.spring.user.model.service.UserService;
import com.kh.spring.user.model.vo.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public String loginUser(User u, HttpSession session, Model model) {
        
        System.out.println("로그인 시도 - 사용자 입력 ID: " + u.getUserId());
        
        // 사용자가 입력한 아이디만 가지고 일치한 회원 정보 조회
        User loginUser = userService.loginUser(u);
        
        System.out.println("DB에서 조회된 사용자: " + loginUser);
        System.out.println("사용자가 입력한 비밀번호: " + u.getPassword());
        
        session.setAttribute("loginUser", loginUser);
        // 비밀번호 검증
        if (loginUser != null && passwordEncoder.matches(u.getPassword(), loginUser.getPassword())) {
            session.setAttribute("alertMsg", "로그인 성공!");
            return "redirect:/";
        } else {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "common/errorPage";
        }
    }

    @RequestMapping("/logout")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "user/userEnrollForm";
    }

    @PostMapping("/signup")
    public String signupUser(User u, HttpSession session, Model model) {
        
        System.out.println("회원가입 시도: " + u);
        
        if (u.getEmail() != null) {
            u.setEmail(u.getEmail().replace("&#64;", "@"));
        }
        
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(u.getPassword());
        u.setPassword(encodedPassword);
        
        // 기본 권한 설정
        if (u.getRole() == null || u.getRole().isEmpty()) {
            u.setRole("USER");
        }
        
        // 회원가입 처리
        int result = userService.insertUser(u);
        
        if (result > 0) {
            session.setAttribute("alertMsg", "회원가입을 환영합니다.");
            return "redirect:/";
        } else {
            model.addAttribute("errorMsg", "회원가입에 실패하였습니다.");
            return "common/errorPage";
        }
    }

    @RequestMapping("/mypage")
    public String myPage() {
        return "user/mypage";
    }

    @RequestMapping("/update")
    public String updateUser(User user, HttpSession session, Model model) {
        
        int result = userService.updateUser(user);
        
        if (result > 0) {
            session.setAttribute("alertMsg", "정보수정 성공!");
            
            // 변경된 정보 갱신
            User updatedUser = userService.loginUser(user);
            session.setAttribute("loginUser", updatedUser);
            
            return "redirect:/user/mypage";
        } else {
            model.addAttribute("errorMsg", "정보수정 실패!");
            return "common/errorPage";
        }
    }

    @RequestMapping("/delete")
    public String deleteUser(User u, HttpSession session) {
        
        User loginUser = userService.loginUser(u);
        
        if (loginUser != null && passwordEncoder.matches(u.getPassword(), loginUser.getPassword())) {
            
            int result = userService.deleteUser(u);
            
            if (result > 0) {
                session.setAttribute("alertMsg", "회원 탈퇴 성공");
                session.removeAttribute("loginUser");
                return "redirect:/";
            } else {
                session.setAttribute("alertMsg", "회원 탈퇴 실패");
                return "redirect:/user/mypage";
            }
        } else {
            session.setAttribute("alertMsg", "비밀번호를 잘못입력하셨습니다.");
            return "redirect:/user/mypage";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/checkId", produces = "text/html;charset=UTF-8")
    public String checkUserId(@RequestParam String userId) {
        
        int count = userService.checkUserId(userId);
        
        if (count > 0) {
            return "NNNNN"; // 중복
        } else {
            return "NNNNY"; // 사용가능
        }
    }
}