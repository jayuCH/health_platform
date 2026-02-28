package com.healthydiet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.healthydiet.common.PageQuery;
import com.healthydiet.common.Result;
import com.healthydiet.service.FoodAnalysisService;
import com.healthydiet.vo.FoodAnalysisVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 食物分析控制器
 */
@Tag(name = "食物分析", description = "食物分析相关接口")
@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final FoodAnalysisService foodAnalysisService;

    @Operation(summary = "上传图片分析")
    @PostMapping("/upload")
    public Result<FoodAnalysisVO> analyzeImage(
            @AuthenticationPrincipal Long userId,
            @RequestParam("file") MultipartFile file) {
        return Result.success(foodAnalysisService.analyzeImage(userId, file));
    }

    @Operation(summary = "获取分析历史")
    @GetMapping("/history")
    public Result<IPage<FoodAnalysisVO>> getHistory(
            @AuthenticationPrincipal Long userId,
            @ModelAttribute PageQuery pageQuery) {
        return Result.success(foodAnalysisService.pageHistory(userId, pageQuery));
    }

    @Operation(summary = "获取分析详情")
    @GetMapping("/{id}")
    public Result<FoodAnalysisVO> getDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        return Result.success(foodAnalysisService.getDetail(userId, id));
    }
}
