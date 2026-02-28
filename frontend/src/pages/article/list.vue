<template>
  <view class="article-list-page">
    <!-- 分类筛选 -->
    <scroll-view class="category-scroll" scroll-x="true">
      <view
        v-for="category in categories"
        :key="category.id"
        class="category-item"
        :class="{ active: selectedCategory === category.id }"
        @tap="selectCategory(category.id)"
      >
        <text class="category-text">{{ category.name }}</text>
      </view>
    </scroll-view>

    <!-- 搜索栏 -->
    <view class="search-bar">
      <input
        v-model="keyword"
        placeholder="搜索健康资讯..."
        class="search-input"
        @confirm="handleSearch"
      />
    </view>

    <!-- 资讯列表 -->
    <view class="article-list">
      <view
        v-for="article in articles"
        :key="article.id"
        class="article-card"
        @tap="goToDetail(article.id)"
      >
        <image :src="article.coverImage" mode="aspectFill" class="article-image" />
        <view class="article-content">
          <text class="article-title">{{ article.title }}</text>
          <text class="article-summary">{{ article.summary }}</text>
          <view class="article-footer">
            <view class="article-meta">
              <text class="meta-item">{{ article.categoryName }}</text>
              <text class="meta-item">{{ formatDate(article.publishDate) }}</text>
            </view>
            <view class="article-stats">
              <text class="stat-item">
                <text class="stat-icon">👁</text>
                {{ formatNumber(article.viewCount) }}
              </text>
              <text class="stat-item" :class="{ liked: article.isLiked }" @tap.stop="toggleLike(article)">
                <text class="stat-icon">{{ article.isLiked ? '♥' : '♡' }}</text>
                {{ formatNumber(article.likeCount) }}
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore">
      <text class="load-text">{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
    <view class="no-more" v-else>
      <text class="no-more-text">没有更多了</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { contentApi } from '@/api/content'
import type { Article } from '@/types'

const categories = ref<any[]>([
  { id: '', name: '全部' },
  { id: '1', name: '营养知识' },
  { id: '2', name: '健康食谱' },
  { id: '3', name: '运动健身' },
  { id: '4', name: '养生保健' }
])

const selectedCategory = ref('')
const keyword = ref('')
const articles = ref<Article[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)

onMounted(() => {
  loadArticles()
})

const selectCategory = (id: string) => {
  selectedCategory.value = id
  page.value = 1
  articles.value = []
  loadArticles()
}

const handleSearch = () => {
  page.value = 1
  articles.value = []
  loadArticles()
}

const loadArticles = async () => {
  if (loading.value) return

  loading.value = true
  try {
    const res = await contentApi.getArticles({
      page: page.value,
      size: size.value,
      categoryId: selectedCategory.value || undefined,
      keyword: keyword.value || undefined
    })

    if (res.code === 200) {
      if (page.value === 1) {
        articles.value = res.data.records
      } else {
        articles.value = [...articles.value, ...res.data.records]
      }
      total.value = res.data.total
      hasMore.value = articles.value.length < total.value
    }
  } catch (e) {
    console.error('加载资讯失败', e)
  } finally {
    loading.value = false
  }
}

const toggleLike = async (article: Article) => {
  try {
    if (article.isLiked) {
      await contentApi.unlikeArticle(article.id)
      article.isLiked = false
      article.likeCount--
    } else {
      await contentApi.likeArticle(article.id)
      article.isLiked = true
      article.likeCount++
    }
  } catch (e) {
    console.error('操作失败', e)
  }
}

const goToDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/article/detail?id=${id}` })
}

const formatDate = (date: string) => {
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  const days = Math.floor(diff / 86400000)

  if (days === 0) return '今天'
  if (days === 1) return '昨天'
  if (days < 7) return `${days}天前`
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

const formatNumber = (num: number) => {
  if (num >= 10000) return `${(num / 10000).toFixed(1)}万`
  if (num >= 1000) return `${(num / 1000).toFixed(1)}k`
  return num.toString()
}

// 下拉刷新
onPullDownRefresh(() => {
  page.value = 1
  articles.value = []
  loadArticles().finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 触底加载更多
onReachBottom(() => {
  if (hasMore.value && !loading.value) {
    page.value++
    loadArticles()
  }
})
</script>

<style lang="scss" scoped>
.article-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.category-scroll {
  background: #ffffff;
  padding: 20rpx 0;
  white-space: nowrap;

  .category-item {
    display: inline-block;
    margin: 0 10rpx;
    padding: 12rpx 30rpx;
    border-radius: 30rpx;
    background: #f5f5f5;

    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }

    .category-text {
      font-size: 26rpx;
      color: #333;

      .active & {
        color: #ffffff;
      }
    }
  }
}

.search-bar {
  padding: 20rpx;
  background: #f5f5f5;

  .search-input {
    width: 100%;
    height: 70rpx;
    background: #ffffff;
    border-radius: 35rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
  }
}

.article-list {
  padding: 20rpx;

  .article-card {
    background: #ffffff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;

    .article-image {
      width: 100%;
      height: 320rpx;
    }

    .article-content {
      padding: 24rpx;

      .article-title {
        display: block;
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 12rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .article-summary {
        display: block;
        font-size: 26rpx;
        color: #666;
        line-height: 1.5;
        margin-bottom: 16rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .article-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .article-meta {
          display: flex;
          gap: 16rpx;

          .meta-item {
            font-size: 22rpx;
            color: #999;
          }
        }

        .article-stats {
          display: flex;
          gap: 24rpx;

          .stat-item {
            display: flex;
            align-items: center;
            font-size: 22rpx;
            color: #999;

            &.liked {
              .stat-icon {
                color: #ff4d4f;
              }
            }

            .stat-icon {
              font-size: 26rpx;
              margin-right: 4rpx;
            }
          }
        }
      }
    }
  }
}

.load-more,
.no-more {
  padding: 30rpx;
  text-align: center;

  .load-text,
  .no-more-text {
    font-size: 26rpx;
    color: #999;
  }
}
</style>
