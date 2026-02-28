package com.healthydiet.service;

import com.healthydiet.vo.RecipeDetailVO;

/**
 * AI服务接口
 */
public interface AiService {

    /**
     * 生成食谱
     */
    RecipeDetailVO generateRecipe(String ingredients, String dietType, String mealType, Integer calories, String preferences);

    /**
     * 识别食物
     */
    String recognizeFood(String imageUrl);
}
