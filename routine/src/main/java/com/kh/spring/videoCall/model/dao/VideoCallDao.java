package com.kh.spring.videoCall.model.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.challenge.model.vo.ConnectByUuid;
import com.kh.spring.videoCall.model.vo.Challenge;
import com.kh.spring.videoCall.model.vo.RoomStatus;
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

	public int openRoom(SqlSessionTemplate sqlSession, ConnectByUuid uuidAndUserNo) {
		return sqlSession.update("videoCallMapper.openRoom", uuidAndUserNo);
	}

	public int isParticipant(SqlSessionTemplate sqlSession, ConnectByUuid uuidAndUserNo) {
		return sqlSession.selectOne("videoCallMapper.isParticipant",uuidAndUserNo);
	}

	public int deleteRoom(SqlSessionTemplate sqlSession, ConnectByUuid uuidAndUserNo) {
		return sqlSession.delete("videoCallMapper.deleteRoom", uuidAndUserNo);
	}

	public List<RoomStatus> openedRoom(SqlSessionTemplate sqlSession) {
		return sqlSession.selectList("videoCallMapper.openedRoom");
	}

	public int closeNoManRooms(SqlSessionTemplate sqlSession, List<byte[]> noMansRoom) {
		return sqlSession.update("videoCallMapper.closeNoManRooms", noMansRoom);
	}

}
