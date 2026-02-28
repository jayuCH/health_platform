package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 食谱详情VO
 */
@Data
public class RecipeDetailVO extends RecipeVO {
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;
    private List<String> nutritionTips;
    private String warning;

    @Data
    public static class RecipeIngredient {
        private String name;
        private String amount;
    }

    @Data
    public static class RecipeStep {
        private Integer step;
        private String description;
        private String image;
    }
}
