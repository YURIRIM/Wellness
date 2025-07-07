package com.kh.spring.challenge.model.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.Attachment;

@Repository
public class AttachmentDao {

	public int insertAttachment(SqlSessionTemplate sqlSession, Attachment at) {
		return sqlSession.insert("attachmentMapper.insertChalAttachment", at);
	}
	
}
