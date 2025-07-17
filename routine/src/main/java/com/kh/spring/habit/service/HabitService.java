package com.kh.spring.habit.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.kh.spring.habit.model.vo.Habit;

public interface HabitService {

	int insertHabit(Habit habit);

	List<Habit> habitList();

	List<Habit> getHabitsByUser(int userNo);


}
