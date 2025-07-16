package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Attachment;

@Repository
public class AttachmentDao {

	public int insertAtChal(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtChal", at);
	}
	
	public int connectChalToAt(SqlSessionTemplate sqlSession, byte[] uuid) {
		return sqlSession.update("attachmentMapper.connectChalToAt", uuid);
	}
	
	public Attachment selectAtChal(SqlSessionTemplate sqlSession, byte[] uuid) {
		return sqlSession.selectOne("attachmentMapper.selectAtChal", uuid);
	}

	public Attachment selectAtComment(SqlSessionTemplate sqlSession, int commentNo) {
		return sqlSession.selectOne("attachmentMapper.selectAtComment", commentNo);
	}

	public int insertAtComment(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtComment", at);
	}
}
