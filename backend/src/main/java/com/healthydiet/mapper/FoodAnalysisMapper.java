package com.healthydiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthydiet.entity.FoodAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 食物分析Mapper
 */
@Mapper
public interface FoodAnalysisMapper extends BaseMapper<FoodAnalysis> {
}
