package com.kh.spring.habit.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;

public interface HabitService {


	List<Habit> habitList();

	List<Habit> getHabitsByUser(int userNo);
	
	
    Habit getHabitById(int habitNo);
    void updateHabit(Habit habit);
    void deleteHabit(int habitNo);


    int insertGoal(Goal goal);
//    Goal selectGoal(int goalNo);


	List<Goal> selectGoalsByUser(int userNo);

	void insertHabit(Habit habit);

	List<Goal> findGoalsWithHabits(int userNo);

}
