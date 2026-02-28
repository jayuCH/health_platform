<template>
  <view class="recipe-list-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <input
        v-model="keyword"
        placeholder="搜索食谱..."
        class="search-input"
        @confirm="handleSearch"
      />
      <text class="search-btn" @tap="handleSearch">搜索</text>
    </view>

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

    <!-- 排序方式 -->
    <view class="sort-bar">
      <view
        v-for="sort in sortOptions"
        :key="sort.value"
        class="sort-item"
        :class="{ active: sortBy === sort.value }"
        @tap="sortBy = sort.value; loadRecipes()"
      >
        <text class="sort-text">{{ sort.label }}</text>
      </view>
    </view>

    <!-- 食谱列表 -->
    <view class="recipe-list">
      <view
        v-for="recipe in recipes"
        :key="recipe.id"
        class="recipe-card"
        @tap="goToDetail(recipe.id)"
      >
        <image :src="recipe.coverImage" mode="aspectFill" class="recipe-image" />
        <view class="recipe-content">
          <view class="recipe-header">
            <text class="recipe-name">{{ recipe.name }}</text>
            <view v-if="recipe.isCollected" class="collected-icon">heart</view>
          </view>
          <text class="recipe-desc">{{ recipe.description }}</text>
          <view class="recipe-info">
            <text class="info-item">{{ recipe.calories }}kcal</text>
            <text class="info-item">蛋白质{{ recipe.protein }}g</text>
            <text class="info-item">{{ recipe.cookingTime }}分钟</text>
            <view class="rating">
              <text class="star">★</text>
              <text class="score">{{ recipe.rating }}</text>
            </view>
          </view>
          <view class="recipe-tags">
            <text v-for="tag in recipe.tags" :key="tag" class="tag">{{ tag }}</text>
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
import { recipeApi } from '@/api/recipe'
import type { Recipe, RecipeCategory } from '@/types'

const categories = ref<RecipeCategory[]>([
  { id: '', name: '全部', icon: '', sort: 0 },
  { id: '1', name: '早餐', icon: '', sort: 1 },
  { id: '2', name: '午餐', icon: '', sort: 2 },
  { id: '3', name: '晚餐', icon: '', sort: 3 },
  { id: '4', name: '健身餐', icon: '', sort: 4 },
  { id: '5', name: '减脂餐', icon: '', sort: 5 },
  { id: '6', name: '素食', icon: '', sort: 6 },
  { id: '7', name: '甜品', icon: '', sort: 7 }
])

const sortOptions = [
  { label: '最新', value: 'latest' },
  { label: '最热', value: 'hot' },
  { label: '评分', value: 'rating' }
]

const keyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('latest')
const recipes = ref<Recipe[]>([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const loading = ref(false)
const hasMore = ref(true)

onMounted(() => {
  loadRecipes()
})

const selectCategory = (id: string) => {
  selectedCategory.value = id
  page.value = 1
  recipes.value = []
  loadRecipes()
}

const handleSearch = () => {
  page.value = 1
  recipes.value = []
  loadRecipes()
}

const loadRecipes = async () => {
  if (loading.value) return

  loading.value = true
  try {
    const res = await recipeApi.getRecipes({
      page: page.value,
      size: size.value,
      categoryId: selectedCategory.value || undefined,
      keyword: keyword.value || undefined,
      sortBy: sortBy.value as any
    })

    if (res.code === 200) {
      if (page.value === 1) {
        recipes.value = res.data.records
      } else {
        recipes.value = [...recipes.value, ...res.data.records]
      }
      total.value = res.data.total
      hasMore.value = recipes.value.length < total.value
    }
  } catch (e) {
    console.error('加载食谱失败', e)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id: string) => {
  uni.navigateTo({ url: `/pages/recipe/detail?id=${id}` })
}

// 下拉刷新
onPullDownRefresh(() => {
  page.value = 1
  recipes.value = []
  loadRecipes().finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 触底加载更多
onReachBottom(() => {
  if (hasMore.value && !loading.value) {
    page.value++
    loadRecipes()
  }
})
</script>

<style lang="scss" scoped>
.recipe-list-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #ffffff;

  .search-input {
    flex: 1;
    height: 70rpx;
    background: #f5f5f5;
    border-radius: 35rpx;
    padding: 0 30rpx;
    font-size: 28rpx;
  }

  .search-btn {
    margin-left: 20rpx;
    color: #667eea;
    font-size: 28rpx;
    padding: 10rpx 20rpx;
  }
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

.sort-bar {
  display: flex;
  background: #ffffff;
  padding: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;

  .sort-item {
    flex: 1;
    text-align: center;
    padding: 10rpx 0;
    border-bottom: 4rpx solid transparent;

    &.active {
      border-bottom-color: #667eea;

      .sort-text {
        color: #667eea;
        font-weight: bold;
      }
    }

    .sort-text {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.recipe-list {
  padding: 20rpx;

  .recipe-card {
    background: #ffffff;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
    overflow: hidden;

    .recipe-image {
      width: 100%;
      height: 320rpx;
    }

    .recipe-content {
      padding: 24rpx;

      .recipe-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12rpx;

        .recipe-name {
          flex: 1;
          font-size: 32rpx;
          font-weight: bold;
          color: #333;
          padding-right: 20rpx;
        }

        .collected-icon {
          color: #ff4d4f;
          font-size: 36rpx;
        }
      }

      .recipe-desc {
        display: block;
        font-size: 26rpx;
        color: #666;
        margin-bottom: 16rpx;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .recipe-info {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
        margin-bottom: 16rpx;

        .info-item {
          font-size: 24rpx;
          color: #999;
          margin-right: 20rpx;
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
            font-size: 24rpx;
            color: #999;
          }
        }
      }

      .recipe-tags {
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
