const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

class HttpClient {
  private baseURL: string

  constructor(baseURL: string) {
    this.baseURL = baseURL
  }

  private getToken(): string {
    return uni.getStorageSync('token') || ''
  }

  request<T = any>(options: RequestOptions): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.request({
        url: this.baseURL + options.url,
        method: options.method || 'GET',
        data: options.data,
        header: {
          'Content-Type': 'application/json',
          'Authorization': this.getToken() ? `Bearer ${this.getToken()}` : '',
          ...options.header
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve(res.data as T)
          } else if (res.statusCode === 401) {
            uni.removeStorageSync('token')
            uni.reLaunch({
              url: '/pages/user/login'
            })
            reject(new Error('未授权，请重新登录'))
          } else {
            reject(new Error((res.data as any)?.message || '请求失败'))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '网络请求失败'))
        }
      })
    })
  }

  get<T = any>(url: string, data?: any): Promise<T> {
    return this.request<T>({ url, method: 'GET', data })
  }

  post<T = any>(url: string, data?: any): Promise<T> {
    return this.request<T>({ url, method: 'POST', data })
  }

  put<T = any>(url: string, data?: any): Promise<T> {
    return this.request<T>({ url, method: 'PUT', data })
  }

  delete<T = any>(url: string, data?: any): Promise<T> {
    return this.request<T>({ url, method: 'DELETE', data })
  }

  upload<T = any>(url: string, filePath: string, formData?: Record<string, string>): Promise<T> {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: this.baseURL + url,
        filePath,
        name: 'file',
        formData,
        header: {
          'Authorization': this.getToken() ? `Bearer ${this.getToken()}` : ''
        },
        success: (res) => {
          if (res.statusCode === 200) {
            const data = JSON.parse(res.data)
            resolve(data as T)
          } else {
            reject(new Error('上传失败'))
          }
        },
        fail: (err) => {
          reject(new Error(err.errMsg || '上传失败'))
        }
      })
    })
  }
}

export const http = new HttpClient(BASE_URL)
