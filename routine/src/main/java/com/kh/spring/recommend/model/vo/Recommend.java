package com.kh.spring.recommend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recommend {
    private int recommendNo;        
    private int weatherNo;          
    
    private String exerciseType;     //운동 종류
    private String locationType;     //추천 장소
}