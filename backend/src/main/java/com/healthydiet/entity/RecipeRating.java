package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;

/**
 * 食谱评分实体
 */
@Data
@TableName("recipe_rating")
public class RecipeRating {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long recipeId;

    private Integer score;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
