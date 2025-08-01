package com.kh.spring.habit.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.habit.dao.HabitDao;
import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;
import com.kh.spring.habit.model.vo.HabitRepeat;

@Service
public class HabitServiceImpl implements HabitService {

	@Autowired
	private HabitDao dao;

	@Autowired
	private SqlSessionTemplate sqlSession;

	@Override
	@Transactional
	public int insertHabit(Habit h) {
		int result = dao.insertHabit(sqlSession, h); // 습관 insert + PK 생성

		HabitRepeat repeat = h.getRepeat();
		System.out.println(repeat);
		if (repeat != null) {
			repeat.setHabitNo(h.getHabitNo()); // 생성된 habitNo 세팅
			System.out.println("habitNo: " + h.getHabitNo());
			System.out.println(repeat);
			// weekDays가 이미 String이면 별도 처리 불필요
			int resultt = dao.insertHabitRepeat(sqlSession, repeat); // 반복 정보 insert
		}

		return result;
	}

	@Override
	public List<Habit> habitList() {
		// TODO Auto-generated method stub
		return dao.habitList(sqlSession);
	}

	@Override
	public List<Habit> getHabitsByUser(int userNo) {
		// TODO Auto-generated method stub
		return dao.selectHabitsByUser(sqlSession,userNo);
	}
	
	
	
	
	
	
	
	
	 @Override
	    public Habit getHabitById(int habitNo) {
	        return dao.selectById(sqlSession, habitNo);
	    }

	    @Override
	    public void updateHabit(Habit habit) {
	    	dao.updateHabit(sqlSession, habit);
	    }

	    @Override
	    public void deleteHabit(int habitNo) {
	    	
	    	
	        // 1. 보상 이력 삭제 (두 컬럼 다 확인)
	    	dao.deleteStreakRewardsByHabitNo(sqlSession, habitNo);
	        // 2. 체크 삭제
	        dao.deleteHabitChecks(sqlSession, habitNo);
	        // 3. 반복 삭제
	        dao.deleteHabitRepeats(sqlSession, habitNo);
	        // 4. 습관 삭제
	        dao.deleteHabit(sqlSession, habitNo);
	    }

	    
	    //목표 추가
		@Override
		public int saveGoal(Goal goal) {
			
			
			return 0;
		}


}
