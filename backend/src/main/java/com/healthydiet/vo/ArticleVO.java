package com.healthydiet.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资讯VO
 */
@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String coverImage;
    private String content;
    private String author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Long categoryId;
    private String categoryName;
    private List<String> tags;
    private Boolean isLiked;
    private LocalDateTime publishTime;
}
