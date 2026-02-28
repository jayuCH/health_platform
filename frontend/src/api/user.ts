import { http } from '@/utils/http'
import type {
  ApiResponse,
  LoginRequest,
  UserInfo,
  HealthGoal
} from '@/types'

export const userApi = {
  // 发送验证码
  sendCode(phone: string) {
    return http.post<ApiResponse<string>>('/api/user/sms/send', { phone })
  },

  // 登录
  login(data: LoginRequest) {
    return http.post<ApiResponse<{ token: string; userInfo: UserInfo }>>('/api/user/login', data)
  },

  // 微信登录
  wxLogin(code: string) {
    return http.post<ApiResponse<{ token: string; userInfo: UserInfo }>>('/api/user/wx/login', { code })
  },

  // 获取用户信息
  getUserInfo() {
    return http.get<ApiResponse<UserInfo>>('/api/user/info')
  },

  // 更新用户信息
  updateUserInfo(data: Partial<UserInfo>) {
    return http.put<ApiResponse<UserInfo>>('/api/user/info', data)
  },

  // 更新头像
  updateAvatar(filePath: string) {
    return http.upload<ApiResponse<{ avatar: string }>>('/api/user/avatar', filePath)
  },

  // 获取健康目标
  getHealthGoal() {
    return http.get<ApiResponse<HealthGoal>>('/api/user/health-goal')
  },

  // 设置健康目标
  setHealthGoal(data: Partial<HealthGoal>) {
    return http.post<ApiResponse<HealthGoal>>('/api/user/health-goal', data)
  }
}
