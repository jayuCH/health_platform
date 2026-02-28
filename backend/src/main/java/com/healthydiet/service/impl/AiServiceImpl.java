package com.healthydiet.service.impl;

import cn.hutool.core.util.StrUtil;
import com.healthydiet.service.AiService;
import com.healthydiet.vo.RecipeDetailVO;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现
 */
@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.api-url}")
    private String apiUrl;

    @Value("${ai.api-key}")
    private String apiKey;

    @Value("${ai.model}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    public RecipeDetailVO generateRecipe(String ingredients, String dietType, String mealType, Integer calories, String preferences) {
        // 构建提示词
        StringBuilder prompt = new StringBuilder("请生成一个健康食谱");

        if (StrUtil.isNotBlank(ingredients)) {
            prompt.append("，主要食材有：").append(ingredients);
        }

        if (StrUtil.isNotBlank(mealType)) {
            prompt.append("，餐食类型：").append(mealType);
        }

        if (StrUtil.isNotBlank(dietType)) {
            prompt.append("，饮食类型：").append(dietType);
        }

        if (calories != null && calories > 0) {
            prompt.append("，目标热量约：").append(calories).append("kcal");
        }

        if (StrUtil.isNotBlank(preferences)) {
            prompt.append("，特殊要求：").append(preferences);
        }

        prompt.append("。请以JSON格式返回，包含name(名称)、description(描述)、calories(热量)、protein(蛋白质g)、fat(脂肪g)、carbohydrate(碳水g)、cookingTime(烹饪时间分钟)、difficulty(难度easy/medium/hard)、ingredients(食材数组，每个包含name和amount)、steps(步骤数组，每个包含step序号和description描述)、nutritionTips(营养小贴士数组)");

        try {
            // 调用AI API
            String response = callAiApi(prompt.toString());

            // 解析响应
            return parseRecipeResponse(response);
        } catch (Exception e) {
            log.error("AI生成食谱失败", e);
            return getFallbackRecipe(mealType);
        }
    }

    @Override
    public String recognizeFood(String imageUrl) {
        // TODO: 实现图像识别
        return "红烧肉";
    }

    private String callAiApi(String prompt) throws IOException {
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"model\":\"" + model + "\",\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt.replace("\"", "\\\"") + "\"}]}");

        Request request = new Request.Builder()
                .url(apiUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("AI API调用失败: " + response.code());
            }

            return response.body() != null ? response.body().string() : "";
        }
    }

    private RecipeDetailVO parseRecipeResponse(String response) {
        // TODO: 解析AI返回的JSON响应
        // 这里返回示例数据

        RecipeDetailVO vo = new RecipeDetailVO();
        vo.setName("AI生成食谱");
        vo.setDescription("根据您的需求生成的健康食谱");
        vo.setCoverImage("https://via.placeholder.com/400x300");
        vo.setCalories(new java.math.BigDecimal("350"));
        vo.setProtein(new java.math.BigDecimal("20"));
        vo.setFat(new java.math.BigDecimal("10"));
        vo.setCarbohydrate(new java.math.BigDecimal("40"));
        vo.setCookingTime(30);
        vo.setDifficulty("easy");
        vo.setRating(new java.math.BigDecimal("4.5"));

        List<RecipeDetailVO.RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeDetailVO.RecipeIngredient() {{
            setName("鸡蛋");
            setAmount("2个");
        }});
        vo.setIngredients(ingredients);

        List<RecipeDetailVO.RecipeStep> steps = new ArrayList<>();
        steps.add(new RecipeDetailVO.RecipeStep() {{
            setStep(1);
            setDescription("准备食材");
        }});
        vo.setSteps(steps);

        vo.setNutritionTips(Arrays.asList("富含蛋白质", "营养丰富"));

        return vo;
    }

    private RecipeDetailVO getFallbackRecipe(String mealType) {
        RecipeDetailVO vo = new RecipeDetailVO();
        vo.setName("推荐食谱");
        vo.setDescription("一个美味的" + (mealType != null ? mealType : "菜品"));
        vo.setCoverImage("https://via.placeholder.com/400x300");
        vo.setCalories(new java.math.BigDecimal("400"));
        vo.setProtein(new java.math.BigDecimal("25"));
        vo.setFat(new java.math.BigDecimal("15"));
        vo.setCarbohydrate(new java.math.BigDecimal("35"));
        vo.setCookingTime(25);
        vo.setDifficulty("easy");
        vo.setRating(new java.math.BigDecimal("4.0"));

        List<RecipeDetailVO.RecipeIngredient> ingredients = new ArrayList<>();
        ingredients.add(new RecipeDetailVO.RecipeIngredient() {{
            setName("鸡肉");
            setAmount("200g");
        }});
        vo.setIngredients(ingredients);

        List<RecipeDetailVO.RecipeStep> steps = new ArrayList<>();
        steps.add(new RecipeDetailVO.RecipeStep() {{
            setStep(1);
            setDescription("鸡肉切块腌制");
        }});
        steps.add(new RecipeDetailVO.RecipeStep() {{
            setStep(2);
            setDescription("热锅下油炒制");
        }});
        vo.setSteps(steps);

        return vo;
    }
}
