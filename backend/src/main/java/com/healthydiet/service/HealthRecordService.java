package com.healthydiet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.common.PageQuery;
import com.healthydiet.entity.HealthRecord;
import com.healthydiet.vo.HealthRecordVO;
import com.healthydiet.vo.HealthStatsVO;
import com.healthydiet.vo.HealthTrendVO;

import java.util.List;

/**
 * 健康档案服务接口
 */
public interface HealthRecordService extends IService<HealthRecord> {

    /**
     * 保存健康记录
     */
    void saveRecord(Long userId, HealthRecordVO recordVO);

    /**
     * 获取今日健康数据
     */
    HealthRecordVO getTodayRecord(Long userId);

    /**
     * 获取健康记录列表
     */
    List<HealthRecordVO> getRecords(Long userId, String startDate, String endDate);

    /**
     * 获取健康统计
     */
    HealthStatsVO getStats(Long userId, Integer days);

    /**
     * 获取健康趋势
     */
    HealthTrendVO getTrend(Long userId, String type, Integer days);
}
