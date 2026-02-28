<template>
  <view class="recipe-detail-page">
    <!-- 食谱封面 -->
    <view class="cover-section">
      <image :src="recipe?.coverImage" mode="aspectFill" class="cover-image" />
      <view class="back-btn" @tap="goBack">
        <text class="back-icon">←</text>
      </view>
      <view class="actions">
        <text class="action-icon" :class="{ active: recipe?.isCollected }" @tap="toggleCollect">
          {{ recipe?.isCollected ? '♥' : '♡' }}
        </text>
        <text class="action-icon" @tap="shareRecipe">↗</text>
      </view>
    </view>

    <!-- 食谱信息 -->
    <view class="info-section">
      <text class="recipe-name">{{ recipe?.name }}</text>
      <text class="recipe-desc">{{ recipe?.description }}</text>

      <view class="nutrition-bar">
        <view class="nutrition-item">
          <text class="nutrition-label">热量</text>
          <text class="nutrition-value">{{ recipe?.calories }}kcal</text>
        </view>
        <view class="nutrition-item">
          <text class="nutrition-label">蛋白质</text>
          <text class="nutrition-value">{{ recipe?.protein }}g</text>
        </view>
        <view class="nutrition-item">
          <text class="nutrition-label">脂肪</text>
          <text class="nutrition-value">{{ recipe?.fat }}g</text>
        </view>
        <view class="nutrition-item">
          <text class="nutrition-label">碳水</text>
          <text class="nutrition-value">{{ recipe?.carbohydrate }}g</text>
        </view>
      </view>

      <view class="meta-info">
        <text class="meta-item">{{ recipe?.cookingTime }}分钟</text>
        <text class="meta-item">{{ difficultyMap[recipe?.difficulty] }}</text>
        <view class="rating">
          <text class="star">★</text>
          <text class="score">{{ recipe?.rating }}</text>
          <text class="count">({{ recipe?.ratingCount }}人评价)</text>
        </view>
      </view>

      <view class="tags">
        <text v-for="tag in recipe?.tags" :key="tag" class="tag">{{ tag }}</text>
      </view>
    </view>

    <!-- 食材清单 -->
    <view class="section">
      <text class="section-title">食材清单</text>
      <view class="ingredients-list">
        <view v-for="item in recipe?.ingredients" :key="item.name" class="ingredient-item">
          <view class="checkbox" @tap="toggleIngredient(item)">
            <text v-if="item.checked" class="check-icon">✓</text>
          </view>
          <text class="ingredient-name">{{ item.name }}</text>
          <text class="ingredient-amount">{{ item.amount }}</text>
        </view>
      </view>
    </view>

    <!-- 制作步骤 -->
    <view class="section">
      <text class="section-title">制作步骤</text>
      <view class="steps-list">
        <view v-for="step in recipe?.steps" :key="step.step" class="step-item">
          <view class="step-number">{{ step.step }}</view>
          <view class="step-content">
            <text class="step-text">{{ step.description }}</text>
            <image v-if="step.image" :src="step.image" mode="aspectFill" class="step-image" />
          </view>
        </view>
      </view>
    </view>

    <!-- 评分区域 -->
    <view class="rating-section">
      <text class="section-title">评分</text>
      <view class="rating-stars">
        <text
          v-for="star in 5"
          :key="star"
          class="star-item"
          :class="{ active: userRating >= star }"
          @tap="rateRecipe(star)"
        >
          ★
        </text>
      </view>
    </view>

    <!-- 底部操作栏 -->
    <view class="bottom-bar">
      <view class="bar-item" @tap="startCooking">
        <text class="bar-icon">🍳</text>
        <text class="bar-text">开始烹饪</text>
      </view>
      <view class="bar-item" @tap="addToRecord">
        <text class="bar-icon">📝</text>
        <text class="bar-text">记录摄入</text>
      </view>
      <view class="bar-item" @tap="generateSimilar">
        <text class="bar-icon">🤖</text>
        <text class="bar-text">AI生成相似</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { recipeApi } from '@/api/recipe'
import type { Recipe } from '@/types'

const recipeId = ref('')
const recipe = ref<Recipe | null>(null)
const userRating = ref(0)

const difficultyMap = {
  easy: '简单',
  medium: '中等',
  hard: '困难'
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as any
  recipeId.value = currentPage.options?.id || ''
  loadRecipeDetail()
})

const loadRecipeDetail = async () => {
  if (!recipeId.value) return

  try {
    const res = await recipeApi.getRecipeDetail(recipeId.value)
    if (res.code === 200) {
      recipe.value = res.data
      // 添加勾选状态
      recipe.value.ingredients = res.data.ingredients.map((ing: any) => ({
        ...ing,
        checked: false
      }))
    }
  } catch (e) {
    console.error('加载食谱详情失败', e)
    uni.showToast({ title: '加载失败', icon: 'none' })
  }
}

const goBack = () => {
  uni.navigateBack()
}

