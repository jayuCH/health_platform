package com.healthydiet.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.common.PageQuery;
import com.healthydiet.entity.Article;
import com.healthydiet.entity.ArticleCategory;
import com.healthydiet.entity.ArticleLike;
import com.healthydiet.mapper.ArticleLikeMapper;
import com.healthydiet.mapper.ArticleMapper;
import com.healthydiet.service.ArticleCategoryService;
import com.healthydiet.service.ArticleService;
import com.healthydiet.vo.ArticleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 资讯服务实现
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleCategoryService articleCategoryService;

    @Override
    public IPage<ArticleVO> pageArticles(Long userId, Long categoryId, String keyword, PageQuery pageQuery) {
        Page<Article> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1); // 只查询已发布的

        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword).or().like(Article::getSummary, keyword));
        }

        wrapper.orderByDesc(Article::getPublishTime);

        IPage<Article> articlePage = page(page, wrapper);
        IPage<ArticleVO> voPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());

        List<ArticleVO> voList = articlePage.getRecords().stream()
                .map(article -> convertToVO(article, userId))
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ArticleVO getArticleDetail(Long userId, Long articleId) {
        Article article = getById(articleId);
        if (article == null || article.getStatus() != 1) {
            throw new BusinessException("资讯不存在");
        }

        // 增加浏览次数
        update().setSql("view_count = view_count + 1").eq("id", articleId).update();

        return convertToVO(article, userId);
    }

    @Override
    @Transactional
    public void toggleLike(Long userId, Long articleId) {
        Article article = getById(articleId);
        if (article == null) {
            throw new BusinessException("资讯不存在");
        }

        ArticleLike like = articleLikeMapper.selectOne(
                new LambdaQueryWrapper<ArticleLike>()
                        .eq(ArticleLike::getUserId, userId)
                        .eq(ArticleLike::getArticleId, articleId)
        );

        if (like == null) {
            // 点赞
            like = new ArticleLike();
            like.setUserId(userId);
            like.setArticleId(articleId);
            articleLikeMapper.insert(like);

            // 增加点赞次数
            update().setSql("like_count = like_count + 1").eq("id", articleId).update();
        } else {
            // 取消点赞
            articleLikeMapper.deleteById(like.getId());

            // 减少点赞次数
            update().setSql("like_count = like_count - 1").eq("id", articleId).update();
        }
    }

    private ArticleVO convertToVO(Article article, Long userId) {
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);

        // 解析JSON字段
        if (StrUtil.isNotBlank(article.getTags())) {
            vo.setTags(JSON.parseArray(article.getTags(), String.class));
        }

        // 设置分类名称
        if (article.getCategoryId() != null) {
            var category = articleCategoryService.getById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 检查是否点赞
        if (userId != null) {
            vo.setIsLiked(articleLikeMapper.selectCount(
                    new LambdaQueryWrapper<ArticleLike>()
                            .eq(ArticleLike::getUserId, userId)
                            .eq(ArticleLike::getArticleId, article.getId())
            ) > 0);
        }

        return vo;
    }
}
