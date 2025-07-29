package com.kh.spring.challenge.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CategorySimilarity {
    private int categoryNo;
    private double similarity;
}
