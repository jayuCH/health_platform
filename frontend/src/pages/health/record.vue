<template>
  <view class="health-record-page">
    <!-- 今日数据卡片 -->
    <view class="today-card">
      <text class="card-title">今日数据</text>
      <view class="data-grid">
        <view class="data-item">
          <text class="data-value">{{ todayData.calorieIntake || 0 }}</text>
          <text class="data-label">摄入热量</text>
          <text class="data-unit">kcal</text>
        </view>
        <view class="data-item">
          <text class="data-value">{{ todayData.exerciseDuration || 0 }}</text>
          <text class="data-label">运动时长</text>
          <text class="data-unit">分钟</text>
        </view>
        <view class="data-item">
          <text class="data-value">{{ todayData.steps || 0 }}</text>
          <text class="data-label">步数</text>
          <text class="data-unit">步</text>
        </view>
        <view class="data-item">
          <text class="data-value">{{ todayData.sleepHours || 0 }}</text>
          <text class="data-label">睡眠</text>
          <text class="data-unit">小时</text>
        </view>
      </view>
      <view class="progress-bar">
        <view class="progress-track">
          <view
            class="progress-fill"
            :style="{ width: `${Math.min((todayData.calorieIntake || 0) / targetCalories * 100, 100)}%` }"
          ></view>
        </view>
        <text class="progress-text">目标：{{ targetCalories }}kcal</text>
      </view>
    </view>

    <!-- 快捷记录 -->
    <view class="quick-record">
      <view class="record-item" @tap="recordWeight">
        <text class="record-icon">⚖️</text>
        <text class="record-label">体重</text>
      </view>
      <view class="record-item" @tap="recordExercise">
        <text class="record-icon">🏃</text>
        <text class="record-label">运动</text>
      </view>
      <view class="record-item" @tap="recordFood">
        <text class="record-icon">🍽️</text>
        <text class="record-label">饮食</text>
      </view>
      <view class="record-item" @tap="recordSleep">
        <text class="record-icon">😴</text>
        <text class="record-label">睡眠</text>
      </view>
    </view>

    <!-- 历史记录 -->
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">历史记录</text>
        <view class="date-selector">
          <text class="date-btn" @tap="changeDate(-7)">近7天</text>
          <text class="date-btn active">近30天</text>
          <text class="date-btn" @tap="changeDate(90)">近3月</text>
        </view>
      </view>

      <view class="record-list">
        <view v-for="record in recordList" :key="record.id" class="record-card">
          <view class="record-date">
            <text class="day">{{ formatDate(record.recordDate).day }}</text>
            <text class="month">{{ formatDate(record.recordDate).month }}</text>
          </view>
          <view class="record-content">
            <view class="record-row">
              <text class="record-key">体重</text>
              <text class="record-value">{{ record.weight }}kg</text>
            </view>
            <view class="record-row">
              <text class="record-key">热量</text>
              <text class="record-value">{{ record.calorieIntake }}kcal</text>
            </view>
            <view class="record-row">
              <text class="record-key">运动</text>
              <text class="record-value">{{ record.exerciseDuration }}分钟</text>
            </view>
            <view class="record-row">
              <text class="record-key">睡眠</text>
              <text class="record-value">{{ record.sleepHours }}小时</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 查看趋势按钮 -->
    <view class="chart-section">
      <button class="chart-btn" @tap="goToChart">查看健康趋势</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { healthApi } from '@/api/health'
import type { HealthRecord } from '@/types'

const todayData = ref<any>({})
const recordList = ref<HealthRecord[]>([])
const targetCalories = ref(2000)

onMounted(() => {
  loadTodayData()
  loadRecords()
  loadTarget()
})

const loadTodayData = async () => {
  try {
    const res = await healthApi.getTodayRecord()
    if (res.code === 200) {
      todayData.value = res.data || {}
    }
  } catch (e) {
    console.error('加载今日数据失败', e)
  }
}

const loadRecords = async () => {
  try {
    const res = await healthApi.getRecords({ page: 1, size: 10 })
    if (res.code === 200) {
      recordList.value = res.data
    }
  } catch (e) {
    console.error('加载记录失败', e)
  }
}

const loadTarget = () => {
  // 从用户存储获取目标热量
  const userInfo = uni.getStorageSync('userInfo')
  if (userInfo?.healthGoal?.targetCalories) {
    targetCalories.value = userInfo.healthGoal.targetCalories
  }
}

