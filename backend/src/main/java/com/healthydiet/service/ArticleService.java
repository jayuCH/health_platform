package com.healthydiet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.common.PageQuery;
import com.healthydiet.entity.Article;
import com.healthydiet.vo.ArticleVO;

/**
 * 资讯服务接口
 */
public interface ArticleService extends IService<Article> {

    /**
     * 分页查询资讯列表
     */
    IPage<ArticleVO> pageArticles(Long userId, Long categoryId, String keyword, PageQuery pageQuery);

    /**
     * 获取资讯详情
     */
    ArticleVO getArticleDetail(Long userId, Long articleId);

    /**
     * 点赞/取消点赞
     */
    void toggleLike(Long userId, Long articleId);
}
