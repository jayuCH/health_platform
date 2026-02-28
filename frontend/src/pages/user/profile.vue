<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <image v-if="userInfo?.avatar" :src="userInfo.avatar" class="avatar" @tap="changeAvatar" />
      <view v-else class="avatar-placeholder" @tap="changeAvatar">
        <text class="icon">user</text>
      </view>
      <view class="user-info">
        <text class="nickname">{{ userInfo?.nickname || '未设置昵称' }}</text>
        <text class="phone">{{ userInfo?.phone?.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') }}</text>
      </view>
      <text class="edit-btn" @tap="editProfile">编辑</text>
    </view>

    <!-- 健康目标 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">健康目标</text>
        <text class="section-edit" @tap="editHealthGoal">设置</text>
      </view>
      <view class="goal-grid">
        <view class="goal-item">
          <text class="goal-value">{{ healthGoal?.targetWeight || '--' }}kg</text>
          <text class="goal-label">目标体重</text>
        </view>
        <view class="goal-item">
          <text class="goal-value">{{ healthGoal?.currentWeight || '--' }}kg</text>
          <text class="goal-label">当前体重</text>
        </view>
        <view class="goal-item">
          <text class="goal-value">{{ healthGoal?.targetCalories || '--' }}kcal</text>
          <text class="goal-label">目标热量</text>
        </view>
      </view>
      <view class="goal-tags">
        <text class="tag">{{ activityLevelMap[healthGoal?.activityLevel] || '--' }}</text>
        <text class="tag">{{ dietTypeMap[healthGoal?.dietType] || '--' }}</text>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-item" @tap="goToMyCollects">
        <view class="menu-left">
          <text class="icon">heart</text>
          <text class="menu-text">我的收藏</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="goToAnalysisHistory">
        <view class="menu-left">
          <text class="icon">camera</text>
          <text class="menu-text">分析记录</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="goToHealthChart">
        <view class="menu-left">
          <text class="icon">chart</text>
          <text class="menu-text">健康趋势</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="goToSettings">
        <view class="menu-left">
          <text class="icon">settings</text>
          <text class="menu-text">设置</text>
        </view>
        <text class="arrow">></text>
      </view>
    </view>

    <!-- 退出登录 -->
    <button class="logout-btn" @tap="logout">退出登录</button>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'
import { recipeApi } from '@/api/recipe'
import { analysisApi } from '@/api/analysis'

const userStore = useUserStore()
const userInfo = ref(userStore.userInfo)
const healthGoal = ref<any>(null)

const activityLevelMap = {
  low: '久坐少动',
  medium: '适度运动',
  high: '高强度运动'
}

const dietTypeMap = {
  balanced: '均衡饮食',
  'low-carb': '低碳水',
  'high-protein': '高蛋白',
  vegetarian: '素食'
}

onMounted(async () => {
  loadUserInfo()
  loadHealthGoal()
})

const loadUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo()
    if (res.code === 200) {
      userInfo.value = res.data
      userStore.setUserInfo(res.data)
    }
  } catch (e) {
    console.error('加载用户信息失败', e)
  }
}

const loadHealthGoal = async () => {
  try {
    const res = await userApi.getHealthGoal()
    if (res.code === 200) {
      healthGoal.value = res.data
    }
  } catch (e) {
    console.error('加载健康目标失败', e)
  }
}

const changeAvatar = () => {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera'],
    success: async (res) => {
      const filePath = res.tempFilePaths[0]
      try {
        uni.showLoading({ title: '上传中...' })
        const uploadRes = await userApi.updateAvatar(filePath)
        if (uploadRes.code === 200) {
          userInfo.value!.avatar = uploadRes.data.avatar
          userStore.setUserInfo(userInfo.value!)
          uni.showToast({ title: '更新成功', icon: 'success' })
        }
      } catch (e: any) {
        uni.showToast({ title: e.message || '上传失败', icon: 'none' })
      } finally {
        uni.hideLoading()
      }
    }
  })
}

const editProfile = () => {
  uni.navigateTo({ url: '/pages/user/edit' })
}

const editHealthGoal = () => {
  uni.navigateTo({ url: '/pages/user/goal' })
}

const goToMyCollects = () => {
  uni.navigateTo({ url: '/pages/recipe/collect' })
}

const goToAnalysisHistory = () => {
  uni.navigateTo({ url: '/pages/analysis/history' })
}

const goToHealthChart = () => {
  uni.navigateTo({ url: '/pages/health/chart' })
}

const goToSettings = () => {
  uni.navigateTo({ url: '/pages/user/settings' })
}

const logout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.clearAuth()
        uni.reLaunch({ url: '/pages/user/login' })
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 30rpx;
  display: flex;
  align-items: center;

  .avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    border: 4rpx solid rgba(255, 255, 255, 0.3);
  }

  .avatar-placeholder {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    border: 4rpx solid rgba(255, 255, 255, 0.3);
  }

  .user-info {
    flex: 1;
    margin-left: 24rpx;

    .nickname {
      display: block;
      color: #ffffff;
      font-size: 36rpx;
      font-weight: bold;
      margin-bottom: 8rpx;
    }

    .phone {
      color: rgba(255, 255, 255, 0.8);
      font-size: 26rpx;
    }
  }

  .edit-btn {
    color: #ffffff;
    font-size: 26rpx;
    padding: 12rpx 24rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.5);
    border-radius: 30rpx;
  }
}

.section {
  background: #ffffff;
  margin: 20rpx;
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

    .section-edit {
      font-size: 26rpx;
      color: #667eea;
    }
  }

  .goal-grid {
    display: flex;
    justify-content: space-around;

    .goal-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .goal-value {
        font-size: 36rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 8rpx;
      }

      .goal-label {
        font-size: 24rpx;
        color: #999;
      }
    }
  }

  .goal-tags {
    display: flex;
    justify-content: center;
    margin-top: 20rpx;

    .tag {
      font-size: 24rpx;
      color: #667eea;
      background: rgba(102, 126, 234, 0.1);
      padding: 8rpx 20rpx;
      border-radius: 20rpx;
      margin: 0 10rpx;
    }
  }
}

.menu-section {
  background: #ffffff;
  margin: 20rpx;
  border-radius: 16rpx;
  overflow: hidden;

  .menu-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .menu-left {
      display: flex;
      align-items: center;

      .icon {
        font-size: 36rpx;
        margin-right: 20rpx;
      }

      .menu-text {
        font-size: 28rpx;
        color: #333;
      }
    }

    .arrow {
      color: #ccc;
      font-size: 28rpx;
    }
  }
}

.logout-btn {
  margin: 40rpx 20rpx;
  background: #ffffff;
  color: #ff4d4f;
  font-size: 30rpx;
  padding: 25rpx 0;
  border: none;
  border-radius: 50rpx;
}
</style>
