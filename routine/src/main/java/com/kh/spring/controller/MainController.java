package com.kh.spring.controller;

import java.util.Iterator;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	
	@GetMapping("/")
	public String main() {
		ImageIO.scanForPlugins();
		return "main";
	}
	
	 /**
     * 로그인 페이지
     */
    @GetMapping("/user/login")
    public String loginForm() {
        return "user/login";
    }

}
