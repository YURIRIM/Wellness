package com.kh.spring.habit.service;

import java.sql.Date;
import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.habit.dao.HabitDao;
import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;

@Service
public class HabitServiceImpl implements HabitService {

	@Autowired
	private HabitDao dao;

	@Autowired
	private SqlSessionTemplate sqlSession;



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
		public int insertGoal(Goal goal) {
			
			return dao.insertGoal(sqlSession,goal);
		}

		@Override
		public List<Goal> selectGoalsByUser(int userNo) {
			// TODO Auto-generated method stub
			return dao.selectGoalsByUser(sqlSession,userNo);
		}
		
		@Override
		@Transactional
		public void insertHabit(Habit habit) {
	        // habit 테이블 저장
	        dao.insertHabit(sqlSession, habit);

	        // HabitRepeat가 있다면 별도로 저장 (DAO, Mapper에 구현 필요)
	        if (habit.getRepeat() != null) {
	            dao.insertHabitRepeat(sqlSession,habit.getHabitNo(), habit.getRepeat());
	        }}

		@Override
	    public List<Goal> findGoalsWithHabits(int userNo) {
	        // 1. 사용자 목표 목록 조회
	        List<Goal> goals = dao.selectGoalsByUser(sqlSession,userNo);
	        
	        // 2. 각 목표별 습관 리스트 조회 후 Goal 객체의 habits 필드에 넣기
	        for (Goal goal : goals) {
	            List<Habit> habits = dao.selectHabitsByGoal(sqlSession,goal.getGoalNo());
	            goal.setHabits(habits);
	        }
	        return goals;
	    }
		
		
		
		@Override
	    public List<HabitCheck> getChecksByHabit(int habitNo) {
	        return dao.selectChecksByHabit(sqlSession, habitNo);
	    }



//		@Override
//		public Goal selectGoal(int goalNo) {
//			// TODO Auto-generated method stub
//			return dao.selectGoal(sqlSession,goalNo);
//		}


}
