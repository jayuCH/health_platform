import { defineStore } from 'pinia'

export interface UserInfo {
  id: string
  nickname: string
  avatar: string
  phone: string
  healthGoal: HealthGoal
}

export interface HealthGoal {
  targetWeight: number
  currentWeight: number
  targetCalories: number
  activityLevel: 'low' | 'medium' | 'high'
  dietType: 'balanced' | 'low-carb' | 'high-protein' | 'vegetarian'
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: uni.getStorageSync('token') || '',
    userInfo: null as UserInfo | null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    hasUserInfo: (state) => !!state.userInfo
  },

  actions: {
    setToken(token: string) {
      this.token = token
      uni.setStorageSync('token', token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
      uni.setStorageSync('userInfo', userInfo)
    },

    clearAuth() {
      this.token = ''
      this.userInfo = null
      uni.removeStorageSync('token')
      uni.removeStorageSync('userInfo')
    }
  }
})
