package com.kh.spring.habit.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.habit.model.vo.Habit;

@Repository
public class HabitDao {

	public int insertHabit(SqlSessionTemplate sqlSession, Habit h) {
		// TODO Auto-generated method stub
		return sqlSession.insert("habitMapper.insertHabit",h);
	}

}
