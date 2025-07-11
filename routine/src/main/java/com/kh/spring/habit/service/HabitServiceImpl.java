package com.kh.spring.habit.service;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.habit.dao.HabitDao;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitRepeat;


@Service
public class HabitServiceImpl implements HabitService{
	
	@Autowired
	private HabitDao dao;
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Override
	@Transactional
	public int insertHabit(Habit h) {
		int result = dao.insertHabit(sqlSession, h); // 습관 insert + PK 생성

	    HabitRepeat repeat = h.getRepeat();
	    repeat.setHabitNo(h.getHabitNo()); // 생성된 habitNo 세팅

	    String weekDaysStr = null;
	    if (repeat.getWeekDays() != null && !repeat.getWeekDays().isEmpty()) {
	        weekDaysStr = String.join(",", repeat.getWeekDays());
	    }

	    int resultt = dao.insertRepeat(sqlSession, repeat, weekDaysStr); // 반복 정보 insert

	    return result; // insertHabit의 결과 리턴
	}

	@Override
	public List<Habit> habitList() {
		// TODO Auto-generated method stub
		return dao.habitList(sqlSession);
	}

}
