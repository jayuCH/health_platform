package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 健康档案实体
 */
@Data
@TableName("health_record")
public class HealthRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 记录日期
     */
    private String recordDate;

    /**
     * 体重 kg
     */
    private BigDecimal weight;

    /**
     * 体脂率 %
     */
    private BigDecimal bodyFat;

    /**
     * 肌肉量 kg
     */
    private BigDecimal muscle;

    /**
     * 水分 kg
     */
    private BigDecimal water;

    /**
     * 睡眠时长 小时
     */
    private BigDecimal sleepHours;

    /**
     * 睡眠质量 poor/fair/good/excellent
     */
    private String sleepQuality;

    /**
     * 运动时长 分钟
     */
    private Integer exerciseDuration;

    /**
     * 运动类型
     */
    private String exerciseType;

    /**
     * 运动消耗 kcal
     */
    private Integer exerciseCalories;

    /**
     * 步数
     */
    private Integer steps;

    /**
     * 热量摄入 kcal
     */
    private Integer calorieIntake;

    /**
     * 饮水量 ml
     */
    private Integer waterIntake;

    /**
     * 备注
     */
    private String notes;

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
