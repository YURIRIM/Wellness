package com.kh.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.spring.common.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 인터셉터를 적용할 URL 패턴
                .addPathPatterns(
                    "/user/mypage",         // 마이페이지
                    "/user/update",         // 정보수정
                    "/user/delete",         // 회원탈퇴
                    "/user/admin/**",       // 관리자 페이지 (모든 하위 경로)
                    "/board/write",         // 글쓰기 (게시판 기능이 있다면)
                    "/board/update",        // 글수정
                    "/board/delete"         // 글삭제
                )
                // 제외할 URL 패턴
                .excludePathPatterns(
                    "/",                    // 메인 페이지
                    "/user/login",          // 로그인 페이지
                    "/user/signup",         // 회원가입 페이지
                    "/user/checkId",        // 아이디 중복확인
                    "/static/**",           // 정적 리소스
                    "/css/**",              // CSS 파일
                    "/js/**",               // JavaScript 파일
                    "/images/**",           // 이미지 파일
                    "/favicon.ico",         // 파비콘
                    "/error"                // 에러 페이지
                );
    }
}