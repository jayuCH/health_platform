-- 健康饮食管理系统数据库初始化脚本
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS healthy_diet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE healthy_diet;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` VARCHAR(64) DEFAULT NULL COMMENT '微信openid',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `birthday` VARCHAR(20) DEFAULT NULL COMMENT '生日',
  `height` INT DEFAULT NULL COMMENT '身高cm',
  `status` TINYINT DEFAULT 1 COMMENT '状态 0禁用 1正常',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 健康目标表
CREATE TABLE IF NOT EXISTS `health_goal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '目标ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `target_weight` DECIMAL(5,2) DEFAULT NULL COMMENT '目标体重kg',
  `current_weight` DECIMAL(5,2) DEFAULT NULL COMMENT '当前体重kg',
  `height` INT DEFAULT NULL COMMENT '身高cm',
  `target_calories` INT DEFAULT NULL COMMENT '目标热量kcal',
  `activity_level` VARCHAR(20) DEFAULT NULL COMMENT '活动水平low/medium/high',
  `diet_type` VARCHAR(30) DEFAULT NULL COMMENT '饮食类型balanced/low-carb/high-protein/vegetarian',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康目标表';

-- 食谱分类表
CREATE TABLE IF NOT EXISTS `recipe_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱分类表';

-- 食谱表
CREATE TABLE IF NOT EXISTS `recipe` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '食谱ID',
  `name` VARCHAR(100) NOT NULL COMMENT '食谱名称',
  `description` TEXT COMMENT '描述',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
  `calories` DECIMAL(7,2) DEFAULT NULL COMMENT '热量kcal',
  `protein` DECIMAL(6,2) DEFAULT NULL COMMENT '蛋白质g',
  `fat` DECIMAL(6,2) DEFAULT NULL COMMENT '脂肪g',
  `carbohydrate` DECIMAL(6,2) DEFAULT NULL COMMENT '碳水化合物g',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `cooking_time` INT DEFAULT NULL COMMENT '烹饪时间分钟',
  `difficulty` VARCHAR(20) DEFAULT NULL COMMENT '难度easy/medium/hard',
  `tags` JSON DEFAULT NULL COMMENT '标签JSON数组',
  `ingredients` JSON DEFAULT NULL COMMENT '食材JSON数组',
  `steps` JSON DEFAULT NULL COMMENT '制作步骤JSON数组',
  `rating` DECIMAL(3,2) DEFAULT 0.00 COMMENT '平均评分',
  `rating_count` INT DEFAULT 0 COMMENT '评分人数',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `collect_count` INT DEFAULT 0 COMMENT '收藏次数',
  `is_ai_generated` TINYINT DEFAULT 0 COMMENT '是否AI生成',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_rating` (`rating`, `rating_count`),
  KEY `idx_view_count` (`view_count`),
  FULLTEXT KEY `ft_name_desc` (`name`, `description`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱表';

-- 食谱收藏表
CREATE TABLE IF NOT EXISTS `recipe_collect` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `recipe_id` BIGINT NOT NULL COMMENT '食谱ID',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recipe` (`user_id`, `recipe_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱收藏表';

-- 食谱评分表
CREATE TABLE IF NOT EXISTS `recipe_rating` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评分ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `recipe_id` BIGINT NOT NULL COMMENT '食谱ID',
  `score` TINYINT NOT NULL COMMENT '评分1-5',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_recipe` (`user_id`, `recipe_id`),
  KEY `idx_recipe_id` (`recipe_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食谱评分表';

-- 食物分析表
CREATE TABLE IF NOT EXISTS `food_analysis` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `image` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `foods` JSON DEFAULT NULL COMMENT '识别到的食物JSON数组',
  `total_calories` DECIMAL(7,2) DEFAULT 0.00 COMMENT '总热量kcal',
  `total_protein` DECIMAL(6,2) DEFAULT 0.00 COMMENT '总蛋白质g',
  `total_fat` DECIMAL(6,2) DEFAULT 0.00 COMMENT '总脂肪g',
  `total_carbohydrate` DECIMAL(6,2) DEFAULT 0.00 COMMENT '总碳水g',
  `ai_model` VARCHAR(50) DEFAULT NULL COMMENT 'AI模型',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='食物分析表';

-- 健康档案表
CREATE TABLE IF NOT EXISTS `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `record_date` VARCHAR(20) NOT NULL COMMENT '记录日期YYYY-MM-DD',
  `weight` DECIMAL(5,2) DEFAULT NULL COMMENT '体重kg',
  `body_fat` DECIMAL(5,2) DEFAULT NULL COMMENT '体脂率%',
  `muscle` DECIMAL(5,2) DEFAULT NULL COMMENT '肌肉量kg',
  `water` DECIMAL(5,2) DEFAULT NULL COMMENT '水分kg',
  `sleep_hours` DECIMAL(4,2) DEFAULT NULL COMMENT '睡眠时长小时',
  `sleep_quality` VARCHAR(20) DEFAULT NULL COMMENT '睡眠质量poor/fair/good/excellent',
  `exercise_duration` INT DEFAULT NULL COMMENT '运动时长分钟',
  `exercise_type` VARCHAR(50) DEFAULT NULL COMMENT '运动类型',
  `exercise_calories` INT DEFAULT NULL COMMENT '运动消耗kcal',
  `steps` INT DEFAULT NULL COMMENT '步数',
  `calorie_intake` INT DEFAULT NULL COMMENT '热量摄入kcal',
  `water_intake` INT DEFAULT NULL COMMENT '饮水量ml',
  `notes` TEXT COMMENT '备注',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `record_date`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康档案表';

-- 资讯表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片',
  `content` LONGTEXT COMMENT '内容',
  `author` VARCHAR(50) DEFAULT NULL COMMENT '作者',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `comment_count` INT DEFAULT 0 COMMENT '评论次数',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `tags` JSON DEFAULT NULL COMMENT '标签JSON数组',
  `status` TINYINT DEFAULT 1 COMMENT '状态0草稿1已发布',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  KEY `idx_publish_time` (`publish_time`),
  FULLTEXT KEY `ft_title` (`title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资讯表';

-- 轮播图表
CREATE TABLE IF NOT EXISTS `banner` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image` VARCHAR(500) NOT NULL COMMENT '图片',
  `link` VARCHAR(255) DEFAULT NULL COMMENT '链接',
  `link_type` VARCHAR(20) DEFAULT 'none' COMMENT '链接类型url/recipe/article/none',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态inactive/active',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- AI日志表
CREATE TABLE IF NOT EXISTS `ai_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `request_type` VARCHAR(50) NOT NULL COMMENT '请求类型recipe_generate/image_analysis',
  `request_params` TEXT COMMENT '请求参数',
  `response` TEXT COMMENT '响应结果',
  `duration` INT DEFAULT NULL COMMENT '调用耗时ms',
  `status` VARCHAR(20) NOT NULL COMMENT '状态success/failed',
  `error_msg` TEXT COMMENT '错误信息',
  `ai_model` VARCHAR(50) DEFAULT NULL COMMENT 'AI模型',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_request_type` (`request_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI日志表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '配置描述',
  `group_name` VARCHAR(50) DEFAULT 'default' COMMENT '分组',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `deleted` TINYINT DEFAULT 0 COMMENT '删除标记',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_group_name` (`group_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入初始数据

-- 插入默认食谱分类
INSERT INTO `recipe_category` (`name`, `icon`, `sort`) VALUES
('早餐', 'breakfast', 1),
('午餐', 'lunch', 2),
('晚餐', 'dinner', 3),
('健身餐', 'fitness', 4),
('减脂餐', 'diet', 5),
('素食', 'vegetarian', 6),
('甜品', 'dessert', 7);

-- 插入默认系统配置
INSERT INTO `system_config` (`config_key`, `config_value`, `description`, `group_name`) VALUES
('app.name', '健康饮食', '应用名称', 'app'),
('app.version', '1.0.0', '应用版本', 'app'),
('ai.enabled', 'true', '是否启用AI功能', 'ai'),
('ai.recipe.model', 'gpt-3.5-turbo', 'AI食谱生成模型', 'ai'),
('ai.image.model', 'gpt-4-vision-preview', 'AI图像识别模型', 'ai'),
('recommendation.enabled', 'true', '是否启用推荐功能', 'recommendation'),
('recommendation.algorithm', 'collaborative', '推荐算法', 'recommendation');
