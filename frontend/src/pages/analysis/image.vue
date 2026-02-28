<template>
  <view class="analysis-page">
    <!-- 拍照/上传区域 -->
    <view class="upload-section">
      <view v-if="!imagePreview" class="upload-box" @tap="chooseImage">
        <text class="upload-icon">📷</text>
        <text class="upload-text">点击拍照或上传图片</text>
      </view>
      <view v-else class="preview-box">
        <image :src="imagePreview" mode="aspectFill" class="preview-image" />
        <text class="retake-btn" @tap="chooseImage">重新拍照</text>
      </view>

      <button class="analyze-btn" @tap="analyzeImage" :disabled="!imagePreview || analyzing">
        {{ analyzing ? '分析中...' : '开始分析' }}
      </button>
    </view>

    <!-- 分析结果 -->
    <view v-if="analysisResult" class="result-section">
      <view class="summary-card">
        <text class="card-title">分析结果</text>
        <view class="nutrition-total">
          <view class="total-item">
            <text class="total-value">{{ analysisResult.totalCalories }}</text>
            <text class="total-label">总热量</text>
          </view>
          <view class="total-item">
            <text class="total-value">{{ analysisResult.totalProtein }}g</text>
            <text class="total-label">蛋白质</text>
          </view>
          <view class="total-item">
            <text class="total-value">{{ analysisResult.totalFat }}g</text>
            <text class="total-label">脂肪</text>
          </view>
          <view class="total-item">
            <text class="total-value">{{ analysisResult.totalCarbohydrate }}g</text>
            <text class="total-label">碳水</text>
          </view>
        </view>
      </view>

      <view class="foods-section">
        <text class="section-title">识别到的食物</text>
        <view
          v-for="food in analysisResult.foods"
          :key="food.name"
          class="food-item"
          @tap="showFoodDetail(food)"
        >
          <view class="food-info">
            <text class="food-name">{{ food.name }}</text>
            <view class="food-nutrition">
              <text class="nutri-item">{{ food.calories }}kcal</text>
              <text class="nutri-item">蛋白质{{ food.protein }}g</text>
            </view>
            <view class="confidence">
              <text class="confidence-label">置信度：</text>
              <text class="confidence-value">{{ (food.confidence * 100).toFixed(0) }}%</text>
            </view>
          </view>
          <text class="arrow">></text>
        </view>
      </view>

      <!-- AI建议 -->
      <view class="suggestion-section">
        <text class="section-title">AI建议</text>
        <view class="suggestion-content">
          <text class="suggestion-text">
            根据今日摄入建议：{{ getSuggestion() }}
          </text>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <button class="action-btn secondary" @tap="addToRecord">记录到健康档案</button>
        <button class="action-btn primary" @tap="findRecipes">找相似食谱</button>
      </view>
    </view>

    <!-- 历史记录 -->
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">分析历史</text>
        <text class="section-more" @tap="goToHistory">全部</text>
      </view>
      <view v-if="historyList.length === 0" class="empty-tip">
        <text>暂无分析记录</text>
      </view>
      <view v-else class="history-list">
        <view
          v-for="item in historyList"
          :key="item.id"
          class="history-item"
          @tap="viewHistory(item.id)"
        >
          <image :src="item.image" mode="aspectFill" class="history-image" />
          <view class="history-info">
            <text class="history-calories">{{ item.totalCalories }}kcal</text>
            <text class="history-time">{{ formatTime(item.analysisTime) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { analysisApi } from '@/api/analysis'
import { healthApi } from '@/api/health'
import { recipeApi } from '@/api/recipe'
import type { FoodAnalysis, AnalyzedFood } from '@/types'

const imagePreview = ref('')
const analyzing = ref(false)
const analysisResult = ref<FoodAnalysis | null>(null)
const historyList = ref<any[]>([])
const todayCalories = ref(0)

onMounted(() => {
  loadHistory()
  loadTodayStats()
})

const loadHistory = async () => {
  try {
    const res = await analysisApi.getHistory({ page: 1, size: 5 })
    if (res.code === 200) {
      historyList.value = res.data.records
    }
  } catch (e) {
    console.error('加载历史记录失败', e)
  }
}

const loadTodayStats = async () => {
  try {
    const res = await healthApi.getTodayRecord()
    if (res.code === 200 && res.data) {
      todayCalories.value = res.data.calorieIntake || 0
    }
  } catch (e) {
    console.error('加载今日数据失败', e)
  }
}

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: (res) => {
      imagePreview.value = res.tempFilePaths[0]
      analysisResult.value = null
    }
  })
}

const analyzeImage = async () => {
  if (!imagePreview.value) return

  analyzing.value = true
  try {
    const res = await analysisApi.analyzeImage(imagePreview.value)
    if (res.code === 200) {
      analysisResult.value = res.data
      uni.showToast({ title: '分析完成', icon: 'success' })
      loadHistory()
    }
  } catch (e: any) {
    uni.showToast({ title: e.message || '分析失败', icon: 'none' })
  } finally {
    analyzing.value = false
  }
}

