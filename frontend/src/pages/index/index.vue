<template>
  <view class="index-page">
    <!-- 顶部区域 -->
    <view class="header">
      <view class="user-info" @tap="goToProfile">
        <image v-if="userInfo?.avatar" :src="userInfo.avatar" class="avatar" />
        <view v-else class="avatar-placeholder">
          <text class="icon">user</text>
        </view>
        <text class="nickname">{{ userInfo?.nickname || '点击登录' }}</text>
      </view>
      <view class="search-bar" @tap="goToSearch">
        <text class="icon">search</text>
        <text class="placeholder">搜索食谱、食材...</text>
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="banner-section">
      <swiper
        class="banner-swiper"
        :indicator-dots="true"
        :autoplay="true"
        :interval="5000"
        :circular="true"
      >
        <swiper-item v-for="banner in banners" :key="banner.id" @tap="handleBannerClick(banner)">
          <image :src="banner.image" mode="aspectFill" class="banner-image" />
        </swiper-item>
      </swiper>
    </view>

    <!-- 今日数据概览 -->
    <view class="today-stats">
      <view class="stat-card">
        <text class="stat-value">{{ todayStats.calories || 0 }}</text>
        <text class="stat-label">今日摄入 (kcal)</text>
      </view>
      <view class="stat-card">
        <text class="stat-value">{{ todayStats.exercise || 0 }}</text>
        <text class="stat-label">今日运动 (min)</text>
      </view>
      <view class="stat-card">
        <text class="stat-value">{{ todayStats.weight || '--' }}</text>
        <text class="stat-label">当前体重 (kg)</text>
      </view>
    </view>

    <!-- 功能入口 -->
    <view class="quick-actions">
      <view class="action-item" @tap="goToRecipeGenerate">
        <view class="action-icon bg-purple">AI</view>
        <text class="action-text">AI生成食谱</text>
      </view>
      <view class="action-item" @tap="goToImageAnalysis">
        <view class="action-icon bg-green">拍摄</view>
        <text class="action-text">食物分析</text>
      </view>
      <view class="action-item" @tap="goToRecommended">
        <view class="action-icon bg-blue">推荐</view>
        <text class="action-text">个性化推荐</text>
      </view>
      <view class="action-item" @tap="goToHealthRecord">
        <view class="action-icon bg-orange">记录</view>
        <text class="action-text">健康档案</text>
      </view>
    </view>

    <!-- 推荐食谱 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">为您推荐</text>
        <text class="section-more" @tap="goToRecipeList">更多</text>
      </view>
      <scroll-view class="recipe-scroll" scroll-x="true">
        <view
          v-for="recipe in recommendedRecipes"
          :key="recipe.id"
          class="recipe-card"
          @tap="goToRecipeDetail(recipe.id)"
        >
          <image :src="recipe.coverImage" mode="aspectFill" class="recipe-image" />
          <view class="recipe-info">
            <text class="recipe-name">{{ recipe.name }}</text>
            <view class="recipe-tags">
              <text class="tag">{{ recipe.calories }}kcal</text>
              <text class="tag">{{ recipe.cookingTime }}分钟</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 健康资讯 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">健康资讯</text>
        <text class="section-more" @tap="goToArticleList">更多</text>
      </view>
      <view class="article-list">
        <view
          v-for="article in articles"
          :key="article.id"
          class="article-item"
          @tap="goToArticleDetail(article.id)"
        >
          <image :src="article.coverImage" mode="aspectFill" class="article-image" />
          <view class="article-info">
            <text class="article-title">{{ article.title }}</text>
            <text class="article-summary">{{ article.summary }}</text>
            <text class="article-meta">{{ article.publishDate }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { contentApi } from '@/api/content'
import { recipeApi } from '@/api/recipe'
import { healthApi } from '@/api/health'
import type { Banner, Article, Recipe } from '@/types'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const banners = ref<Banner[]>([])
const articles = ref<Article[]>([])
const recommendedRecipes = ref<Recipe[]>([])
const todayStats = ref({
  calories: 0,
  exercise: 0,
  weight: '--'
})

onMounted(async () => {
  loadBanners()
  loadArticles()
  loadRecommendedRecipes()
  loadTodayStats()
  if (userStore.hasUserInfo) {
    userInfo.value = userStore.userInfo
  }
})

const loadBanners = async () => {
  try {
    const res = await contentApi.getBanners()
    if (res.code === 200) {
      banners.value = res.data.filter((b: Banner) => b.status === 'active')
    }
  } catch (e) {
    console.error('加载轮播图失败', e)
  }
}

const loadArticles = async () => {
  try {
    const res = await contentApi.getArticles({ page: 1, size: 3 })
    if (res.code === 200) {
      articles.value = res.data.records
    }
  } catch (e) {
    console.error('加载资讯失败', e)
  }
}

const loadRecommendedRecipes = async () => {
  try {
    const res = await recipeApi.getRecommendedRecipes()
    if (res.code === 200) {
      recommendedRecipes.value = res.data.slice(0, 5)
    }
  } catch (e) {
    console.error('加载推荐食谱失败', e)
  }
}

const loadTodayStats = async () => {
  try {
    const res = await healthApi.getTodayRecord()
    if (res.code === 200 && res.data) {
      todayStats.value = {
        calories: res.data.calorieIntake || 0,
        exercise: res.data.exerciseDuration || 0,
        weight: res.data.weight || '--'
      }
    }
  } catch (e) {
    console.error('加载今日数据失败', e)
  }
}

const handleBannerClick = (banner: Banner) => {
  if (banner.linkType === 'recipe') {
    uni.navigateTo({ url: `/pages/recipe/detail?id=${banner.link}` })
  } else if (banner.linkType === 'article') {
    uni.navigateTo({ url: `/pages/article/detail?id=${banner.link}` })
  }
}

const goToProfile = () => {
  uni.switchTab({ url: '/pages/user/profile' })
}

const goToSearch = () => {
  uni.navigateTo({ url: '/pages/recipe/list?search=1' })
}

const goToRecipeGenerate = () => {
  uni.navigateTo({ url: '/pages/recipe/ai-generate' })
}

const goToImageAnalysis = () => {
  uni.switchTab({ url: '/pages/analysis/image' })
}

const goToRecommended = () => {
  uni.navigateTo({ url: '/pages/recipe/list?type=recommended' })
}

const goToHealthRecord = () => {
  uni.switchTab({ url: '/pages/health/record' })
}

const goToRecipeList = () => {
  uni.switchTab({ url: '/pages/recipe/list' })
}

const goToRecipeDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/recipe/detail?id=${id}` })
}

const goToArticleList = () => {
  uni.navigateTo({ url: '/pages/article/list' })
}

const goToArticleDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/article/detail?id=${id}` })
}
</script>

