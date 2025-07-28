package com.kh.spring.videoCall.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ConnectByUuid;
import com.kh.spring.videoCall.model.vo.Challenge;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;
import com.kh.spring.videoCall.model.vo.VideoCallResponse;

@Repository
public class VideoCallDao {

	public int insertRoom(SqlSessionTemplate sqlSession, VideoCallRequest vcr) {
		return sqlSession.insert("videoCallMapper.insertRoom", vcr);
	}

	public List<Challenge> notCreatedRoom(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectList("videoCallMapper.notCreatedRoom", userNo);
	}

	public List<VideoCallResponse> createdRoom(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectList("videoCallMapper.createdRoom", userNo);
	}

	public List<VideoCallResponse> invitedRoom(SqlSessionTemplate sqlSession, int userNo) {
		return sqlSession.selectList("videoCallMapper.invitedRoom", userNo);
	}

	public int isParticipant(SqlSessionTemplate sqlSession, ConnectByUuid uuid) {
		return sqlSession.selectOne("videoCallMapper.isParticipant", uuid);
	}

}
