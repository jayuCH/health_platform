import { http } from '@/utils/http'
import type {
  ApiResponse,
  PageResult,
  FoodAnalysis
} from '@/types'

export const analysisApi = {
  // 上传图片分析
  analyzeImage(filePath: string) {
    return http.upload<ApiResponse<FoodAnalysis>>('/api/analysis/upload', filePath)
  },

  // 获取历史分析记录
  getHistory(params: { page?: number; size?: number }) {
    return http.get<ApiResponse<PageResult<FoodAnalysis>>>('/api/analysis/history', params)
  },

  // 获取分析详情
  getDetail(id: string) {
    return http.get<ApiResponse<FoodAnalysis>>(`/api/analysis/${id}`)
  }
}
