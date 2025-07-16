package com.kh.spring.habit.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.habit.model.vo.Habit;
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


}
