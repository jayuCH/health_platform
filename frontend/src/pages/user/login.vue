<template>
  <view class="login-page">
    <view class="logo-section">
      <image src="/static/logo.png" class="logo" mode="aspectFit" />
      <text class="app-name">健康饮食</text>
      <text class="app-slogan">AI驱动的健康管理专家</text>
    </view>

    <view class="form-section">
      <!-- 手机号登录 -->
      <view class="phone-login">
        <view class="input-group">
          <text class="label">手机号</text>
          <input
            v-model="phone"
            type="number"
            placeholder="请输入手机号"
            maxlength="11"
            class="input"
          />
        </view>
        <view class="input-group">
          <text class="label">验证码</text>
          <input
            v-model="code"
            type="number"
            placeholder="请输入验证码"
            maxlength="6"
            class="input code-input"
          />
          <button
            class="code-btn"
            :class="{ disabled: countdown > 0 }"
            :disabled="countdown > 0"
            @tap="sendCode"
          >
            {{ countdown > 0 ? `${countdown}秒` : '获取验证码' }}
          </button>
        </view>
        <button class="login-btn" @tap="phoneLogin" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </view>

      <!-- 微信登录 -->
      <view class="divider">
        <text class="divider-text">或</text>
      </view>

      <button class="wx-login-btn" open-type="getUserInfo" @getuserinfo="wxLogin">
        <image src="/static/wx-icon.png" class="wx-icon" />
        微信一键登录
      </button>

      <view class="agreement">
        <checkbox-group @change="agreeChange">
          <label>
            <checkbox :checked="agreed" color="#667eea" />
            <text class="agreement-text">
              我已阅读并同意
              <text class="link" @tap.stop="showAgreement">《用户协议》</text>
              和
              <text class="link" @tap.stop="showPrivacy">《隐私政策》</text>
            </text>
          </label>
        </checkbox-group>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api/user'

const userStore = useUserStore()
const phone = ref('')
const code = ref('')
const loading = ref(false)
const countdown = ref(0)
const agreed = ref(false)

const sendCode = async () => {
  if (!phone.value) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  if (!/^1[3-9]\d{9}$/.test(phone.value)) {
    uni.showToast({ title: '手机号格式不正确', icon: 'none' })
    return
  }

  try {
    const res = await userApi.sendCode(phone.value)
    if (res.code === 200) {
      uni.showToast({ title: '验证码已发送', icon: 'success' })
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) {
          clearInterval(timer)
        }
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '发送失败', icon: 'none' })
    }
  } catch (e: any) {
    uni.showToast({ title: e.message || '发送失败', icon: 'none' })
  }
}

const phoneLogin = async () => {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }
  if (!phone.value) {
    uni.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  if (!code.value) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }

  loading.value = true
  try {
    const res = await userApi.login({ phone: phone.value, code: code.value })
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data.userInfo)
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' })
    }
  } catch (e: any) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const wxLogin = async (e: any) => {
  if (!agreed.value) {
    uni.showToast({ title: '请先同意用户协议', icon: 'none' })
    return
  }
  if (e.detail.errMsg !== 'getUserInfo:ok') {
    return
  }

  try {
    uni.showLoading({ title: '登录中...' })
    const loginRes = await uni.login({ provider: 'weixin' })
    const res = await userApi.wxLogin(loginRes.code)
    if (res.code === 200) {
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data.userInfo)
      uni.showToast({ title: '登录成功', icon: 'success' })
      setTimeout(() => {
        uni.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } else {
      uni.showToast({ title: res.message || '登录失败', icon: 'none' })
    }
  } catch (e: any) {
    uni.showToast({ title: e.message || '登录失败', icon: 'none' })
  } finally {
    uni.hideLoading()
  }
}

const agreeChange = (e: any) => {
  agreed.value = e.detail.value.length > 0
}

const showAgreement = () => {
  uni.navigateTo({ url: '/pages/webview?url=/agreement' })
}

const showPrivacy = () => {
  uni.navigateTo({ url: '/pages/webview?url=/privacy' })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 60rpx;
}

.logo-section {
  margin-top: 150rpx;
  display: flex;
  flex-direction: column;
  align-items: center;

  .logo {
    width: 160rpx;
    height: 160rpx;
    margin-bottom: 30rpx;
  }

  .app-name {
    font-size: 48rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 16rpx;
  }

  .app-slogan {
    font-size: 28rpx;
    color: rgba(255, 255, 255, 0.8);
  }
}

.form-section {
  width: 100%;
  margin-top: 100rpx;

  .phone-login {
    .input-group {
      display: flex;
      align-items: center;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50rpx;
      padding: 20rpx 30rpx;
      margin-bottom: 30rpx;

      .label {
        color: #ffffff;
        font-size: 28rpx;
        width: 100rpx;
      }

      .input {
        flex: 1;
        color: #ffffff;
        font-size: 28rpx;

        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }
      }

      .code-input {
        flex: 1;
      }

      .code-btn {
        background: rgba(255, 255, 255, 0.3);
        color: #ffffff;
        font-size: 24rpx;
        padding: 10rpx 20rpx;
        border: none;
        border-radius: 30rpx;
        margin-left: 20rpx;

        &.disabled {
          opacity: 0.5;
        }
      }
    }

    .login-btn {
      width: 100%;
      background: #ffffff;
      color: #667eea;
      font-size: 32rpx;
      font-weight: bold;
      padding: 25rpx 0;
      border: none;
      border-radius: 50rpx;
      margin-top: 20rpx;
    }
  }

  .divider {
    display: flex;
    align-items: center;
    margin: 40rpx 0;

    &::before,
    &::after {
      content: '';
      flex: 1;
      height: 1px;
      background: rgba(255, 255, 255, 0.3);
    }

    .divider-text {
      color: rgba(255, 255, 255, 0.6);
      font-size: 24rpx;
      margin: 0 20rpx;
    }
  }

  .wx-login-btn {
    width: 100%;
    background: #07c160;
    color: #ffffff;
    font-size: 30rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 25rpx 0;
    border: none;
    border-radius: 50rpx;

    .wx-icon {
      width: 40rpx;
      height: 40rpx;
      margin-right: 12rpx;
    }
  }

  .agreement {
    margin-top: 40rpx;
    text-align: center;

    .agreement-text {
      color: rgba(255, 255, 255, 0.8);
      font-size: 24rpx;

      .link {
        color: #ffffff;
        text-decoration: underline;
      }
    }
  }
}
</style>
