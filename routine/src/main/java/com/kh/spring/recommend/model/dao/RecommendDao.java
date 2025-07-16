package com.kh.spring.recommend.model.dao;

import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecommendDao {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    // 위치 정보 저장
    public int insertLocation(Location location) {
        return sqlSession.insert("recommendMapper.insertLocation", location);
    }
    
    // 날씨 정보 저장
    public int insertWeather(Weather weather) {
        return sqlSession.insert("recommendMapper.insertWeather", weather);
    }
    
    // 추천 정보 저장
    public int insertRecommend(Recommend recommend) {
        return sqlSession.insert("recommendMapper.insertRecommend", recommend);
    }
    
    public List<Recommend> selectRecommendsByWeatherNo(int weatherNo) {
        return sqlSession.selectList("recommendMapper.selectRecommendsByWeatherNo", weatherNo);
    }
    
    public Weather selectWeatherByNo(int weatherNo) {
        return sqlSession.selectOne("recommendMapper.selectWeatherByNo", weatherNo);
    }
    
    
    
    
    
    
    
}