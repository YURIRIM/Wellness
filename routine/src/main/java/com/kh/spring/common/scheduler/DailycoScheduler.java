package com.kh.spring.common.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kh.spring.util.common.Regexp;
import com.kh.spring.util.common.UuidUtil;
import com.kh.spring.videoCall.model.dao.VideoCallDao;
import com.kh.spring.videoCall.model.service.CachingParticipants;
import com.kh.spring.videoCall.model.service.DailyService;
import com.kh.spring.videoCall.model.vo.RoomStatus;

@Component
public class DailycoScheduler {
	@Autowired
	private SqlSessionTemplate sqlSession;
	@Autowired
	private DailyService dailyService;
	@Autowired
	private CachingParticipants cachService;
	@Autowired
	private VideoCallDao vcDao;
	
	//Daily.co에서 방 참여자를 돚거(아님)해오는 택티껄
	@Scheduled(cron = "0 0/1 * * * *")
	public void countParticipate() {
		
		//apikey활성화 안 되었으면 멀리멀리 끄져라.
		if(Regexp.dailycoApiKey==null) return;
		
		try {
			//돚거하기
			Map<String, Integer> result = dailyService.countParticipants();
			
			//캐시 업데이트
			cachService.update(result);
			
			//활성화 되어 있다고 주장하는 방 이리와
			List<RoomStatus> openedRoomList = vcDao.openedRoom(sqlSession);
			
			//DB와 Daily.co 응답 데이터를 비교하기
			List<byte[]> noMansRoom = new ArrayList<>();
			for(RoomStatus rs : openedRoomList) {
				String uuidStr = UuidUtil.byteArrToStr(rs.getRoomUuid());
	            if (!result.containsKey(uuidStr)) {
	            	//Daily.co 목록에 없는데 DB에서 활성화가 되어 있다?? 너 잘 걸렸다 심심했는데
	            	noMansRoom.add(rs.getRoomUuid());
	            	
	            	//닫아줘요
	            	dailyService.deleteRoom(uuidStr);
	            }
			}
			
			//유령방 닫기
			if(!noMansRoom.isEmpty()) {
				vcDao.closeNoManRooms(sqlSession, noMansRoom);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
