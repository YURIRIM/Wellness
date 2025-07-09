package com.kh.spring.habit.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.habit.dao.HabitDao;
import com.kh.spring.habit.model.vo.Habit;

@Service
public class HabitServiceImpl implements HabitService{
	
	@Autowired
	private HabitDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Override
	@Transactional
	public int insertHabit(Habit h) {
		// TODO Auto-generated method stub
		return dao.insertHabit(sqlSession,h);
	}

	@Override
	public List<Habit> habitList() {
		// TODO Auto-generated method stub
		return dao.habitList(sqlSession);
	}

}
