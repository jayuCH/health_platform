package com.healthydiet.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * AI生成日志实体
 */
@Data
@TableName("ai_log")
public class AiLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 请求类型 recipe_generate/image_analysis
     */
    private String requestType;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * 响应结果
     */
    private String response;

    /**
     * 调用耗时 ms
     */
    private Integer duration;

    /**
     * 状态 success/failed
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMsg;

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
