<template>
  <view class="health-chart-page">
    <!-- 时间选择器 -->
    <view class="time-selector">
      <view
        v-for="tab in timeTabs"
        :key="tab.value"
        class="tab-item"
        :class="{ active: selectedTime === tab.value }"
        @tap="selectTime(tab.value)"
      >
        <text class="tab-text">{{ tab.label }}</text>
      </view>
    </view>

    <!-- 图表容器 -->
    <view class="chart-container">
      <view class="chart-card">
        <text class="chart-title">体重趋势</text>
        <view class="chart-wrapper">
          <qiun-ucharts
            type="line"
            :opts="weightChartOpts"
            :chartData="weightChartData"
            canvasId="weightChart"
          />
        </view>
      </view>

      <view class="chart-card">
        <text class="chart-title">热量摄入趋势</text>
        <view class="chart-wrapper">
          <qiun-ucharts
            type="column"
            :opts="caloriesChartOpts"
            :chartData="caloriesChartData"
            canvasId="caloriesChart"
          />
        </view>
      </view>

      <view class="chart-card">
        <text class="chart-title">运动时长趋势</text>
        <view class="chart-wrapper">
          <qiun-ucharts
            type="area"
            :opts="exerciseChartOpts"
            :chartData="exerciseChartData"
            canvasId="exerciseChart"
          />
        </view>
      </view>

      <view class="chart-card">
        <text class="chart-title">睡眠质量分布</text>
        <view class="chart-wrapper">
          <qiun-ucharts
            type="pie"
            :opts="sleepChartOpts"
            :chartData="sleepChartData"
            canvasId="sleepChart"
          />
        </view>
      </view>
    </view>

    <!-- 数据统计 -->
    <view class="stats-section">
      <text class="section-title">数据统计</text>
      <view class="stats-grid">
        <view class="stat-item">
          <text class="stat-label">平均体重</text>
          <text class="stat-value">{{ stats.avgWeight }}kg</text>
        </view>
        <view class="stat-item">
          <text class="stat-label">日均热量</text>
          <text class="stat-value">{{ stats.avgCalories }}kcal</text>
        </view>
        <view class="stat-item">
          <text class="stat-label">累计运动</text>
          <text class="stat-value">{{ stats.totalExercise }}分钟</text>
        </view>
        <view class="stat-item">
          <text class="stat-label">平均睡眠</text>
          <text class="stat-value">{{ stats.avgSleep }}小时</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { healthApi } from '@/api/health'

const timeTabs = [
  { label: '近7天', value: 7 },
  { label: '近30天', value: 30 },
  { label: '近3月', value: 90 }
]

const selectedTime = ref(30)
const stats = ref({
  avgWeight: '--',
  avgCalories: '--',
  totalExercise: '--',
  avgSleep: '--'
})

// 体重图表数据
const weightChartData = ref({
  categories: [],
  series: [{
    name: '体重',
    data: []
  }]
})

const weightChartOpts = computed(() => ({
  color: ['#667eea'],
  padding: [15, 15, 0, 5],
  enableScroll: false,
  legend: {},
  xAxis: {
    disableGrid: true
  },
  yAxis: {
    gridType: 'dash',
    dashLength: 2,
    data: [{ min: 0 }]
  },
  extra: {
    line: {
      type: 'curve',
      width: 2,
      activeType: 'hollow'
    }
  }
}))

// 热量图表数据
const caloriesChartData = ref({
  categories: [],
  series: [{
    name: '热量摄入',
    data: []
  }]
})

const caloriesChartOpts = computed(() => ({
  color: ['#764ba2'],
  padding: [15, 15, 0, 5],
  enableScroll: false,
  legend: {},
  xAxis: {
    disableGrid: true
  },
  yAxis: {
    gridType: 'dash',
    dashLength: 2,
    data: [{ min: 0 }]
  },
  extra: {
    column: {
      type: 'group',
      width: 20,
      activeBgColor: '#000000',
      activeBgOpacity: 0.08
    }
  }
}))

// 运动图表数据
const exerciseChartData = ref({
  categories: [],
  series: [{
    name: '运动时长',
    data: []
  }]
})

