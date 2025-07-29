package com.kh.spring.challenge.model.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Attachment;
import com.kh.spring.challenge.model.vo.ConnectByUuid;

@Repository
public class AttachmentDao {

	public int insertAtChal(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtChal", at);
	}
	
	public int connectAtbyUuid(SqlSessionTemplate sqlSession, ConnectByUuid cbu) {
		return sqlSession.update("attachmentMapper.connectAtbyUuid", cbu);
	}
	
	public Attachment selectAtChal(SqlSessionTemplate sqlSession, byte[] uuid) {
		Map<String, Object> param = new HashMap<>();
		param.put("uuid", uuid);
		return sqlSession.selectOne("attachmentMapper.selectAtChal", param);
	}

	public List<Attachment> selectAtComment(SqlSessionTemplate sqlSession, List<Integer> commentNos) {
		return sqlSession.selectList("attachmentMapper.selectAtComment", commentNos);
	}

	public int insertAtComment(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertAtComment", at);
	}

	public int disconnectCommentToAt(SqlSessionTemplate sqlSession, int commentNo) {
		return sqlSession.update("attachmentMapper.disconnectCommentAndAt",commentNo);
	}
}
