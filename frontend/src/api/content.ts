import { http } from '@/utils/http'
import type {
  ApiResponse,
  PageResult,
  Article,
  Banner
} from '@/types'

export const contentApi = {
  // 获取轮播图
  getBanners() {
    return http.get<ApiResponse<Banner[]>>('/api/content/banners')
  },

  // 获取资讯列表
  getArticles(params: {
    page?: number
    size?: number
    categoryId?: string
    keyword?: string
  }) {
    return http.get<ApiResponse<PageResult<Article>>>('/api/content/articles', params)
  },

  // 获取资讯详情
  getArticleDetail(id: string) {
    return http.get<ApiResponse<Article>>(`/api/content/article/${id}`)
  },

  // 点赞资讯
  likeArticle(id: string) {
    return http.post<ApiResponse<void>>(`/api/content/article/${id}/like`)
  },

  // 取消点赞
  unlikeArticle(id: string) {
    return http.delete<ApiResponse<void>>(`/api/content/article/${id}/like`)
  },

  // 获取分类列表
  getCategories() {
    return http.get<ApiResponse<any[]>>('/api/content/categories')
  }
}
