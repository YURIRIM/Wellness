package com.kh.spring.openForum.model.service;

import java.util.ArrayList;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.openForum.model.vo.OpenForum;
import com.kh.spring.openForum.model.vo.OpenForumAttachment;

public interface OpenForumService {

	ArrayList<OpenForum> openForumList(PageInfo pi);

	int listCount();

	int increaseCount(int postId);

	OpenForum postDetail(int postId);
	
	//게시물 & 첨부파일 함께 등록
	int openForumWrite(OpenForum forum, ArrayList<OpenForumAttachment> atList);

}
