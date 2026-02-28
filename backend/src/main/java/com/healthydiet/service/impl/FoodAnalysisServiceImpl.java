package com.healthydiet.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.common.PageQuery;
import com.healthydiet.entity.FoodAnalysis;
import com.healthydiet.mapper.FoodAnalysisMapper;
import com.healthydiet.service.AiLogService;
import com.healthydiet.service.FoodAnalysisService;
import com.healthydiet.vo.FoodAnalysisVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 食物分析服务实现
 */
@Service
@RequiredArgsConstructor
public class FoodAnalysisServiceImpl extends ServiceImpl<FoodAnalysisMapper, FoodAnalysis> implements FoodAnalysisService {

    private final AiLogService aiLogService;

    @Value("${file.upload-path:/data/uploads/}")
    private String uploadPath;

    @Override
    @Transactional
    public FoodAnalysisVO analyzeImage(Long userId, MultipartFile file) {
        // 上传图片
        String imageUrl = uploadFile(file);

        // 调用AI分析
        long startTime = System.currentTimeMillis();
        FoodAnalysisVO analysisVO;
        String status = "success";
        String errorMsg = null;

        try {
            analysisVO = analyzeWithAI(imageUrl);
        } catch (Exception e) {
            status = "failed";
            errorMsg = e.getMessage();
            throw new BusinessException("食物分析失败: " + errorMsg);
        } finally {
            long duration = (int) (System.currentTimeMillis() - startTime);
            aiLogService.logRequest(userId, "image_analysis", imageUrl, null, duration, status, errorMsg, "food_recognition_model");
        }

        // 保存分析结果
        FoodAnalysis analysis = new FoodAnalysis();
        analysis.setUserId(userId);
        analysis.setImage(imageUrl);
        analysis.setFoods(cn.hutool.json.JSONUtil.toJsonStr(analysisVO.getFoods()));
        analysis.setTotalCalories(analysisVO.getTotalCalories());
        analysis.setTotalProtein(analysisVO.getTotalProtein());
        analysis.setTotalFat(analysisVO.getTotalFat());
        analysis.setTotalCarbohydrate(analysisVO.getTotalCarbohydrate());
        analysis.setAiModel("food_recognition_model");
        save(analysis);

        analysisVO.setId(analysis.getId());
        return analysisVO;
    }

    @Override
    public IPage<FoodAnalysisVO> pageHistory(Long userId, PageQuery pageQuery) {
        Page<FoodAnalysis> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());

        LambdaQueryWrapper<FoodAnalysis> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodAnalysis::getUserId, userId);
        wrapper.orderByDesc(FoodAnalysis::getCreateTime);

        IPage<FoodAnalysis> analysisPage = page(page, wrapper);
        IPage<FoodAnalysisVO> voPage = new Page<>(analysisPage.getCurrent(), analysisPage.getSize(), analysisPage.getTotal());

        List<FoodAnalysisVO> voList = analysisPage.getRecords().stream().map(analysis -> {
            FoodAnalysisVO vo = new FoodAnalysisVO();
            BeanUtils.copyProperties(analysis, vo);

            if (analysis.getFoods() != null) {
                vo.setFoods(cn.hutool.json.JSONUtil.toList(analysis.getFoods(), FoodAnalysisVO.FoodItem.class));
            }

            vo.setAnalysisTime(analysis.getCreateTime());
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public FoodAnalysisVO getDetail(Long userId, Long analysisId) {
        FoodAnalysis analysis = getOne(new LambdaQueryWrapper<FoodAnalysis>()
                .eq(FoodAnalysis::getId, analysisId)
                .eq(FoodAnalysis::getUserId, userId));

        if (analysis == null) {
            throw new BusinessException("分析记录不存在");
        }

        FoodAnalysisVO vo = new FoodAnalysisVO();
        BeanUtils.copyProperties(analysis, vo);

        if (analysis.getFoods() != null) {
            vo.setFoods(cn.hutool.json.JSONUtil.toList(analysis.getFoods(), FoodAnalysisVO.FoodItem.class));
        }

        vo.setAnalysisTime(analysis.getCreateTime());
        return vo;
    }

    private String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = "analysis/" + IdUtil.simpleUUID() + extension;

            File uploadDir = new File(uploadPath + "analysis/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destFile = new File(uploadPath + filename);
            file.transferTo(destFile);

            return "http://localhost:8080/api/file/" + filename;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }

    private FoodAnalysisVO analyzeWithAI(String imageUrl) {
        // TODO: 调用AI图像识别服务
        // 这里返回模拟数据

        FoodAnalysisVO vo = new FoodAnalysisVO();
        vo.setImage(imageUrl);
        vo.setTotalCalories(new BigDecimal("450"));
        vo.setTotalProtein(new BigDecimal("18"));
        vo.setTotalFat(new BigDecimal("22"));
        vo.setTotalCarbohydrate(new BigDecimal("38"));
        vo.setAiModel("food_recognition_model");
        vo.setAnalysisTime(LocalDateTime.now());

        List<FoodAnalysisVO.FoodItem> foods = Arrays.asList(
                createFoodItem("红烧肉", new BigDecimal("320"), new BigDecimal("15"), new BigDecimal("20"), new BigDecimal("15")),
                createFoodItem("青菜", new BigDecimal("60"), new BigDecimal("2"), new BigDecimal("1"), new BigDecimal("8")),
                createFoodItem("米饭", new BigDecimal("70"), new BigDecimal("1"), new BigDecimal("1"), new BigDecimal("15"))
        );
        vo.setFoods(foods);

        return vo;
    }

    private FoodAnalysisVO.FoodItem createFoodItem(String name, BigDecimal calories, BigDecimal protein, BigDecimal fat, BigDecimal carbohydrate) {
        FoodAnalysisVO.FoodItem item = new FoodAnalysisVO.FoodItem();
        item.setName(name);
        item.setCalories(calories);
        item.setProtein(protein);
        item.setFat(fat);
        item.setCarbohydrate(carbohydrate);
        item.setConfidence(new BigDecimal("0.95"));
        return item;
    }
}
