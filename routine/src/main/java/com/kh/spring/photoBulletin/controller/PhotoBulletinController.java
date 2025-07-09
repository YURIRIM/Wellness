package com.kh.spring.photoBulletin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.kh.spring.photoBulletin.model.service.PhotoBulletinService;

@Controller
public class PhotoBulletinController {
	//사진 위주의 운동 인증 게시판 (인스타그램 같은 느낌으로) 
	//첨부파일은 사진만 가능하게, 3~5장으로 제한 후 커버 사진 설정 가능하도록
	
	@Autowired
	private PhotoBulletinService service;


}
