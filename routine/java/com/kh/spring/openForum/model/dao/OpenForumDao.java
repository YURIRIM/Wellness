package com.kh.spring.openForum.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.comments.model.vo.Comments;
import com.kh.spring.common.model.vo.PageInfo;
import com.kh.spring.openForum.model.vo.OpenForum;
import com.kh.spring.openForum.model.vo.OpenForumAttachment;

@Repository
public class OpenForumDao {
	
	//게시글 총 개수 조회 메소드
	public int listCount(SqlSessionTemplate sqlSession) {
		return sqlSession.selectOne("openForumMapper.listCount");
	}
	
	public ArrayList<OpenForum> openForumList(SqlSessionTemplate sqlSession, PageInfo pi) {
		
		//rowBounds: 마이바티스에서 페이징 처리를 도와주는 객체 - limit, offset
		//offset: 몇번째 행부터 시작할지
		//limit: 몇개 가져올지
		
		int limit = pi.getBoardLimit(); //몇개씩 보여줄 건지 (조회 개수)
		int offset = (pi.getCurrentPage()-1)*limit;
		
		RowBounds rowBounds = new RowBounds (offset, limit);
		ArrayList<OpenForum> posts = (ArrayList)sqlSession.selectList("openForumMapper.selectPostList",null, rowBounds);
		return posts;
	}

	public int increaseCount(SqlSessionTemplate sqlSession, int postId) {	
		return sqlSession.update("openForumMapper.increaseCount",postId);
	}

	public OpenForum postDetail(SqlSessionTemplate sqlSession, int postId) {
		return sqlSession.selectOne("openForumMapper.postDetail",postId);
	}
	
	
	//dao 에는 구문 따로 작성
	//게시물부분 작성
	public int postWrite(SqlSessionTemplate sqlSession, OpenForum forum) {
		return sqlSession.insert("openForumMapper.postWrite",forum);
	}

	//첨부파일작성
	public int insertAttachment(SqlSessionTemplate sqlSession, OpenForumAttachment at) {
		return sqlSession.insert("openForumMapper.insertAttachment",at);
	}

	public int selectPostId(SqlSessionTemplate sqlSession) {
		return sqlSession.selectOne("openForumMapper.selectPostId");
	}

	public int writeComments(SqlSessionTemplate sqlSession, Comments c) {
		return sqlSession.insert("openForumMapper.writeComments",c);
	}

	public ArrayList<Comments> commentList(SqlSessionTemplate sqlSession, int postId) {
		
		return (ArrayList)sqlSession.selectList("openForumMapper.commentList",postId);
	}  
	
	//sqlSession 객체 객체 받아와서 mapper 에 접근해서 필요한 sql 구문 받아오기
}
