package com.kh.spring.habit.dao;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.kh.spring.habit.model.vo.Goal;
import com.kh.spring.habit.model.vo.Habit;
import com.kh.spring.habit.model.vo.HabitCheck;
import com.kh.spring.habit.model.vo.HabitRepeat;

@Repository
public class HabitDao {

    public List<Habit> habitList(SqlSessionTemplate sqlSession) {
        return sqlSession.selectList("habitMapper.habitList");
    }

    public int insertHabitRepeat(SqlSessionTemplate sqlSession, HabitRepeat repeat) {
        return sqlSession.insert("habitMapper.insertHabitRepeat", repeat);
    }

    public List<Habit> selectHabitsByUser(SqlSessionTemplate sqlSession, int userNo) {
        return sqlSession.selectList("habitMapper.selectHabitsByUser", userNo);
    }

    public Habit selectById(SqlSessionTemplate sqlSession, int habitNo) {
        return sqlSession.selectOne("habitMapper.selectById", habitNo);
    }

    public int updateHabit(SqlSessionTemplate sqlSession, Habit habit) {
        return sqlSession.update("habitMapper.updateHabit", habit);
    }

    public void deleteStreakRewardsByHabitNo(SqlSessionTemplate sqlSession, int habitNo) {
        sqlSession.delete("habitMapper.deleteStreakRewardsByHabitNo", habitNo);
    }

    public void deleteHabitChecks(SqlSessionTemplate sqlSession, int habitNo) {
        sqlSession.delete("habitMapper.deleteHabitChecks", habitNo);
    }

    public void deleteHabitRepeats(SqlSessionTemplate sqlSession, int habitNo) {
        sqlSession.delete("habitMapper.deleteHabitRepeats", habitNo);
    }

    public int deleteHabit(SqlSessionTemplate sqlSession, int habitNo) {
        return sqlSession.delete("habitMapper.deleteHabit", habitNo);
    }

    public int insertGoal(SqlSessionTemplate sqlSession, Goal goal) {
        return sqlSession.insert("goalMapper.insertGoal", goal);
    }

    public List<Goal> selectGoalsByUser(SqlSessionTemplate sqlSession, int userNo) {
        return sqlSession.selectList("goalMapper.selectGoalsByUser", userNo);
    }

    public int insertHabit(SqlSessionTemplate sqlSession, Habit habit) {
        return sqlSession.insert("habitMapper.insertHabit", habit);
    }

    /**
     * habitNo가 필요하므로 사전에 habit insert 후 호출해야 함
     */
    public int insertHabitRepeat(SqlSessionTemplate sqlSession, int habitNo, HabitRepeat repeat) {
        repeat.setHabitNo(habitNo);
        return sqlSession.insert("habitMapper.insertHabitRepeat", repeat);
    }

    /**
     * 목표별 습관 리스트 조회
     */
    public List<Habit> selectHabitsByGoal(SqlSessionTemplate sqlSession, int goalNo) {
        return sqlSession.selectList("habitMapper.selectHabitsByGoal", goalNo);
    }
    
    
    
    // 특정 습관 체크 이력 전체 조회
    public List<HabitCheck> selectChecksByHabit(SqlSessionTemplate sqlSession, int habitNo) {
        return sqlSession.selectList("habitMapper.selectChecksByHabit", habitNo);
    }
}
