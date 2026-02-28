package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 健康统计VO
 */
@Data
public class HealthStatsVO {
    private BigDecimal avgWeight;
    private Integer avgCalories;
    private Integer totalExercise;
    private BigDecimal avgSleep;
    private Integer totalSteps;
}
