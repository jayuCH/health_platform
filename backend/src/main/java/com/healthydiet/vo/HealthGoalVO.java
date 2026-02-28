package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 健康目标VO
 */
@Data
public class HealthGoalVO {
    private Long id;
    private BigDecimal targetWeight;
    private BigDecimal currentWeight;
    private Integer height;
    private Integer targetCalories;
    private String activityLevel;
    private String dietType;
}
