<template>
  <view class="ai-generate-page">
    <!-- 输入参数 -->
    <view class="form-section">
      <view class="form-item">
        <text class="label">现有食材（可选）</text>
        <textarea
          v-model="formData.ingredients"
          placeholder="如：鸡蛋、西红柿、洋葱，用逗号分隔"
          class="textarea"
        />
      </view>

      <view class="form-item">
        <text class="label">饮食偏好</text>
        <view class="tag-group">
          <view
            v-for="type in dietTypes"
            :key="type.value"
            class="tag-item"
            :class="{ active: formData.dietType === type.value }"
            @tap="formData.dietType = type.value"
          >
            <text class="tag-text">{{ type.label }}</text>
          </view>
        </view>
      </view>

      <view class="form-item">
        <text class="label">餐食类型</text>
        <view class="tag-group">
          <view
            v-for="type in mealTypes"
            :key="type.value"
            class="tag-item"
            :class="{ active: formData.mealType === type.value }"
            @tap="formData.mealType = type.value"
          >
            <text class="tag-text">{{ type.label }}</text>
          </view>
        </view>
      </view>

      <view class="form-item">
        <text class="label">目标热量（kcal）</text>
        <slider
          v-model="formData.calories"
          min="100"
          max="1000"
          step="50"
          activeColor="#667eea"
          show-value
          class="slider"
        />
      </view>

      <view class="form-item">
        <text class="label">特殊要求（可选）</text>
        <textarea
          v-model="formData.preferences"
          placeholder="如：少油少盐、不吃辣、需要快手菜等"
          class="textarea"
        />
      </view>
    </view>

    <!-- 生成按钮 -->
    <button class="generate-btn" @tap="generateRecipe" :disabled="generating">
      {{ generating ? '生成中...' : 'AI生成食谱' }}
    </button>

    <!-- 生成结果 -->
    <view v-if="result" class="result-section">
      <view class="result-header">
        <text class="result-title">生成结果</text>
        <text class="save-btn" @tap="saveRecipe">保存到食谱库</text>
      </view>

      <view class="recipe-card">
        <image v-if="result.coverImage" :src="result.coverImage" mode="aspectFill" class="cover-image" />
        <text class="recipe-name">{{ result.name }}</text>
        <text class="recipe-desc">{{ result.description }}</text>

        <view class="nutrition-bar">
          <view class="nutrition-item">
            <text class="label">{{ result.calories }}kcal</text>
          </view>
          <view class="nutrition-item">
            <text class="label">蛋白质{{ result.protein }}g</text>
          </view>
          <view class="nutrition-item">
            <text class="label">脂肪{{ result.fat }}g</text>
          </view>
          <view class="nutrition-item">
            <text class="label">碳水{{ result.carbohydrate }}g</text>
          </view>
        </view>

        <view class="section">
          <text class="section-title">食材清单</text>
          <view v-for="item in result.ingredients" :key="item.name" class="ingredient-item">
            <text class="name">{{ item.name }}</text>
            <text class="amount">{{ item.amount }}</text>
          </view>
        </view>

        <view class="section">
          <text class="section-title">制作步骤</text>
          <view v-for="(step, index) in result.steps" :key="index" class="step-item">
            <view class="step-number">{{ step.step }}</view>
            <text class="step-text">{{ step.description }}</text>
            <image v-if="step.image" :src="step.image" mode="aspectFill" class="step-image" />
          </view>
        </view>

        <view v-if="result.nutritionTips" class="tips-section">
          <text class="tips-title">营养小贴士</text>
          <text v-for="(tip, index) in result.nutritionTips" :key="index" class="tip-item">
            {{ index + 1 }}. {{ tip }}
          </text>
        </view>

        <view v-if="result.warning" class="warning-section">
          <text class="warning-text">⚠️ {{ result.warning }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { recipeApi } from '@/api/recipe'
import type { AiRecipeRequest, AiRecipeResponse } from '@/types'

const formData = ref<AiRecipeRequest>({
  ingredients: '',
  dietType: 'balanced',
  mealType: 'dinner',
  calories: 500,
  preferences: ''
})

const dietTypes = [
  { label: '均衡饮食', value: 'balanced' },
  { label: '低碳水', value: 'low-carb' },
  { label: '高蛋白', value: 'high-protein' },
  { label: '素食', value: 'vegetarian' }
]

const mealTypes = [
  { label: '早餐', value: 'breakfast' },
  { label: '午餐', value: 'lunch' },
  { label: '晚餐', value: 'dinner' },
  { label: '加餐', value: 'snack' }
]

const generating = ref(false)
const result = ref<AiRecipeResponse | null>(null)

const generateRecipe = async () => {
  generating.value = true
  result.value = null

  try {
    const res = await recipeApi.generateRecipe(formData.value)
    if (res.code === 200) {
      result.value = res.data
      uni.showToast({ title: '生成成功', icon: 'success' })
    }
  } catch (e: any) {
    uni.showToast({ title: e.message || '生成失败', icon: 'none' })
  } finally {
    generating.value = false
  }
}

const saveRecipe = () => {
  uni.showToast({ title: '已保存到食谱库', icon: 'success' })
}
</script>

<style lang="scss" scoped>
.ai-generate-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.form-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;

  .form-item {
    margin-bottom: 30rpx;

    &:last-child {
      margin-bottom: 0;
    }

    .label {
      display: block;
      font-size: 28rpx;
      font-weight: 500;
      color: #333;
      margin-bottom: 16rpx;
    }

    .textarea {
      width: 100%;
      min-height: 150rpx;
      background: #f5f5f5;
      border-radius: 12rpx;
      padding: 20rpx;
      font-size: 26rpx;
      color: #333;
    }

    .tag-group {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;

      .tag-item {
        padding: 12rpx 24rpx;
        background: #f5f5f5;
        border-radius: 30rpx;
        border: 2rpx solid transparent;

        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-color: #667eea;

          .tag-text {
            color: #ffffff;
          }
        }

        .tag-text {
          font-size: 26rpx;
          color: #666;
        }
      }
    }

    .slider {
      width: 100%;
    }
  }
}

