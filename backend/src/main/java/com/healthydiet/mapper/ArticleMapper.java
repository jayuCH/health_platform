package com.healthydiet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthydiet.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资讯Mapper
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}
