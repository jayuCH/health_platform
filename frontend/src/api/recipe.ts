import { http } from '@/utils/http'
import type {
  ApiResponse,
  PageResult,
  Recipe,
  RecipeCategory,
  AiRecipeRequest,
  AiRecipeResponse
} from '@/types'

export const recipeApi = {
  // 获取食谱分类
  getCategories() {
    return http.get<ApiResponse<RecipeCategory[]>>('/api/recipe/categories')
  },

  // 获取食谱列表
  getRecipes(params: {
    page?: number
    size?: number
    categoryId?: string
    keyword?: string
    sortBy?: 'latest' | 'hot' | 'rating'
  }) {
    return http.get<ApiResponse<PageResult<Recipe>>>('/api/recipe/list', params)
  },

  // 获取食谱详情
  getRecipeDetail(id: string) {
    return http.get<ApiResponse<Recipe>>(`/api/recipe/${id}`)
  },

  // 收藏食谱
  collectRecipe(id: string) {
    return http.post<ApiResponse<void>>(`/api/recipe/${id}/collect`)
  },

  // 取消收藏
  uncollectRecipe(id: string) {
    return http.delete<ApiResponse<void>>(`/api/recipe/${id}/collect`)
  },

  // 获取我的收藏
  getMyCollects(params: { page?: number; size?: number }) {
    return http.get<ApiResponse<PageResult<Recipe>>>('/api/recipe/my-collects', params)
  },

  // 评分
  rateRecipe(id: string, score: number) {
    return http.post<ApiResponse<void>>(`/api/recipe/${id}/rate`, { score })
  },

  // AI生成食谱
  generateRecipe(data: AiRecipeRequest) {
    return http.post<ApiResponse<AiRecipeResponse>>('/api/recipe/ai-generate', data)
  },

  // 获取推荐食谱
  getRecommendedRecipes() {
    return http.get<ApiResponse<Recipe[]>>('/api/recipe/recommended')
  }
}