.generate-btn {
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

.result-section {
  margin-top: 30rpx;

  .result-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;

    .result-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333;
    }

    .save-btn {
      color: #667eea;
      font-size: 26rpx;
    }
  }

  .recipe-card {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 30rpx;

    .cover-image {
      width: 100%;
      height: 350rpx;
      border-radius: 12rpx;
      margin-bottom: 20rpx;
    }

    .recipe-name {
      display: block;
      font-size: 36rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 12rpx;
    }

    .recipe-desc {
      display: block;
      font-size: 26rpx;
      color: #666;
      line-height: 1.6;
      margin-bottom: 20rpx;
    }

    .nutrition-bar {
      display: flex;
      justify-content: space-around;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 12rpx;
      padding: 20rpx 0;
      margin-bottom: 30rpx;

      .nutrition-item {
        .label {
          font-size: 24rpx;
          color: #ffffff;
        }
      }
    }

    .section {
      margin-bottom: 30rpx;

      .section-title {
        display: block;
        font-size: 30rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 16rpx;
      }

      .ingredient-item {
        display: flex;
        justify-content: space-between;
        padding: 12rpx 0;
        border-bottom: 1rpx solid #f0f0f0;

        .name {
          font-size: 26rpx;
          color: #333;
        }

        .amount {
          font-size: 26rpx;
          color: #999;
        }
      }

      .step-item {
        display: flex;
        margin-bottom: 20rpx;

        .step-number {
          width: 50rpx;
          height: 50rpx;
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #ffffff;
          font-size: 22rpx;
          font-weight: bold;
          margin-right: 16rpx;
          flex-shrink: 0;
        }

        .step-text {
          flex: 1;
          font-size: 26rpx;
          color: #333;
          line-height: 1.6;
        }

        .step-image {
          width: 100%;
          height: 200rpx;
          border-radius: 12rpx;
          margin-top: 12rpx;
        }
      }
    }

    .tips-section {
      background: rgba(102, 126, 234, 0.1);
      border-radius: 12rpx;
      padding: 20rpx;
      margin-bottom: 20rpx;

      .tips-title {
        display: block;
        font-size: 26rpx;
        font-weight: bold;
        color: #667eea;
        margin-bottom: 12rpx;
      }

      .tip-item {
        display: block;
        font-size: 24rpx;
        color: #666;
        line-height: 1.6;
        margin-bottom: 8rpx;
      }
    }

    .warning-section {
      background: rgba(255, 77, 79, 0.1);
      border-radius: 12rpx;
      padding: 20rpx;

      .warning-text {
        font-size: 24rpx;
        color: #ff4d4f;
      }
    }
  }
}
</style>
