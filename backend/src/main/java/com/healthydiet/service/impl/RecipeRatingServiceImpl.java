package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.entity.Recipe;
import com.healthydiet.entity.RecipeRating;
import com.healthydiet.mapper.RecipeRatingMapper;
import com.healthydiet.service.RecipeRatingService;
import com.healthydiet.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 食谱评分服务实现
 */
@Service
@RequiredArgsConstructor
public class RecipeRatingServiceImpl extends ServiceImpl<RecipeRatingMapper, RecipeRating> implements RecipeRatingService {

    private final RecipeService recipeService;

    @Override
    @Transactional
    public void saveOrUpdateRating(Long userId, Long recipeId, Integer score) {
        RecipeRating rating = getOne(new LambdaQueryWrapper<RecipeRating>()
                .eq(RecipeRating::getUserId, userId)
                .eq(RecipeRating::getRecipeId, recipeId));

        Integer oldScore = null;
        if (rating != null) {
            oldScore = rating.getScore();
        }

        rating = rating != null ? rating : new RecipeRating();
        rating.setUserId(userId);
        rating.setRecipeId(recipeId);
        rating.setScore(score);
        saveOrUpdate(rating);

        // 更新食谱平均评分
        Recipe recipe = recipeService.getById(recipeId);
        if (recipe != null) {
            Integer newCount = recipe.getRatingCount();
            BigDecimal newRating;

            if (oldScore == null) {
                newCount = recipe.getRatingCount() + 1;
                newRating = calculateNewRating(recipe.getRating(), recipe.getRatingCount(), score);
            } else {
                newRating = calculateUpdatedRating(recipe.getRating(), recipe.getRatingCount(), oldScore, score);
            }

            recipe.setRatingCount(newCount);
            recipe.setRating(newRating);
            recipeService.updateById(recipe);
        }
    }

    private BigDecimal calculateNewRating(BigDecimal currentRating, Integer currentCount, Integer newScore) {
        if (currentCount == 0) {
            return BigDecimal.valueOf(newScore);
        }
        return currentRating.multiply(BigDecimal.valueOf(currentCount))
                .add(BigDecimal.valueOf(newScore))
                .divide(BigDecimal.valueOf(currentCount + 1), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateUpdatedRating(BigDecimal currentRating, Integer currentCount, Integer oldScore, Integer newScore) {
        BigDecimal total = currentRating.multiply(BigDecimal.valueOf(currentCount))
                .subtract(BigDecimal.valueOf(oldScore))
                .add(BigDecimal.valueOf(newScore));
        return total.divide(BigDecimal.valueOf(currentCount), 2, RoundingMode.HALF_UP);
    }
}
