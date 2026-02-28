package com.healthydiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthydiet.entity.HealthGoal;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康目标Mapper
 */
@Mapper
public interface HealthGoalMapper extends BaseMapper<HealthGoal> {
}
