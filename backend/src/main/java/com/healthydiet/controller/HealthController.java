package com.healthydiet.controller;

import com.healthydiet.annotation.CurrentUserId;
import com.healthydiet.common.Result;
import com.healthydiet.service.HealthRecordService;
import com.healthydiet.vo.HealthRecordVO;
import com.healthydiet.vo.HealthStatsVO;
import com.healthydiet.vo.HealthTrendVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康档案控制器
 */
@Tag(name = "健康档案", description = "健康档案相关接口")
@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthRecordService healthRecordService;

    @Operation(summary = "保存健康记录")
    @PostMapping("/record")
    public Result<Void> saveRecord(
            @CurrentUserId Long userId,
            @RequestBody HealthRecordVO recordVO) {
        healthRecordService.saveRecord(userId, recordVO);
        return Result.success();
    }

    @Operation(summary = "获取今日健康数据")
    @GetMapping("/today")
    public Result<HealthRecordVO> getTodayRecord(@CurrentUserId Long userId) {
        return Result.success(healthRecordService.getTodayRecord(userId));
    }

    @Operation(summary = "获取健康记录列表")
    @GetMapping("/records")
    public Result<List<HealthRecordVO>> getRecords(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return Result.success(healthRecordService.getRecords(userId, startDate, endDate));
    }

    @Operation(summary = "获取健康统计")
    @GetMapping("/stats")
    public Result<HealthStatsVO> getStats(
            @CurrentUserId Long userId,
            @RequestParam(defaultValue = "7") Integer days) {
        return Result.success(healthRecordService.getStats(userId, days));
    }

    @Operation(summary = "获取健康趋势")
    @GetMapping("/trend/{type}")
    public Result<HealthTrendVO> getTrend(
            @CurrentUserId Long userId,
            @PathVariable String type,
            @RequestParam(defaultValue = "30") Integer days) {
        return Result.success(healthRecordService.getTrend(userId, type, days));
    }
}
