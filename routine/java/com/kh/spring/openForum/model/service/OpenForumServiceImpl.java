package com.kh.spring.openForum.model.service;

import java.util.ArrayList;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.openForum.model.dao.OpenForumDao;
import com.kh.spring.openForum.model.vo.OpenForum;
import com.kh.spring.openForum.model.vo.OpenForumAttachment;

@Service
public class OpenForumServiceImpl implements OpenForumService {

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Autowired
	private OpenForumDao dao;

	@Override
	public ArrayList<OpenForum> openForumList(PageInfo pi) {
	
		return dao.openForumList(sqlSession,pi);
	}

	@Override
	public int listCount() {
		return dao.listCount(sqlSession);
	}

	@Override
	public int increaseCount(int postId) {
		return dao.increaseCount(sqlSession,postId);
	}

	@Override
	public OpenForum postDetail(int postId) {
		return dao.postDetail(sqlSession,postId);
	}
	
	//serviceImpl impl 에서 @Transactional 걸어서  두 구문 메소드 하나로 합치기
	@Transactional
	@Override
	public int openForumWrite(OpenForum forum, ArrayList<OpenForumAttachment> atList) {
		
		//1. 게시물번호 (postId) 추출
		int postId = dao.selectPostId(sqlSession);
		
		//2. 게시글 번호가 잘 추출 된 후 게시물 번호에 직접 추가
		if(postId>0) {
			forum.setPostId(postId); //입력할 게시물 번호로 추가하기
		}else { 
			return postId; // 실패처리될 수 있도록 반환? 뭘 반환? 
		}
		
		//postWrite: 
		//insertAttachment:  
		//위 두 메소드를 transaction 으로 묶기
		int result1 = dao.postWrite(sqlSession, forum);
		int result2 = 1; //첨부파일 등록 반환값 담을 변수 (일단 가라로..1)
		
		if(result1>0) {
			//반복문을 이용하여 각 첨부파일 정보 등록 작업
			for(OpenForumAttachment at: atList) {
				at.setPostId(postId); //해당 게시물 번호 추가
				result2 = dao.insertAttachment(sqlSession, at);
			}
		} return result1 * result2;
	
	}
}
