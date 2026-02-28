package com.healthydiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthydiet.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 健康档案Mapper
 */
@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