const exerciseChartOpts = computed(() => ({
  color: ['#11998e'],
  padding: [15, 15, 0, 5],
  enableScroll: false,
  legend: {},
  xAxis: {
    disableGrid: true
  },
  yAxis: {
    gridType: 'dash',
    dashLength: 2,
    data: [{ min: 0 }]
  },
  extra: {
    area: {
      type: 'curve',
      opacity: 0.2,
      addLine: true,
      width: 2,
      gradient: true,
      activeType: 'hollow'
    }
  }
}))

// 睡眠图表数据
const sleepChartData = ref({
  series: [
    { name: '优秀', data: 35 },
    { name: '良好', data: 40 },
    { name: '一般', data: 20 },
    { name: '较差', data: 5 }
  ]
})

const sleepChartOpts = computed(() => ({
  color: ['#667eea', '#764ba2', '#11998e', '#f5576c'],
  padding: [5, 5, 5, 5],
  enableScroll: false,
  legend: {
    show: true,
    position: 'right',
    lineHeight: 25
  },
  extra: {
    pie: {
      activeOpacity: 0.5,
      activeRadius: 10,
      offsetAngle: 0,
      labelWidth: 15,
      ringWidth: 0,
      border: true,
      borderWidth: 3,
      borderColor: '#FFFFFF'
    }
  }
}))

onMounted(() => {
  loadData()
})

const selectTime = (days: number) => {
  selectedTime.value = days
  loadData()
}

const loadData = async () => {
  await Promise.all([
    loadWeightTrend(),
    loadCaloriesTrend(),
    loadExerciseTrend(),
    loadStats()
  ])
}

const loadWeightTrend = async () => {
  try {
    const res = await healthApi.getTrends('weight', selectedTime.value)
    if (res.code === 200) {
      const data = res.data
      weightChartData.value = {
        categories: data.dates || [],
        series: [{
          name: '体重',
          data: data.values || []
        }]
      }
    }
  } catch (e) {
    console.error('加载体重趋势失败', e)
  }
}

const loadCaloriesTrend = async () => {
  try {
    const res = await healthApi.getTrends('calories', selectedTime.value)
    if (res.code === 200) {
      const data = res.data
      caloriesChartData.value = {
        categories: data.dates || [],
        series: [{
          name: '热量摄入',
          data: data.values || []
        }]
      }
    }
  } catch (e) {
    console.error('加载热量趋势失败', e)
  }
}

const loadExerciseTrend = async () => {
  try {
    const res = await healthApi.getTrends('exercise', selectedTime.value)
    if (res.code === 200) {
      const data = res.data
      exerciseChartData.value = {
        categories: data.dates || [],
        series: [{
          name: '运动时长',
          data: data.values || []
        }]
      }
    }
  } catch (e) {
    console.error('加载运动趋势失败', e)
  }
}

const loadStats = async () => {
  try {
    const res = await healthApi.getStats(selectedTime.value)
    if (res.code === 200) {
      stats.value = {
        avgWeight: res.data.avgWeight?.toFixed(1) || '--',
        avgCalories: Math.round(res.data.avgCalories) || '--',
        totalExercise: res.data.totalExercise || '--',
        avgSleep: res.data.avgSleep?.toFixed(1) || '--'
      }
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}
</script>

<style lang="scss" scoped>
.health-chart-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.time-selector {
  display: flex;
  background: #ffffff;
  border-radius: 16rpx;
  padding: 10rpx;
  margin-bottom: 20rpx;

  .tab-item {
    flex: 1;
    text-align: center;
    padding: 16rpx 0;
    border-radius: 12rpx;

    &.active {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

      .tab-text {
        color: #ffffff;
      }
    }

    .tab-text {
      font-size: 26rpx;
      color: #666;
    }
  }
}

.chart-container {
  .chart-card {
    background: #ffffff;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 20rpx;

    .chart-title {
      display: block;
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 16rpx;
    }

    .chart-wrapper {
      height: 400rpx;
    }
  }
}

.stats-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;

  .section-title {
    display: block;
    font-size: 30rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 24rpx;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20rpx;

    .stat-item {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 16rpx;
      padding: 30rpx;
      text-align: center;

      .stat-label {
        display: block;
        font-size: 24rpx;
        color: rgba(255, 255, 255, 0.8);
        margin-bottom: 12rpx;
      }

      .stat-value {
        font-size: 36rpx;
        font-weight: bold;
        color: #ffffff;
      }
    }
  }
}
</style>
