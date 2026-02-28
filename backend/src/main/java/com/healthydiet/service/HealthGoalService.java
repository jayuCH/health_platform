package com.healthydiet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.entity.HealthGoal;
import com.healthydiet.vo.HealthGoalVO;

/**
 * 健康目标服务接口
 */
public interface HealthGoalService extends IService<HealthGoal> {

    /**
     * 根据用户ID获取健康目标
     */
    HealthGoal getByUserId(Long userId);

    /**
     * 设置健康目标
     */
    void setHealthGoal(Long userId, HealthGoalVO healthGoalVO);
}
