package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 食物分析VO
 */
@Data
public class FoodAnalysisVO {
    private Long id;
    private String image;
    private List<FoodItem> foods;
    private BigDecimal totalCalories;
    private BigDecimal totalProtein;
    private BigDecimal totalFat;
    private BigDecimal totalCarbohydrate;
    private LocalDateTime analysisTime;
    private String aiModel;

    @Data
    public static class FoodItem {
        private String name;
        private BigDecimal calories;
        private BigDecimal protein;
        private BigDecimal fat;
        private BigDecimal carbohydrate;
        private BigDecimal confidence;
    }
}
