package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 健康记录VO
 */
@Data
public class HealthRecordVO {
    private Long id;
    private String recordDate;
    private BigDecimal weight;
    private BigDecimal bodyFat;
    private BigDecimal muscle;
    private BigDecimal water;
    private BigDecimal sleepHours;
    private String sleepQuality;
    private Integer exerciseDuration;
    private String exerciseType;
    private Integer exerciseCalories;
    private Integer steps;
    private Integer calorieIntake;
    private Integer waterIntake;
    private String notes;
}
