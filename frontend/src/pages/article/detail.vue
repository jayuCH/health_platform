<template>
  <view class="article-detail-page">
    <!-- 返回按钮 -->
    <view class="nav-bar">
      <text class="back-btn" @tap="goBack">←</text>
      <text class="nav-title">文章详情</text>
      <text class="share-btn" @tap="share">分享</text>
    </view>

    <!-- 文章内容 -->
    <scroll-view class="content-scroll" scroll-y="true">
      <image :src="article?.coverImage" mode="aspectFill" class="cover-image" />

      <view class="article-content">
        <text class="article-title">{{ article?.title }}</text>

        <view class="article-meta">
          <image v-if="article?.author" src="/static/default-avatar.png" class="author-avatar" />
          <view class="meta-info">
            <text class="author-name">{{ article?.author }}</text>
            <text class="publish-date">{{ formatDate(article?.publishDate) }}</text>
          </view>
          <view class="meta-stats">
            <text class="stat-item">
              <text class="stat-icon">👁</text>
              {{ formatNumber(article?.viewCount) }}
            </text>
          </view>
        </view>

        <view class="article-tags">
          <text v-for="tag in article?.tags" :key="tag" class="tag">{{ tag }}</text>
        </view>

        <view class="article-body">
          <rich-text :nodes="article?.content" class="rich-content"></rich-text>
        </view>
      </view>
    </scroll-view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="comment-input" @tap="showCommentInput">
        <text class="input-placeholder">写评论...</text>
      </view>
      <view class="action-item" @tap="toggleLike">
        <text class="action-icon" :class="{ liked: article?.isLiked }">
          {{ article?.isLiked ? '♥' : '♡' }}
        </text>
        <text class="action-text">{{ formatNumber(article?.likeCount) }}</text>
      </view>
      <view class="action-item" @tap="showComments">
        <text class="action-icon">💬</text>
        <text class="action-text">{{ formatNumber(article?.commentCount || 0) }}</text>
      </view>
      <view class="action-item" @tap="collectArticle">
        <text class="action-icon" :class="{ collected: article?.isCollected }">
          {{ article?.isCollected ? '★' : '☆' }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { contentApi } from '@/api/content'
import type { Article } from '@/types'

const articleId = ref('')
const article = ref<Article | null>(null)

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  articleId.value = currentPage.options?.id || ''
  loadArticle()
})

const loadArticle = async () => {
  if (!articleId.value) return

  try {
    const res = await contentApi.getArticleDetail(articleId.value)
    if (res.code === 200) {
      article.value = res.data
      // 更新页面标题
      uni.setNavigationBarTitle({ title: res.data.title })
    }
  } catch (e) {
    console.error('加载文章失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

const goBack = () => {
  uni.navigateBack()
}

const share = () => {
  uni.showShareMenu({
    withShareTicket: true
  })
}

const toggleLike = async () => {
  if (!article.value) return

  try {
    if (article.value.isLiked) {
      await contentApi.unlikeArticle(article.value.id)
      article.value.isLiked = false
      article.value.likeCount--
    } else {
      await contentApi.likeArticle(article.value.id)
      article.value.isLiked = true
      article.value.likeCount++
    }
  } catch (e) {
    console.error('操作失败', e)
  }
}

const showCommentInput = () => {
  uni.showModal({
    title: '发表评论',
    editable: true,
    placeholderText: '写下你的评论...',
    success: (res) => {
      if (res.confirm && res.content) {
        submitComment(res.content)
      }
    }
  })
}

const submitComment = async (content: string) => {
  // 提交评论逻辑
  uni.showToast({ title: '评论成功', icon: 'success' })
}

const showComments = () => {
  uni.navigateTo({ url: `/pages/article/comment?id=${articleId.value}` })
}

const collectArticle = () => {
  if (!article.value) return
  article.value.isCollected = !article.value.isCollected
  uni.showToast({ title: article.value.isCollected ? '收藏成功' : '取消收藏', icon: 'success' })
}

const formatDate = (date: string) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

const formatNumber = (num?: number) => {
  if (!num) return '0'
  if (num >= 10000) return `${(num / 10000).toFixed(1)}万`
  if (num >= 1000) return `${(num / 1000).toFixed(1)}k`
  return num.toString()
}
</script>

<style lang="scss" scoped>
.article-detail-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: calc(var(--status-bar-height) + 88rpx);
  padding-top: var(--status-bar-height);
  background: #ffffff;
  padding-left: 30rpx;
  padding-right: 30rpx;

  .back-btn {
    font-size: 36rpx;
    color: #333;
  }

  .nav-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }

  .share-btn {
    font-size: 28rpx;
    color: #667eea;
  }
}

.content-scroll {
  flex: 1;
  background: #f5f5f5;

  .cover-image {
    width: 100%;
    height: 400rpx;
  }

  .article-content {
    background: #ffffff;
    padding: 30rpx;
    min-height: calc(100vh - 400rpx - 120rpx);

    .article-title {
      display: block;
      font-size: 40rpx;
      font-weight: bold;
      color: #333;
      line-height: 1.5;
      margin-bottom: 24rpx;
    }

    .article-meta {
      display: flex;
      align-items: center;
      margin-bottom: 20rpx;

      .author-avatar {
        width: 60rpx;
        height: 60rpx;
        border-radius: 50%;
        margin-right: 16rpx;
      }

      .meta-info {
        flex: 1;

        .author-name {
          display: block;
          font-size: 28rpx;
          color: #333;
          margin-bottom: 6rpx;
        }

        .publish-date {
          font-size: 24rpx;
          color: #999;
        }
      }

      .meta-stats {
        .stat-item {
          display: flex;
          align-items: center;
          font-size: 24rpx;
          color: #999;

          .stat-icon {
            font-size: 26rpx;
            margin-right: 4rpx;
          }
        }
      }
    }

    .article-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 12rpx;
      margin-bottom: 30rpx;

      .tag {
        font-size: 22rpx;
        color: #667eea;
        background: rgba(102, 126, 234, 0.1);
        padding: 6rpx 16rpx;
        border-radius: 4rpx;
      }
    }

    .article-body {
      .rich-content {
        font-size: 28rpx;
        color: #333;
        line-height: 1.8;

        :deep(img) {
          max-width: 100%;
          border-radius: 12rpx;
          margin: 20rpx 0;
        }

        :deep(p) {
          margin-bottom: 20rpx;
        }

        :deep(h1),
        :deep(h2),
        :deep(h3) {
          font-size: 32rpx;
          font-weight: bold;
          margin: 30rpx 0 16rpx;
        }

        :deep(ul),
        :deep(ol) {
          padding-left: 40rpx;
          margin-bottom: 20rpx;

          li {
            margin-bottom: 10rpx;
          }
        }
      }
    }
  }
}

.bottom-bar {
  display: flex;
  align-items: center;
  height: calc(100rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  padding: 0 30rpx;
  padding-bottom: env(safe-area-inset-bottom);
  border-top: 1rpx solid #f0f0f0;

  .comment-input {
    flex: 1;
    height: 60rpx;
    background: #f5f5f5;
    border-radius: 30rpx;
    display: flex;
    align-items: center;
    padding: 0 24rpx;
    margin-right: 20rpx;

    .input-placeholder {
      font-size: 26rpx;
      color: #999;
    }
  }

  .action-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-left: 30rpx;

    .action-icon {
      font-size: 36rpx;
      color: #666;
      margin-bottom: 4rpx;

      &.liked,
      &.collected {
        color: #ff4d4f;
      }
    }

    .action-text {
      font-size: 20rpx;
      color: #666;
    }
  }
}
</style>
