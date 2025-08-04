package com.kh.spring.habit.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitRepeat;

@Repository
public class HabitDao {
	


	public List<Habit> habitList(SqlSessionTemplate sqlSession) {
		return sqlSession.selectList("habitMapper.habitList");
	}

	public int insertHabitRepeat(SqlSessionTemplate sqlSession, HabitRepeat repeat) {
	    return sqlSession.insert("habitMapper.insertHabitRepeat", repeat);
	}

	public List<Habit> selectHabitsByUser(SqlSessionTemplate sqlSession, int userNo) {
		// TODO Auto-generated method stub
		return sqlSession.selectList("habitMapper.selectHabitsByUser", userNo);
	}
	
	
	
	public Habit selectById(SqlSessionTemplate session, int habitNo) {
        return session.selectOne("habitMapper.selectById", habitNo);
    }

    public int updateHabit(SqlSessionTemplate session, Habit habit) {
        return session.update("habitMapper.updateHabit", habit);
    }

    
    
    
    
    
    public void deleteStreakRewardsByHabitNo(SqlSessionTemplate session, int  habitNo) {
    	session.delete("habitMapper.deleteStreakRewardsByHabitNo", habitNo);
    }

    public void deleteHabitChecks(SqlSessionTemplate session, int  habitNo) {
    	session.delete("habitMapper.deleteHabitChecks", habitNo);
    }

    public void deleteHabitRepeats(SqlSessionTemplate session, int  habitNo) {
    	session.delete("habitMapper.deleteHabitRepeats", habitNo);
    }
    
    public int deleteHabit(SqlSessionTemplate session, int habitNo) {
        return session.delete("habitMapper.deleteHabit", habitNo);
    }

    
    
    public int insertGoal(SqlSessionTemplate session,Goal goal) {
    	return session.insert("goalMapper.insertGoal",goal);
    }


	public List<Goal> selectGoalsByUser(SqlSessionTemplate session, int userNo) {
		// TODO Auto-generated method stub
		return session.selectList("goalMapper.selectGoalsByUser",userNo);
	}
	
	
	

	public int insertHabit(SqlSessionTemplate sqlSession, Habit h) {
	    return sqlSession.insert("habitMapper.insertHabit", h);
	}

	public void insertHabitRepeat(SqlSessionTemplate session, int habitNo, HabitRepeat repeat) {
	    repeat.setHabitNo(habitNo);
	    session.insert("habitMapper.insertHabitRepeat", repeat);
	}


//    public Goal selectGoal(SqlSessionTemplate session,int goalNo) {
//        return session.selectOne("goalMapper.selectGoalByNo(goalNo)",goalNo);
//    }

	


}
