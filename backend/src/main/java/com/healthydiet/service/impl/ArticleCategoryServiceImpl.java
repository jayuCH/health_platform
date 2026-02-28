package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.entity.ArticleCategory;
import com.healthydiet.mapper.ArticleCategoryMapper;
import com.healthydiet.service.ArticleCategoryService;
import org.springframework.stereotype.Service;

/**
 * 资讯分类服务实现
 */
@Service
public class ArticleCategoryServiceImpl extends ServiceImpl<ArticleCategoryMapper, ArticleCategory> implements ArticleCategoryService {
}
