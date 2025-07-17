package com.kh.spring.challenge.model.dao;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Attachment;

@Repository
public class AttachmentDao {

	public int insertAtChal(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtChal", at);
	}
	
	public int connectAtbyUuid(SqlSessionTemplate sqlSession, byte[] uuid) {
		Map<String, Object> param = new HashMap<>();
		param.put("uuid", uuid);
		return sqlSession.update("attachmentMapper.connectChalToAt", param);
	}
	
	public Attachment selectAtChal(SqlSessionTemplate sqlSession, byte[] uuid) {
		Map<String, Object> param = new HashMap<>();
		param.put("uuid", uuid);
		return sqlSession.selectOne("attachmentMapper.selectAtChal", param);
	}

	public Attachment selectAtComment(SqlSessionTemplate sqlSession, int commentNo) {
		return sqlSession.selectOne("attachmentMapper.selectAtComment", commentNo);
	}

	public int insertAtComment(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtComment", at);
	}

	public int disconnectCommentToAt(SqlSessionTemplate sqlSession, int commentNo) {
		return sqlSession.update("attachmentMapper.disconnectCommentAndAt",commentNo);
	}
}
