package com.kh.spring.user.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder bcrypt;

    @PostMapping("/login")
    public String loginUser(User u, HttpSession session, Model model) {
        
        System.out.println("로그인 시도 - 사용자 입력 ID: " + u.getUserId());
        
        User loginUser = userService.loginUser(u);
        
        System.out.println("DB에서 조회된 사용자: " + loginUser);
        
        if (loginUser != null && bcrypt.matches(u.getPassword(), loginUser.getPassword())) {
            session.setAttribute("loginUser", loginUser);
            
            // 관리자면 관리자 대시보드로, 일반 사용자면 메인 페이지로
            if ("ADMIN".equals(loginUser.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/";
            }
        } else {
            model.addAttribute("errorMsg", "아이디 또는 비밀번호가 일치하지 않습니다.");
            return "user/login";
        }
    }

    @RequestMapping("/logout")
    public String logoutUser(HttpSession session){
        String userName = "";
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            userName = loginUser.getName();
        }
        
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
        
        String encodedPassword = bcrypt.encode(u.getPassword());
        u.setPassword(encodedPassword);
        
        if (u.getRole() == null || u.getRole().isEmpty()) {
            u.setRole("USER");
        }
        
        int result = userService.insertUser(u);
        
        if (result > 0) {
            session.setAttribute("alertMsg", u.getName() + "님, 회원가입을 환영합니다!");
            return "redirect:/user/login";
        } else {
            model.addAttribute("errorMsg", "회원가입에 실패하였습니다. 다시 시도해주세요.");
            return "user/userEnrollForm";
        }
    }

    @RequestMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser != null) {
            model.addAttribute("isAdmin", "ADMIN".equals(loginUser.getRole()));
        }
        return "user/mypage";
    }

    @PostMapping("/update")
    public String updateUser(User user, HttpSession session, Model model) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("errorMsg", "로그인이 필요합니다.");
            return "redirect:/user/login";
        }
        
        user.setUserId(loginUser.getUserId());
        
        int result = userService.updateUser(user);
        
        if (result > 0) {
            session.setAttribute("alertMsg", "정보수정이 완료되었습니다!");
            
            User updatedUser = userService.loginUser(user);
            session.setAttribute("loginUser", updatedUser);
            
            return "redirect:/user/mypage";
        } else {
            session.setAttribute("errorMsg", "정보수정에 실패했습니다.");
            return "redirect:/user/mypage";
        }
    }

    @PostMapping("/delete")
    public String deleteUser(User u, HttpSession session) {
        
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login?msg=로그인이 필요합니다.";
        }
        
        u.setUserId(loginUser.getUserId());
        User userToDelete = userService.loginUser(u);
        
        if (userToDelete != null && bcrypt.matches(u.getPassword(), userToDelete.getPassword())) {
            
            int result = userService.deleteUser(u);
            
            if (result > 0) {
                session.invalidate();
                return "redirect:/?msg=회원 탈퇴가 완료되었습니다.";
            } else {
                session.setAttribute("errorMsg", "회원 탈퇴에 실패했습니다.");
                return "redirect:/user/mypage";
            }
        } else {
            session.setAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
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