const getSuggestion = () => {
  if (!analysisResult.value) return ''

  const total = todayCalories.value + analysisResult.value.totalCalories
  const target = 2000 // 假设目标热量

  if (total < target * 0.7) {
    return '今日摄入不足，建议适当增加优质蛋白质摄入'
  } else if (total > target * 1.1) {
    return '今日摄入已超标，建议晚餐清淡一些或增加运动量'
  } else {
    return '今日摄入适中，继续保持健康饮食习惯'
  }
}

const showFoodDetail = (food: AnalyzedFood) => {
  uni.showModal({
    title: food.name,
    content: `热量：${food.calories}kcal\n蛋白质：${food.protein}g\n脂肪：${food.fat}g\n碳水：${food.carbohydrate}g`,
    showCancel: false
  })
}

const addToRecord = () => {
  if (!analysisResult.value) return

  uni.navigateTo({
    url: `/pages/health/record?type=analysis&analysisId=${analysisResult.value.id}`
  })
}

const findRecipes = () => {
  if (!analysisResult.value) return

  const foodNames = analysisResult.value.foods.map(f => f.name).join(',')
  uni.navigateTo({
    url: `/pages/recipe/list?keyword=${encodeURIComponent(foodNames)}`
  })
}

const goToHistory = () => {
  uni.navigateTo({ url: '/pages/analysis/history' })
}

const viewHistory = (id: string) => {
  uni.navigateTo({ url: `/pages/analysis/detail?id=${id}` })
}

const formatTime = (time: string) => {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (days > 0) return `${days}天前`
  if (hours > 0) return `${hours}小时前`
  if (minutes > 0) return `${minutes}分钟前`
  return '刚刚'
}
</script>

<style lang="scss" scoped>
.analysis-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.upload-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;

  .upload-box {
    height: 400rpx;
    border: 2rpx dashed #ddd;
    border-radius: 12rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .upload-icon {
      font-size: 80rpx;
      margin-bottom: 20rpx;
    }

    .upload-text {
      font-size: 26rpx;
      color: #999;
    }
  }

  .preview-box {
    .preview-image {
      width: 100%;
      height: 400rpx;
      border-radius: 12rpx;
      margin-bottom: 20rpx;
    }

    .retake-btn {
      display: block;
      text-align: center;
      color: #667eea;
      font-size: 26rpx;
    }
  }

  .analyze-btn {
    width: 100%;
    height: 90rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    font-size: 32rpx;
    font-weight: bold;
    border: none;
    border-radius: 50rpx;
    margin-top: 20rpx;

    &[disabled] {
      opacity: 0.6;
    }
  }
}

.result-section {
  .summary-card {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;

    .card-title {
      display: block;
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
    }

    .nutrition-total {
      display: flex;
      justify-content: space-around;

      .total-item {
        display: flex;
        flex-direction: column;
        align-items: center;

        .total-value {
          font-size: 36rpx;
          font-weight: bold;
          color: #667eea;
          margin-bottom: 8rpx;
        }

        .total-label {
          font-size: 22rpx;
          color: #999;
        }
      }
    }
  }

  .foods-section {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;

    .section-title {
      display: block;
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 20rpx;
    }

    .food-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .food-info {
        flex: 1;

        .food-name {
          display: block;
          font-size: 28rpx;
          color: #333;
          margin-bottom: 8rpx;
        }

        .food-nutrition {
          display: flex;
          gap: 20rpx;
          margin-bottom: 8rpx;

          .nutri-item {
            font-size: 24rpx;
            color: #666;
          }
        }

        .confidence {
          font-size: 22rpx;
          color: #999;

          .confidence-value {
            color: #52c41a;
          }
        }
      }

      .arrow {
        color: #ccc;
        font-size: 28rpx;
      }
    }
  }

  .suggestion-section {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;

    .section-title {
      display: block;
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 16rpx;
    }

    .suggestion-content {
      background: rgba(102, 126, 234, 0.1);
      border-radius: 12rpx;
      padding: 20rpx;

      .suggestion-text {
        font-size: 26rpx;
        color: #666;
        line-height: 1.6;
      }
    }
  }

  .action-buttons {
    display: flex;
    gap: 20rpx;

    .action-btn {
      flex: 1;
      height: 80rpx;
      border: none;
      border-radius: 40rpx;
      font-size: 28rpx;

      &.secondary {
        background: #f5f5f5;
        color: #333;
      }

      &.primary {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #ffffff;
      }
    }
  }
}

.history-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }

    .section-more {
      font-size: 24rpx;
      color: #999;
    }
  }

  .empty-tip {
    text-align: center;
    padding: 60rpx 0;
    color: #999;
    font-size: 26rpx;
  }

  .history-list {
    display: flex;
    gap: 16rpx;
    overflow-x: auto;

    .history-item {
      width: 200rpx;
      flex-shrink: 0;

      .history-image {
        width: 200rpx;
        height: 150rpx;
        border-radius: 12rpx;
        margin-bottom: 12rpx;
      }

      .history-info {
        .history-calories {
          display: block;
          font-size: 26rpx;
          font-weight: bold;
          color: #333;
          margin-bottom: 4rpx;
        }

        .history-time {
          font-size: 22rpx;
          color: #999;
        }
      }
    }
  }
}
</style>
