package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食谱实体
 */
@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 食谱名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 热量 kcal
     */
    private BigDecimal calories;

    /**
     * 蛋白质 g
     */
    private BigDecimal protein;

    /**
     * 脂肪 g
     */
    private BigDecimal fat;

    /**
     * 碳水化合物 g
     */
    private BigDecimal carbohydrate;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 烹饪时间 分钟
     */
    private Integer cookingTime;

    /**
     * 难度 easy/medium/hard
     */
    private String difficulty;

    /**
     * 标签 JSON数组
     */
    private String tags;

    /**
     * 食材 JSON数组
     */
    private String ingredients;

    /**
     * 制作步骤 JSON数组
     */
    private String steps;

    /**
     * 平均评分
     */
    private BigDecimal rating;

    /**
     * 评分人数
     */
    private Integer ratingCount;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 收藏次数
     */
    private Integer collectCount;

    /**
     * 是否AI生成
     */
    private Integer isAiGenerated;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
