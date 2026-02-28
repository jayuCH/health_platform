package com.healthydiet.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.entity.HealthRecord;
import com.healthydiet.mapper.HealthRecordMapper;
import com.healthydiet.service.HealthRecordService;
import com.healthydiet.vo.HealthRecordVO;
import com.healthydiet.vo.HealthStatsVO;
import com.healthydiet.vo.HealthTrendVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 健康档案服务实现
 */
@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord> implements HealthRecordService {

    @Override
    @Transactional
    public void saveRecord(Long userId, HealthRecordVO recordVO) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId);
        wrapper.eq(HealthRecord::getRecordDate, today);

        HealthRecord record = getOne(wrapper);

        if (record == null) {
            record = new HealthRecord();
            record.setUserId(userId);
            record.setRecordDate(today);
        }

        BeanUtils.copyProperties(recordVO, record);
        saveOrUpdate(record);
    }

    @Override
    public HealthRecordVO getTodayRecord(Long userId) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

        HealthRecord record = getOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .eq(HealthRecord::getRecordDate, today));

        if (record == null) {
            return new HealthRecordVO();
        }

        return convertToVO(record);
    }

    @Override
    public List<HealthRecordVO> getRecords(Long userId, String startDate, String endDate) {
        LambdaQueryWrapper<HealthRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HealthRecord::getUserId, userId);

        if (startDate != null) {
            wrapper.ge(HealthRecord::getRecordDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(HealthRecord::getRecordDate, endDate);
        }

        wrapper.orderByDesc(HealthRecord::getRecordDate);

        List<HealthRecord> records = list(wrapper);
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public HealthStatsVO getStats(Long userId, Integer days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<HealthRecord> records = list(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .ge(HealthRecord::getRecordDate, startDateStr));

        HealthStatsVO stats = new HealthStatsVO();

        if (records.isEmpty()) {
            return stats;
        }

        stats.setAvgWeight(records.stream()
                .filter(r -> r.getWeight() != null)
                .map(HealthRecord::getWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(records.size()), 1, RoundingMode.HALF_UP));

        stats.setAvgCalories(records.stream()
                .filter(r -> r.getCalorieIntake() != null)
                .mapToInt(HealthRecord::getCalorieIntake)
                .sum() / records.size());

        stats.setTotalExercise(records.stream()
                .filter(r -> r.getExerciseDuration() != null)
                .mapToInt(HealthRecord::getExerciseDuration)
                .sum());

        stats.setAvgSleep(records.stream()
                .filter(r -> r.getSleepHours() != null)
                .map(HealthRecord::getSleepHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(records.size()), 1, RoundingMode.HALF_UP));

        stats.setTotalSteps(records.stream()
                .filter(r -> r.getSteps() != null)
                .mapToInt(HealthRecord::getSteps)
                .sum());

        return stats;
    }

    @Override
    public HealthTrendVO getTrend(Long userId, String type, Integer days) {
        LocalDate startDate = LocalDate.now().minusDays(days - 1);
        String startDateStr = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

        List<HealthRecord> records = list(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getUserId, userId)
                .ge(HealthRecord::getRecordDate, startDateStr)
                .orderByAsc(HealthRecord::getRecordDate));

        HealthTrendVO trend = new HealthTrendVO();
        List<String> dates = new ArrayList<>();
        List<Object> values = new ArrayList<>();

        for (HealthRecord record : records) {
            dates.add(record.getRecordDate());

            Object value = switch (type) {
                case "weight" -> record.getWeight();
                case "calories" -> record.getCalorieIntake();
                case "exercise" -> record.getExerciseDuration();
                case "sleep" -> record.getSleepHours();
                default -> null;
            };

            values.add(value);
        }

        trend.setDates(dates);
        trend.setValues(values);

        return trend;
    }

    private HealthRecordVO convertToVO(HealthRecord record) {
        HealthRecordVO vo = new HealthRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}
