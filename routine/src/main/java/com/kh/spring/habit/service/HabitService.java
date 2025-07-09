package com.kh.spring.habit.service;

import java.util.List;

import com.kh.spring.habit.model.vo.Habit;

public interface HabitService {

	int insertHabit(Habit habit);

	List<Habit> habitList();

}
