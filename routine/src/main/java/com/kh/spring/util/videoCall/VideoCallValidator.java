package com.kh.spring.util.videoCall;

import com.kh.spring.util.common.Regexp;
import com.kh.spring.videoCall.model.vo.VideoCallRequest;

public class VideoCallValidator {

	//방 유효성 검사
	public static boolean createRoom(VideoCallRequest vcr) throws Exception{
		if(vcr.getRoomName() == null
				|| !vcr.getRoomName().matches(Regexp.VC_ROOM_NAME)
				|| vcr.getIsRestrict()==null
				|| !vcr.getIsRestrict().matches(Regexp.VC_RESTRICT)
				)return false;
		
		return true;
	}

}
