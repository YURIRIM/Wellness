package com.kh.spring.recommend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private int weatherNo;          
    private int locationNo;        
    
   
    private int temperature;        
    private int humidity;         
    private int rainProbability; 
    private String weatherCondition; 
    
    private int pm10;             
    private String airGrade;       
}