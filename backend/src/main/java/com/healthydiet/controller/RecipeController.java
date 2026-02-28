package com.healthydiet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.healthydiet.annotation.CurrentUserId;
import com.healthydiet.common.PageQuery;
import com.healthydiet.common.Result;
import com.healthydiet.entity.RecipeCategory;
import com.healthydiet.service.RecipeCategoryService;
import com.healthydiet.service.RecipeService;
import com.healthydiet.vo.RecipeDetailVO;
import com.healthydiet.vo.RecipeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食谱控制器
 */
@Tag(name = "食谱管理", description = "食谱相关接口")
@RestController
@RequestMapping("/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeCategoryService recipeCategoryService;

    @Operation(summary = "获取食谱分类")
    @GetMapping("/categories")
    public Result<List<RecipeCategory>> getCategories() {
        return Result.success(recipeCategoryService.list());
    }

    @Operation(summary = "获取食谱列表")
    @GetMapping("/list")
    public Result<IPage<RecipeVO>> listRecipes(
            @CurrentUserId(required = false) Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sortBy,
            @ModelAttribute PageQuery pageQuery) {
        return Result.success(recipeService.pageRecipes(userId, categoryId, keyword, sortBy, pageQuery.getCurrent(), pageQuery.getSize()));
    }

    @Operation(summary = "获取食谱详情")
    @GetMapping("/{id}")
    public Result<RecipeDetailVO> getRecipeDetail(
            @CurrentUserId(required = false) Long userId,
            @PathVariable Long id) {
        return Result.success(recipeService.getRecipeDetail(userId, id));
    }

    @Operation(summary = "收藏/取消收藏食谱")
    @PostMapping("/{id}/collect")
    public Result<Void> toggleCollect(
            @CurrentUserId Long userId,
            @PathVariable Long id) {
        recipeService.toggleCollect(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取我的收藏")
    @GetMapping("/my-collects")
    public Result<IPage<RecipeVO>> getMyCollects(
            @CurrentUserId Long userId,
            @ModelAttribute PageQuery pageQuery) {
        return Result.success(recipeService.getMyCollects(userId, pageQuery.getCurrent(), pageQuery.getSize()));
    }

    @Operation(summary = "评分")
    @PostMapping("/{id}/rate")
    public Result<Void> rateRecipe(
            @CurrentUserId Long userId,
            @PathVariable Long id,
            @RequestParam Integer score) {
        recipeService.rateRecipe(userId, id, score);
        return Result.success();
    }

    @Operation(summary = "AI生成食谱")
    @PostMapping("/ai-generate")
    public Result<RecipeDetailVO> generateRecipe(
            @CurrentUserId Long userId,
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String dietType,
            @RequestParam(required = false) String mealType,
            @RequestParam(required = false) Integer calories,
            @RequestParam(required = false) String preferences) {
        return Result.success(recipeService.generateRecipe(userId, ingredients, dietType, mealType, calories, preferences));
    }

    @Operation(summary = "获取推荐食谱")
    @GetMapping("/recommended")
    public Result<IPage<RecipeVO>> getRecommendedRecipes(
            @CurrentUserId(required = false) Long userId,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(recipeService.getRecommendedRecipes(userId, size));
    }
}
