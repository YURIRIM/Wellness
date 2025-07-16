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

}