const formatDate = (date: string) => {
  const d = new Date(date)
  return {
    day: d.getDate(),
    month: `${d.getMonth() + 1}月`
  }
}

const recordWeight = () => {
  uni.showModal({
    title: '记录体重',
    editable: true,
    placeholderText: '请输入体重(kg)',
    success: async (res) => {
      if (res.confirm && res.content) {
        await saveRecord({ weight: parseFloat(res.content) })
      }
    }
  })
}

const recordExercise = () => {
  uni.navigateTo({ url: '/pages/health/exercise' })
}

const recordFood = () => {
  uni.navigateTo({ url: '/pages/recipe/list' })
}

const recordSleep = () => {
  uni.showModal({
    title: '记录睡眠',
    editable: true,
    placeholderText: '请输入睡眠时长(小时)',
    success: async (res) => {
      if (res.confirm && res.content) {
        await saveRecord({ sleepHours: parseFloat(res.content) })
      }
    }
  })
}

const saveRecord = async (data: any) => {
  try {
    const res = await healthApi.saveRecord(data)
    if (res.code === 200) {
      uni.showToast({ title: '记录成功', icon: 'success' })
      loadTodayData()
      loadRecords()
    }
  } catch (e) {
    uni.showToast({ title: '记录失败', icon: 'none' })
  }
}

const changeDate = (days: number) => {
  // 加载指定天数的记录
  loadRecords()
}

const goToChart = () => {
  uni.switchTab({ url: '/pages/health/chart' })
}
</script>

<style lang="scss" scoped>
.health-record-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.today-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;

  .card-title {
    display: block;
    color: #ffffff;
    font-size: 30rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
  }

  .data-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20rpx;
    margin-bottom: 24rpx;

    .data-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .data-value {
        font-size: 32rpx;
        font-weight: bold;
        color: #ffffff;
        margin-bottom: 6rpx;
      }

      .data-label {
        font-size: 20rpx;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 2rpx;
      }

      .data-unit {
        font-size: 18rpx;
        color: rgba(255, 255, 255, 0.6);
      }
    }
  }

  .progress-bar {
    .progress-track {
      height: 16rpx;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 8rpx;
      overflow: hidden;
      margin-bottom: 12rpx;

      .progress-fill {
        height: 100%;
        background: #ffffff;
        transition: width 0.3s;
      }
    }

    .progress-text {
      color: rgba(255, 255, 255, 0.8);
      font-size: 22rpx;
    }
  }
}

.quick-record {
  display: flex;
  justify-content: space-around;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx 0;
  margin-bottom: 20rpx;

  .record-item {
    display: flex;
    flex-direction: column;
    align-items: center;

    .record-icon {
      font-size: 48rpx;
      margin-bottom: 10rpx;
    }

    .record-label {
      font-size: 24rpx;
      color: #333;
    }
  }
}

.history-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24rpx;

    .section-title {
      font-size: 30rpx;
      font-weight: bold;
      color: #333;
    }

    .date-selector {
      display: flex;
      gap: 10rpx;

      .date-btn {
        padding: 8rpx 20rpx;
        font-size: 24rpx;
        color: #666;
        background: #f5f5f5;
        border-radius: 20rpx;

        &.active {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: #ffffff;
        }
      }
    }
  }

  .record-list {
    .record-card {
      display: flex;
      padding: 20rpx 0;
      border-bottom: 1rpx solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .record-date {
        width: 80rpx;
        height: 80rpx;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 12rpx;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        margin-right: 20rpx;
        flex-shrink: 0;

        .day {
          font-size: 32rpx;
          font-weight: bold;
          color: #ffffff;
        }

        .month {
          font-size: 20rpx;
          color: rgba(255, 255, 255, 0.8);
        }
      }

      .record-content {
        flex: 1;

        .record-row {
          display: flex;
          justify-content: space-between;
          margin-bottom: 10rpx;

          &:last-child {
            margin-bottom: 0;
          }

          .record-key {
            font-size: 26rpx;
            color: #666;
          }

          .record-value {
            font-size: 26rpx;
            color: #333;
            font-weight: 500;
          }
        }
      }
    }
  }
}

.chart-section {
  .chart-btn {
    width: 100%;
    height: 80rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #ffffff;
    font-size: 30rpx;
    font-weight: bold;
    border: none;
    border-radius: 50rpx;
  }
}
</style>
