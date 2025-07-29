package com.kh.spring.recommend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private int locationNo;         
    private String locationName;    
    private String address;         
    private Double latitude;      
    private Double longitude;       
    private Integer nx;            
    private Integer ny;            

    private Weather weatherObject; 
}