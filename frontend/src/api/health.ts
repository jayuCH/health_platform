import { http } from '@/utils/http'
import type {
  ApiResponse,
  HealthRecord
} from '@/types'

export const healthApi = {
  // 记录健康数据
  saveRecord(data: Partial<HealthRecord>) {
    return http.post<ApiResponse<HealthRecord>>('/api/health/record', data)
  },

  // 获取健康记录列表
  getRecords(params: {
    page?: number
    size?: number
    startDate?: string
    endDate?: string
  }) {
    return http.get<ApiResponse<HealthRecord[]>>('/api/health/records', params)
  },

  // 获取今日健康数据
  getTodayRecord() {
    return http.get<ApiResponse<HealthRecord>>('/api/health/today')
  },

  // 获取健康统计
  getStats(days: number = 7) {
    return http.get<ApiResponse<any>>(`/api/health/stats?days=${days}`)
  },

  // 获取健康趋势数据
  getTrends(type: 'weight' | 'calories' | 'exercise' | 'sleep', days: number = 30) {
    return http.get<ApiResponse<any>>(`/api/health/trend/${type}?days=${days}`)
  }
}