const toggleCollect = async () => {
  if (!recipe.value) return

  try {
    if (recipe.value.isCollected) {
      await recipeApi.uncollectRecipe(recipe.value.id)
      recipe.value.isCollected = false
      uni.showToast({ title: '已取消收藏', icon: 'success' })
    } else {
      await recipeApi.collectRecipe(recipe.value.id)
      recipe.value.isCollected = true
      uni.showToast({ title: '收藏成功', icon: 'success' })
    }
  } catch (e) {
    console.error('操作失败', e)
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

const shareRecipe = () => {
  uni.showShareMenu({
    withShareTicket: true
  })
}

const toggleIngredient = (item: any) => {
  item.checked = !item.checked
}

const rateRecipe = async (score: number) => {
  if (!recipe.value) return

  try {
    await recipeApi.rateRecipe(recipe.value.id, score)
    userRating.value = score
    recipe.value.rating = (recipe.value.rating * recipe.value.ratingCount + score) / (recipe.value.ratingCount + 1)
    recipe.value.ratingCount++
    uni.showToast({ title: '评分成功', icon: 'success' })
  } catch (e) {
    console.error('评分失败', e)
    uni.showToast({ title: '评分失败', icon: 'none' })
  }
}

const startCooking = () => {
  uni.navigateTo({
    url: `/pages/recipe/cooking?id=${recipeId.value}`
  })
}

const addToRecord = () => {
  uni.navigateTo({
    url: `/pages/health/record?type=recipe&recipeId=${recipeId.value}`
  })
}

const generateSimilar = () => {
  uni.navigateTo({
    url: `/pages/recipe/ai-generate?similar=${recipeId.value}`
  })
}
</script>

<style lang="scss" scoped>
.recipe-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 150rpx;
}

.cover-section {
  position: relative;
  height: 500rpx;

  .cover-image {
    width: 100%;
    height: 100%;
  }

  .back-btn {
    position: absolute;
    top: calc(var(--status-bar-height) + 20rpx);
    left: 20rpx;
    width: 70rpx;
    height: 70rpx;
    background: rgba(0, 0, 0, 0.5);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;

    .back-icon {
      color: #ffffff;
      font-size: 36rpx;
      font-weight: bold;
    }
  }

  .actions {
    position: absolute;
    top: calc(var(--status-bar-height) + 20rpx);
    right: 20rpx;
    display: flex;
    gap: 16rpx;

    .action-icon {
      width: 70rpx;
      height: 70rpx;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ffffff;
      font-size: 36rpx;

      &.active {
        color: #ff4d4f;
      }
    }
  }
}

.info-section {
  background: #ffffff;
  padding: 30rpx;
  margin: 20rpx;
  border-radius: 16rpx;

  .recipe-name {
    display: block;
    font-size: 40rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 16rpx;
  }

  .recipe-desc {
    display: block;
    font-size: 28rpx;
    color: #666;
    line-height: 1.6;
    margin-bottom: 24rpx;
  }

  .nutrition-bar {
    display: flex;
    justify-content: space-around;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 12rpx;
    padding: 24rpx 0;
    margin-bottom: 24rpx;

    .nutrition-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .nutrition-label {
        font-size: 22rpx;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 8rpx;
      }

      .nutrition-value {
        font-size: 28rpx;
        font-weight: bold;
        color: #ffffff;
      }
    }
  }

  .meta-info {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;

    .meta-item {
      font-size: 24rpx;
      color: #999;
      margin-right: 24rpx;
    }

    .rating {
      margin-left: auto;
      display: flex;
      align-items: center;

      .star {
        color: #ffc107;
        font-size: 24rpx;
      }

      .score {
        margin-left: 4rpx;
        font-size: 26rpx;
        font-weight: bold;
        color: #333;
      }

      .count {
        font-size: 22rpx;
        color: #999;
      }
    }
  }

  .tags {
    display: flex;
    flex-wrap: wrap;

    .tag {
      font-size: 22rpx;
      color: #667eea;
      background: rgba(102, 126, 234, 0.1);
      padding: 6rpx 16rpx;
      border-radius: 4rpx;
      margin-right: 10rpx;
      margin-bottom: 10rpx;
    }
  }
}

.section {
  background: #ffffff;
  padding: 30rpx;
  margin: 20rpx;
  border-radius: 16rpx;

  .section-title {
    display: block;
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 24rpx;
  }

  .ingredients-list {
    .ingredient-item {
      display: flex;
      align-items: center;
      padding: 16rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .checkbox {
        width: 40rpx;
        height: 40rpx;
        border: 2rpx solid #ddd;
        border-radius: 8rpx;
        margin-right: 20rpx;
        display: flex;
        align-items: center;
        justify-content: center;

        .check-icon {
          color: #667eea;
          font-size: 28rpx;
        }
      }

      .ingredient-name {
        flex: 1;
        font-size: 28rpx;
        color: #333;
      }

      .ingredient-amount {
        font-size: 26rpx;
        color: #999;
      }
    }
  }

  .steps-list {
    .step-item {
      display: flex;
      margin-bottom: 30rpx;

      &:last-child {
        margin-bottom: 0;
      }

      .step-number {
        width: 60rpx;
        height: 60rpx;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #ffffff;
        font-size: 26rpx;
        font-weight: bold;
        flex-shrink: 0;
        margin-right: 20rpx;
      }

      .step-content {
        flex: 1;

        .step-text {
          display: block;
          font-size: 28rpx;
          color: #333;
          line-height: 1.6;
          margin-bottom: 16rpx;
        }

        .step-image {
          width: 100%;
          height: 300rpx;
          border-radius: 12rpx;
        }
      }
    }
  }
}

.rating-section {
  background: #ffffff;
  padding: 30rpx;
  margin: 20rpx;
  border-radius: 16rpx;

  .rating-stars {
    display: flex;
    justify-content: center;
    gap: 16rpx;

    .star-item {
      font-size: 60rpx;
      color: #ddd;

      &.active {
        color: #ffc107;
      }
    }
  }
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  background: #ffffff;
  border-top: 1rpx solid #f0f0f0;
  padding: 16rpx 0;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));

  .bar-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;

    .bar-icon {
      font-size: 40rpx;
      margin-bottom: 6rpx;
    }

    .bar-text {
      font-size: 22rpx;
      color: #666;
    }
  }
}
</style>
