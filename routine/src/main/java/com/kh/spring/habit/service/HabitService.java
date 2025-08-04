package com.kh.spring.habit.service;

import java.sql.Date;
import java.util.List;

import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;

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

	List<HabitCheck> getChecksByHabit(int habitNo); // 전체
}
