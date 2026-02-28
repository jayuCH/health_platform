package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.entity.RecipeCategory;
import com.healthydiet.mapper.RecipeCategoryMapper;
import com.healthydiet.service.RecipeCategoryService;
import org.springframework.stereotype.Service;

/**
 * 食谱分类服务实现
 */
@Service
public class RecipeCategoryServiceImpl extends ServiceImpl<RecipeCategoryMapper, RecipeCategory> implements RecipeCategoryService {
}
