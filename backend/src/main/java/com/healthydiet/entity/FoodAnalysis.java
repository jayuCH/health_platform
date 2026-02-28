package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食物分析实体
 */
@Data
@TableName("food_analysis")
public class FoodAnalysis {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 图片URL
     */
    private String image;

    /**
     * 识别到的食物 JSON数组
     */
    private String foods;

    /**
     * 总热量 kcal
     */
    private BigDecimal totalCalories;

    /**
     * 总蛋白质 g
     */
    private BigDecimal totalProtein;

    /**
     * 总脂肪 g
     */
    private BigDecimal totalFat;

    /**
     * 总碳水 g
     */
    private BigDecimal totalCarbohydrate;

    /**
     * AI模型
     */
    private String aiModel;

    /**
     * 删除标记
     */
    @TableLogic
    private Integer deleted;

    /**
     * 分析时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
