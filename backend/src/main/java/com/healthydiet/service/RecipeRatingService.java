package com.healthydiet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.entity.RecipeRating;

/**
 * 食谱评分服务接口
 */
public interface RecipeRatingService extends IService<RecipeRating> {

    void saveOrUpdateRating(Long userId, Long recipeId, Integer score);
}
