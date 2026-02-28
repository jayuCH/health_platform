package com.healthydiet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.common.PageQuery;
import com.healthydiet.entity.FoodAnalysis;
import com.healthydiet.vo.FoodAnalysisVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 食物分析服务接口
 */
public interface FoodAnalysisService extends IService<FoodAnalysis> {

    /**
     * 上传图片分析
     */
    FoodAnalysisVO analyzeImage(Long userId, MultipartFile file);

    /**
     * 分页查询分析历史
     */
    IPage<FoodAnalysisVO> pageHistory(Long userId, PageQuery pageQuery);

    /**
     * 获取分析详情
     */
    FoodAnalysisVO getDetail(Long userId, Long analysisId);
}
