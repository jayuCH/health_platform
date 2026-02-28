package com.healthydiet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.healthydiet.common.PageQuery;
import com.healthydiet.common.Result;
import com.healthydiet.entity.ArticleCategory;
import com.healthydiet.entity.Banner;
import com.healthydiet.service.ArticleCategoryService;
import com.healthydiet.service.ArticleService;
import com.healthydiet.service.BannerService;
import com.healthydiet.vo.ArticleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内容管理控制器
 */
@Tag(name = "内容管理", description = "资讯和轮播图相关接口")
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private final ArticleService articleService;
    private final ArticleCategoryService articleCategoryService;
    private final BannerService bannerService;

    @Operation(summary = "获取轮播图")
    @GetMapping("/banners")
    public Result<List<Banner>> getBanners() {
        return Result.success(bannerService.getActiveBanners());
    }

    @Operation(summary = "获取资讯列表")
    @GetMapping("/articles")
    public Result<IPage<ArticleVO>> getArticles(
            @AuthenticationPrincipal(required = false) Long userId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @ModelAttribute PageQuery pageQuery) {
        return Result.success(articleService.pageArticles(userId, categoryId, keyword, pageQuery));
    }

    @Operation(summary = "获取资讯详情")
    @GetMapping("/article/{id}")
    public Result<ArticleVO> getArticleDetail(
            @AuthenticationPrincipal(required = false) Long userId,
            @PathVariable Long id) {
        return Result.success(articleService.getArticleDetail(userId, id));
    }

    @Operation(summary = "点赞/取消点赞")
    @PostMapping("/article/{id}/like")
    public Result<Void> toggleLike(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long id) {
        articleService.toggleLike(userId, id);
        return Result.success();
    }

    @Operation(summary = "获取分类列表")
    @GetMapping("/categories")
    public Result<List<ArticleCategory>> getCategories() {
        return Result.success(articleCategoryService.list());
    }
}
