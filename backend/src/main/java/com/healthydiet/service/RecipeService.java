package com.healthydiet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.entity.Recipe;
import com.healthydiet.vo.RecipeDetailVO;
import com.healthydiet.vo.RecipeVO;

/**
 * 食谱服务接口
 */
public interface RecipeService extends IService<Recipe> {

    /**
     * 分页查询食谱列表
     */
    IPage<RecipeVO> pageRecipes(Long userId, Long categoryId, String keyword, String sortBy, Integer current, Integer size);

    /**
     * 获取食谱详情
     */
    RecipeDetailVO getRecipeDetail(Long userId, Long recipeId);

    /**
     * 收藏/取消收藏食谱
     */
    void toggleCollect(Long userId, Long recipeId);

    /**
     * 获取我的收藏
     */
    IPage<RecipeVO> getMyCollects(Long userId, Integer current, Integer size);

    /**
     * 评分
     */
    void rateRecipe(Long userId, Long recipeId, Integer score);

    /**
     * AI生成食谱
     */
    RecipeDetailVO generateRecipe(Long userId, String ingredients, String dietType, String mealType, Integer calories, String preferences);

    /**
     * 获取推荐食谱
     */
    IPage<RecipeVO> getRecommendedRecipes(Long userId, Integer size);
}
