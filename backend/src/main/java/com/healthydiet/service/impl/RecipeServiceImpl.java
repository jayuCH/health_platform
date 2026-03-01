package com.healthydiet.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.entity.*;
import com.healthydiet.mapper.RecipeCollectMapper;
import com.healthydiet.mapper.RecipeMapper;
import com.healthydiet.service.*;
import com.healthydiet.vo.RecipeDetailVO;
import com.healthydiet.vo.RecipeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 食谱服务实现
 */
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl extends ServiceImpl<RecipeMapper, Recipe> implements RecipeService {

    private final RecipeCollectMapper recipeCollectMapper;
    private final RecipeCategoryService recipeCategoryService;
    private final HealthGoalService healthGoalService;
    private final RecipeRatingService recipeRatingService;
    private final AiLogService aiLogService;

    @Override
    public IPage<RecipeVO> pageRecipes(Long userId, Long categoryId, String keyword, String sortBy, Integer current, Integer size) {
        Page<Recipe> page = new Page<>(current, size);

        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();

        if (categoryId != null) {
            wrapper.eq(Recipe::getCategoryId, categoryId);
        }

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Recipe::getName, keyword).or().like(Recipe::getDescription, keyword));
        }

        // 排序
        if ("hot".equals(sortBy)) {
            wrapper.orderByDesc(Recipe::getViewCount);
        } else if ("rating".equals(sortBy)) {
            wrapper.orderByDesc(Recipe::getRating, Recipe::getRatingCount);
        } else {
            wrapper.orderByDesc(Recipe::getCreateTime);
        }

        IPage<Recipe> recipePage = page(page, wrapper);
        IPage<RecipeVO> voPage = new Page<>(recipePage.getCurrent(), recipePage.getSize(), recipePage.getTotal());

        List<RecipeVO> voList = recipePage.getRecords().stream().map(recipe -> {
            RecipeVO vo = convertToVO(recipe, userId);
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public RecipeDetailVO getRecipeDetail(Long userId, Long recipeId) {
        Recipe recipe = getById(recipeId);
        if (recipe == null) {
            throw new BusinessException("食谱不存在");
        }

        // 增加浏览次数
        update().setSql("view_count = view_count + 1").eq("id", recipeId).update();

        RecipeDetailVO vo = new RecipeDetailVO();
        BeanUtils.copyProperties(recipe, vo);

        // 解析JSON字段
        if (StrUtil.isNotBlank(recipe.getIngredients())) {
            vo.setIngredients(JSON.parseArray(recipe.getIngredients(), RecipeDetailVO.RecipeIngredient.class));
        }
        if (StrUtil.isNotBlank(recipe.getSteps())) {
            vo.setSteps(JSON.parseArray(recipe.getSteps(), RecipeDetailVO.RecipeStep.class));
        }
        if (StrUtil.isNotBlank(recipe.getTags())) {
            vo.setTags(JSON.parseArray(recipe.getTags(), String.class));
        }

        // 设置分类名称
        if (recipe.getCategoryId() != null) {
            var category = recipeCategoryService.getById(recipe.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 检查是否收藏
        if (userId != null) {
            vo.setIsCollected(recipeCollectMapper.selectCount(
                    new LambdaQueryWrapper<RecipeCollect>()
                            .eq(RecipeCollect::getUserId, userId)
                            .eq(RecipeCollect::getRecipeId, recipeId)
            ) > 0);
        }

        return vo;
    }

    @Override
    @Transactional
    public void toggleCollect(Long userId, Long recipeId) {
        Recipe recipe = getById(recipeId);
        if (recipe == null) {
            throw new BusinessException("食谱不存在");
        }

        RecipeCollect collect = recipeCollectMapper.selectOne(
                new LambdaQueryWrapper<RecipeCollect>()
                        .eq(RecipeCollect::getUserId, userId)
                        .eq(RecipeCollect::getRecipeId, recipeId)
        );

        if (collect == null) {
            // 收藏
            collect = new RecipeCollect();
            collect.setUserId(userId);
            collect.setRecipeId(recipeId);
            recipeCollectMapper.insert(collect);

            // 增加收藏次数
            update().setSql("collect_count = collect_count + 1").eq("id", recipeId).update();
        } else {
            // 取消收藏
            recipeCollectMapper.deleteById(collect.getId());

            // 减少收藏次数
            update().setSql("collect_count = collect_count - 1").eq("id", recipeId).update();
        }
    }

    @Override
    public IPage<RecipeVO> getMyCollects(Long userId, Integer current, Integer size) {
        Page<RecipeCollect> page = new Page<>(current, size);

        LambdaQueryWrapper<RecipeCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecipeCollect::getUserId, userId);
        wrapper.orderByDesc(RecipeCollect::getCreateTime);

        IPage<RecipeCollect> collectPage = recipeCollectMapper.selectPage(page, wrapper);

        List<Long> recipeIds = collectPage.getRecords().stream()
                .map(RecipeCollect::getRecipeId)
                .collect(Collectors.toList());

        List<Recipe> recipes = listByIds(recipeIds);
        Map<Long, Recipe> recipeMap = recipes.stream()
                .collect(Collectors.toMap(Recipe::getId, r -> r));

        List<RecipeVO> voList = recipeIds.stream()
                .map(id -> {
                    Recipe recipe = recipeMap.get(id);
                    if (recipe != null) {
                        RecipeVO vo = convertToVO(recipe, userId);
                        vo.setIsCollected(true);
                        return vo;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Page<RecipeVO> voPage = new Page<>(collectPage.getCurrent(), collectPage.getSize(), collectPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void rateRecipe(Long userId, Long recipeId, Integer score) {
        if (score < 1 || score > 5) {
            throw new BusinessException("评分必须在1-5之间");
        }

        Recipe recipe = getById(recipeId);
        if (recipe == null) {
            throw new BusinessException("食谱不存在");
        }

        recipeRatingService.saveOrUpdateRating(userId, recipeId, score);
    }

    @Override
    public RecipeDetailVO generateRecipe(Long userId, String ingredients, String dietType, String mealType, Integer calories, String preferences) {
        // TODO: 调用AI服务生成食谱
        // 这里先返回模拟数据

        RecipeDetailVO vo = new RecipeDetailVO();
        vo.setId(System.currentTimeMillis());
        vo.setName("AI生成的" + (mealType != null ? mealType : "美味") + "食谱");
        vo.setDescription("根据您的需求AI生成的健康食谱");
        vo.setCoverImage("https://via.placeholder.com/400x300");
        vo.setCalories(new BigDecimal("350"));
        vo.setProtein(new BigDecimal("25"));
        vo.setFat(new BigDecimal("12"));
        vo.setCarbohydrate(new BigDecimal("35"));
        vo.setCookingTime(30);
        vo.setDifficulty("easy");
        vo.setRating(new BigDecimal("4.5"));
        vo.setRatingCount(0);
        vo.setViewCount(0);
        vo.setIsCollected(false);

        // 食材
        List<RecipeDetailVO.RecipeIngredient> ingredientList = new ArrayList<>();
        ingredientList.add(new RecipeDetailVO.RecipeIngredient() {{
            setName("鸡蛋");
            setAmount("2个");
        }});
        ingredientList.add(new RecipeDetailVO.RecipeIngredient() {{
            setName("西红柿");
            setAmount("1个");
        }});
        vo.setIngredients(ingredientList);

        // 步骤
        List<RecipeDetailVO.RecipeStep> stepList = new ArrayList<>();
        stepList.add(new RecipeDetailVO.RecipeStep() {{
            setStep(1);
            setDescription("准备食材，将西红柿洗净切块");
        }});
        stepList.add(new RecipeDetailVO.RecipeStep() {{
            setStep(2);
            setDescription("打散鸡蛋备用");
        }});
        stepList.add(new RecipeDetailVO.RecipeStep() {{
            setStep(3);
            setDescription("热锅下油，炒鸡蛋盛起");
        }});
        stepList.add(new RecipeDetailVO.RecipeStep() {{
            setStep(4);
            setDescription("炒西红柿出汁，倒入鸡蛋翻炒即可");
        }});
        vo.setSteps(stepList);

        // 营养小贴士
        vo.setNutritionTips(Arrays.asList(
                "此食谱富含优质蛋白质",
                "西红柿富含维生素C和番茄红素",
                "适合减脂期间食用"
        ));

        return vo;
    }

    @Override
    public IPage<RecipeVO> getRecommendedRecipes(Long userId, Integer size) {
        // TODO: 实现基于协同过滤的推荐算法
        // 这里先返回热门食谱

        Page<Recipe> page = new Page<>(1, size);

        LambdaQueryWrapper<Recipe> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Recipe::getViewCount, Recipe::getCollectCount);

        IPage<Recipe> recipePage = page(page, wrapper);

        List<RecipeVO> voList = recipePage.getRecords().stream()
                .map(recipe -> convertToVO(recipe, userId))
                .collect(Collectors.toList());

        Page<RecipeVO> voPage = new Page<>(recipePage.getCurrent(), recipePage.getSize(), recipePage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    private RecipeVO convertToVO(Recipe recipe, Long userId) {
        RecipeVO vo = new RecipeVO();
        BeanUtils.copyProperties(recipe, vo);

        // 解析JSON字段
        if (StrUtil.isNotBlank(recipe.getTags())) {
            vo.setTags(JSON.parseArray(recipe.getTags(), String.class));
        }

        // 设置分类名称
        if (recipe.getCategoryId() != null) {
            var category = recipeCategoryService.getById(recipe.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 检查是否收藏
        if (userId != null) {
            vo.setIsCollected(recipeCollectMapper.selectCount(
                    new LambdaQueryWrapper<RecipeCollect>()
                            .eq(RecipeCollect::getUserId, userId)
                            .eq(RecipeCollect::getRecipeId, recipe.getId())
            ) > 0);
        }

        return vo;
    }
}