<style lang="scss" scoped>
.index-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30rpx;
  padding-top: calc(var(--status-bar-height) + 30rpx);

  .user-info {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      margin-right: 16rpx;
    }

    .avatar-placeholder {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.3);
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16rpx;
    }

    .nickname {
      color: #ffffff;
      font-size: 32rpx;
      font-weight: 500;
    }
  }

  .search-bar {
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50rpx;
    padding: 20rpx 30rpx;
    display: flex;
    align-items: center;

    .icon {
      color: rgba(255, 255, 255, 0.6);
      margin-right: 10rpx;
    }

    .placeholder {
      color: rgba(255, 255, 255, 0.6);
      font-size: 26rpx;
    }
  }
}

.banner-section {
  margin: 20rpx;

  .banner-swiper {
    height: 320rpx;
    border-radius: 16rpx;
    overflow: hidden;
  }

  .banner-image {
    width: 100%;
    height: 100%;
  }
}

.today-stats {
  display: flex;
  justify-content: space-around;
  margin: 0 20rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx 0;

  .stat-card {
    display: flex;
    flex-direction: column;
    align-items: center;

    .stat-value {
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 8rpx;
    }

    .stat-label {
      font-size: 22rpx;
      color: #999;
    }
  }
}

.quick-actions {
  display: flex;
  justify-content: space-around;
  margin: 20rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx 0;

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    .action-icon {
      width: 100rpx;
      height: 100rpx;
      border-radius: 20rpx;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 32rpx;
      font-weight: bold;
      color: #ffffff;
      margin-bottom: 12rpx;

      &.bg-purple {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.bg-green {
        background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
      }

      &.bg-blue {
        background: linear-gradient(135deg, #2193b0 0%, #6dd5ed 100%);
      }

      &.bg-orange {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }
    }

    .action-text {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.section {
  margin: 20rpx;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .section-more {
      font-size: 24rpx;
      color: #999;
    }
  }

  .recipe-scroll {
    white-space: nowrap;

    .recipe-card {
      display: inline-block;
      width: 240rpx;
      margin-right: 20rpx;
      vertical-align: top;

      .recipe-image {
        width: 240rpx;
        height: 180rpx;
        border-radius: 12rpx;
        margin-bottom: 12rpx;
      }

      .recipe-info {
        .recipe-name {
          display: block;
          font-size: 26rpx;
          color: #333;
          margin-bottom: 8rpx;
          white-space: normal;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
        }

        .recipe-tags {
          display: flex;

          .tag {
            font-size: 20rpx;
            color: #666;
            background: #f5f5f5;
            padding: 4rpx 12rpx;
            border-radius: 4rpx;
            margin-right: 8rpx;
          }
        }
      }
    }
  }

  .article-list {
    .article-item {
      display: flex;
      margin-bottom: 20rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .article-image {
        width: 200rpx;
        height: 140rpx;
        border-radius: 12rpx;
        margin-right: 20rpx;
        flex-shrink: 0;
      }

      .article-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .article-title {
          font-size: 28rpx;
          color: #333;
          font-weight: 500;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .article-summary {
          font-size: 24rpx;
          color: #666;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .article-meta {
          font-size: 22rpx;
          color: #999;
        }
      }
    }
  }
}
</style>
