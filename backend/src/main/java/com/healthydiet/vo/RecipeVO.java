package com.healthydiet.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 食谱VO
 */
@Data
public class RecipeVO {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrate;
    private Long categoryId;
    private String categoryName;
    private Integer cookingTime;
    private String difficulty;
    private List<String> tags;
    private BigDecimal rating;
    private Integer ratingCount;
    private Integer viewCount;
    private Integer collectCount;
    private Boolean isCollected;
}
