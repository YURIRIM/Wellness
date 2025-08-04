package com.kh.spring.recommend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List; 

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private int weatherNo;          
    private int locationNo;        
    private Integer temperature;  
    private Integer humidity;      
    private Integer rainProbability;
    private String weatherCondition; 
    private Integer pm10;          
    private String airGrade;       
    private List<Recommend> recommendList; 
}