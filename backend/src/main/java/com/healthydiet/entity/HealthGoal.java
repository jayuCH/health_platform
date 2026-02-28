package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康目标实体
 */
@Data
@TableName("health_goal")
public class HealthGoal {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 目标体重 kg
     */
    private BigDecimal targetWeight;

    /**
     * 当前体重 kg
     */
    private BigDecimal currentWeight;

    /**
     * 身高 cm
     */
    private Integer height;

    /**
     * 目标热量 kcal
     */
    private Integer targetCalories;

    /**
     * 活动水平 low/medium/high
     */
    private String activityLevel;

    /**
     * 饮食类型 balanced/low-carb/high-protein/vegetarian
     */
    private String dietType;

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
