package com.kh.spring.videoCall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Challenge {
	private int chalNo;
    private String title;
    private String content;
}
