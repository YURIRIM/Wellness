package com.kh.spring.recommend.model.dao;

import com.kh.spring.recommend.model.vo.Location;
import com.kh.spring.recommend.model.vo.Weather;
import com.kh.spring.recommend.model.vo.Recommend;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map; 

@Repository
public class RecommendDao {
    
    @Autowired
    private SqlSessionTemplate sqlSession;
    
    public int insertLocation(Location location) {
        return sqlSession.insert("recommendMapper.insertLocation", location);
    }
    
    public int insertWeather(Weather weather) {
        return sqlSession.insert("recommendMapper.insertWeather", weather);
    }
    
    public int insertRecommend(Recommend recommend) {
        return sqlSession.insert("recommendMapper.insertRecommend", recommend);
    }
    
    public List<Recommend> selectRecommendsByWeatherNo(int weatherNo) {
        return sqlSession.selectList("recommendMapper.selectRecommendsByWeatherNo", weatherNo);
    }
    
    public Weather selectWeatherByNo(int weatherNo) { 
        return sqlSession.selectOne("recommendMapper.selectWeatherByNo", weatherNo);
    }

    public List<Location> selectAllWeatherLocations() {
        return sqlSession.selectList("recommendMapper.selectAllWeatherLocations");
    }

    public Location selectLocationByCoords(Map<String, Double> coords) {
        return sqlSession.selectOne("recommendMapper.selectLocationByCoords", coords);
    }

    public Weather selectWeatherByLocationNo(int locationNo) {
        return sqlSession.selectOne("recommendMapper.selectWeatherByLocationNo", locationNo);
    }
}