package com.kh.spring.habit.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;
import com.kh.spring.habit.model.vo.HabitRepeat;

@Repository
public class HabitDao {

	public int insertHabit(SqlSessionTemplate sqlSession, Habit h) {
		return sqlSession.insert("habitMapper.insertHabit",h);
	}

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

    public int deleteHabit(SqlSessionTemplate session, int habitNo) {
        return session.delete("habitMapper.deleteHabit", habitNo);
    }

	


}
