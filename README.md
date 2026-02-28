# 健康饮食管理系统

基于微信小程序的健康饮食管理系统，采用前后端分离架构，集成AI功能实现智能食谱生成和食物识别。

## 项目概述

本项目是一个完整的健康饮食管理解决方案，帮助用户：
- 记录和管理个人健康数据
- 发现和收藏健康食谱
- 通过AI生成个性化食谱
- 上传食物图片进行营养分析
- 查看健康趋势和数据可视化

## 技术架构

### 前端
- **框架**: UniApp + Vue3
- **状态管理**: Pinia
- **UI组件**: Tailwind UI
- **图表**: ECharts
- **工具库**: Day.js

### 后端
- **框架**: Spring Boot 3.2
- **Java版本**: Java 17
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus
- **缓存**: Redis
- **API文档**: Knife4j (Swagger)
- **安全**: Spring Security + JWT

### AI集成
- **食谱生成**: 支持接入OpenAI等AI API
- **图像识别**: 支持食物图像识别和营养分析

## 项目结构

```
healthy-diet-system/
├── frontend/              # 前端项目 (UniApp)
│   ├── src/
│   │   ├── pages/       # 页面
│   │   ├── components/  # 组件
│   │   ├── api/         # API接口
│   │   ├── store/       # 状态管理
│   │   ├── types/       # TypeScript类型定义
│   │   └── utils/       # 工具函数
│   ├── package.json
│   ├── vite.config.ts
│   └── pages.json
├── backend/              # 后端项目 (Spring Boot)
│   └── src/main/
│       ├── java/com/healthydiet/
│       │   ├── controller/   # 控制器
│       │   ├── service/      # 业务逻辑
│       │   ├── entity/       # 实体类
│       │   ├── mapper/       # 数据访问层
│       │   ├── config/       # 配置类
│       │   ├── dto/          # 数据传输对象
│       │   ├── vo/           # 视图对象
│       │   └── util/         # 工具类
│       └── resources/
│           ├── application.yml
│           └── db/migration/init.sql
└── docs/                 # 项目文档
```

## 核心功能模块

### 1. 用户管理模块
- 手机号/微信登录
- 个人信息维护
- 健康目标设定
- JWT身份认证

### 2. 食谱管理模块
- 食谱分类浏览
- 食谱搜索和筛选
- 食谱详情查看
- 食谱收藏和评分
- AI智能生成食谱

### 3. 图像分析模块
- 上传食物图片
- AI识别食物种类
- 营养成分分析
- 热量计算

### 4. 健康档案模块
- 体重记录
- 运动记录
- 睡眠记录
- 饮水记录
- 热量摄入记录

### 5. 智能推荐模块
- 基于用户画像推荐
- 协同过滤算法
- 个性化食谱推荐

### 6. 数据可视化模块
- 健康趋势图表
- 营养摄入分析
- 体重变化曲线
- 运动数据统计

### 7. 系统管理模块
- 资讯发布
- 轮播图配置
- AI日志审计
- 系统健康检查

## 数据库设计

### 核心表结构

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| health_goal | 健康目标表 |
| recipe | 食谱表 |
| recipe_category | 食谱分类表 |
| recipe_collect | 食谱收藏表 |
| recipe_rating | 食谱评分表 |
| food_analysis | 食物分析表 |
| health_record | 健康档案表 |
| article | 资讯表 |
| banner | 轮播图表 |
| ai_log | AI日志表 |
| system_config | 系统配置表 |

## 快速开始

### 环境要求

- Node.js >= 16.20.0
- Java 17
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 后端启动

1. 创建数据库并执行初始化脚本：
```bash
mysql -u root -p < backend/src/main/resources/db/migration/init.sql
```

2. 修改配置文件 `application.yml` 中的数据库和Redis连接信息

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

4. 访问API文档：http://localhost:8080/api/doc.html

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev:h5
```

3. 编译微信小程序：
```bash
npm run dev:mp-weixin
```

## API接口文档

### 用户接口
- `POST /api/user/sms/send` - 发送验证码
- `POST /api/user/login` - 手机号登录
- `POST /api/user/wx/login` - 微信登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息

### 食谱接口
- `GET /api/recipe/categories` - 获取食谱分类
- `GET /api/recipe/list` - 获取食谱列表
- `GET /api/recipe/{id}` - 获取食谱详情
- `POST /api/recipe/{id}/collect` - 收藏食谱
- `POST /api/recipe/ai-generate` - AI生成食谱

### 分析接口
- `POST /api/analysis/upload` - 上传图片分析
- `GET /api/analysis/history` - 获取分析历史

### 健康档案接口
- `POST /api/health/record` - 保存健康记录
- `GET /api/health/today` - 获取今日数据
- `GET /api/health/trend/{type}` - 获取健康趋势

### 内容接口
- `GET /api/content/banners` - 获取轮播图
- `GET /api/content/articles` - 获取资讯列表
- `GET /api/content/article/{id}` - 获取资讯详情

## 配置说明

### 后端配置 (application.yml)

```yaml
# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/healthy_diet
    username: root
    password: your_password

# Redis配置
  data:
    redis:
      host: localhost
      port: 6379

# AI配置
ai:
  api-url: https://api.openai.com/v1/chat/completions
  api-key: your-api-key
  model: gpt-3.5-turbo

# JWT配置
jwt:
  secret: your-secret-key
  expiration: 604800000
```

### 前端配置

修改 `frontend/src/utils/http.ts` 中的 `BASE_URL` 配置后端API地址。

## 开发计划

- [x] 项目架构设计
- [x] 数据库表结构设计
- [x] 用户管理模块
- [x] 食谱管理模块
- [x] 图像分析模块
- [x] 健康档案模块
- [x] 内容管理模块
- [ ] 推荐算法优化
- [ ] AI API深度集成
- [ ] 单元测试完善
- [ ] 性能优化

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进项目。

## 许可证

MIT License
