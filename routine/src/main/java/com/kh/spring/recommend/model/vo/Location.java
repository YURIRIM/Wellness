package com.kh.spring.recommend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int locationNo;         
    private String locationName;    
    private String address;         
    private double latitude;        
    private double longitude;    
    private int nx;                
    private int ny;             
}