package com.kh.spring.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        HttpSession session = request.getSession();
        
        // 로그인 정보가 없다면 기존 요청을 차단
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("alertMsg", "로그인 후 이용 가능한 서비스입니다.");
            response.sendRedirect(request.getContextPath() + "/");
            return false; // 기존 요청 흐름 차단
        }
        
        return true; // 요청 흐름 유지
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        
        // 필요시 공통 데이터 추가 처리
        // modelAndView.addObject("commonData", someData);
        
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    /**
     * 사용자에게 응답이 완료된 후 실행
     * 리소스 정리 등의 작업 수행 가능
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        
        // 예외 발생 시 로깅 처리
        if (ex != null) {
            System.err.println("요청 처리 중 예외 발생: " + ex.getMessage());
        }
        
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}