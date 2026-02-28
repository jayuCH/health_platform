package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.entity.HealthGoal;
import com.healthydiet.mapper.HealthGoalMapper;
import com.healthydiet.service.HealthGoalService;
import com.healthydiet.vo.HealthGoalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 健康目标服务实现
 */
@Service
@RequiredArgsConstructor
public class HealthGoalServiceImpl extends ServiceImpl<HealthGoalMapper, HealthGoal> implements HealthGoalService {

    @Override
    public HealthGoal getByUserId(Long userId) {
        return getOne(new LambdaQueryWrapper<HealthGoal>()
                .eq(HealthGoal::getUserId, userId));
    }

    @Override
    @Transactional
    public void setHealthGoal(Long userId, HealthGoalVO healthGoalVO) {
        HealthGoal healthGoal = getByUserId(userId);

        if (healthGoal == null) {
            healthGoal = new HealthGoal();
            healthGoal.setUserId(userId);
        }

        BeanUtils.copyProperties(healthGoalVO, healthGoal);
        saveOrUpdate(healthGoal);
    }
}
