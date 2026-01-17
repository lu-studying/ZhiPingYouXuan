<template>
  <view class="login-container">
    <view class="form">
      <view class="form-header">
        <text class="title">登录</text>
        <text class="subtitle">欢迎回来</text>
      </view>
      
      <view class="form-item">
        <text class="label">账号</text>
        <input 
          v-model="account" 
          placeholder="请输入手机号或邮箱"
          class="input"
          :class="{ error: accountError }"
          @blur="validateAccount"
          @input="clearAccountError"
        />
        <text v-if="accountError" class="error-text">{{ accountError }}</text>
      </view>
      
      <view class="form-item">
        <text class="label">密码</text>
        <input 
          v-model="password" 
          type="password"
          placeholder="请输入密码"
          class="input"
          :class="{ error: passwordError }"
          @blur="validatePassword"
          @input="clearPasswordError"
        />
        <text v-if="passwordError" class="error-text">{{ passwordError }}</text>
      </view>
      
      <view class="form-item">
        <button 
          class="btn-primary" 
          :class="{ disabled: submitting }"
          :disabled="submitting"
          @click="handleLogin"
        >
          <text v-if="!submitting">登录</text>
          <text v-else>登录中...</text>
        </button>
      </view>
      
      <view class="form-item">
        <text class="link" @click="goToRegister">还没有账号？去注册</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const account = ref('')
const password = ref('')
const accountError = ref('')
const passwordError = ref('')
const submitting = ref(false)
const authStore = useAuthStore()

/**
 * 验证账号格式（手机号或邮箱）
 */
const validateAccount = () => {
  const value = account.value.trim()
  if (!value) {
    accountError.value = '请输入账号'
    return false
  }
  
  // 手机号正则：11位数字
  const phoneRegex = /^1[1-9]\d{9}$/
  // 邮箱正则
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  
  if (!phoneRegex.test(value) && !emailRegex.test(value)) {
    accountError.value = '请输入正确的手机号或邮箱'
    return false
  }
  
  accountError.value = ''
  return true
}

/**
 * 验证密码
 */
const validatePassword = () => {
  const value = password.value.trim()
  if (!value) {
    passwordError.value = '请输入密码'
    return false
  }
  
  if (value.length < 6) {
    passwordError.value = '密码至少6位'
    return false
  }
  
  passwordError.value = ''
  return true
}

/**
 * 清除账号错误
 */
const clearAccountError = () => {
  if (accountError.value) {
    accountError.value = ''
  }
}

/**
 * 清除密码错误
 */
const clearPasswordError = () => {
  if (passwordError.value) {
    passwordError.value = ''
  }
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 验证表单
  if (!validateAccount() || !validatePassword()) {
    return
  }
  
  if (submitting.value) return
  
  try {
    submitting.value = true
    const res = await login(account.value.trim(), password.value)
    
    if (res.token) {
      authStore.setToken(res.token)
      // 保存用户信息，如果没有返回 userInfo，则保存登录账号
      const userInfo = res.userInfo || {}
      // 如果 userInfo 中没有 mobile 和 email，且登录账号是手机号或邮箱，则保存
      if (!userInfo.mobile && !userInfo.email) {
        const accountValue = account.value.trim()
        // 判断是手机号还是邮箱
        if (/^1[1-9]\d{9}$/.test(accountValue)) {
          userInfo.mobile = accountValue
        } else if (accountValue.includes('@')) {
          userInfo.email = accountValue
        }
      }
      authStore.setUserInfo(userInfo)
      
      uni.showToast({ title: '登录成功', icon: 'success' })
      
      setTimeout(() => {
        // 返回上一页或跳转到首页
        const pages = getCurrentPages()
        if (pages.length > 1) {
          uni.navigateBack()
        } else {
          uni.switchTab({ url: '/pages/home/index' })
        }
      }, 1500)
    }
  } catch (error) {
    console.error('登录失败:', error)
    const errorMsg = error?.message || error?.data?.message || '登录失败，请检查账号密码'
    uni.showToast({ title: errorMsg, icon: 'none', duration: 2000 })
  } finally {
    submitting.value = false
  }
}

const goToRegister = () => {
  uni.navigateTo({ url: '/pages/user/register' })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form {
  width: 100%;
  max-width: 600rpx;
  background-color: #fff;
  padding: 60rpx 40rpx;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 60rpx;
}

.title {
  display: block;
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.subtitle {
  display: block;
  font-size: 26rpx;
  color: #999;
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.input {
  width: 100%;
  height: 88rpx;
  padding: 0 24rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 12rpx;
  font-size: 28rpx;
  background-color: #fafafa;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.input:focus {
  border-color: #3c9cff;
  background-color: #fff;
}

.input.error {
  border-color: #ff4757;
  background-color: #fff5f5;
}

.error-text {
  display: block;
  font-size: 24rpx;
  color: #ff4757;
  margin-top: 8rpx;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 12rpx;
  line-height: 88rpx;
  text-align: center;
  border: none;
  margin-top: 40rpx;
  font-size: 32rpx;
  font-weight: 500;
  transition: opacity 0.3s;
}

.btn-primary.disabled {
  opacity: 0.6;
}

.btn-primary:active:not(.disabled) {
  opacity: 0.8;
}

.link {
  display: block;
  text-align: center;
  color: #3c9cff;
  font-size: 26rpx;
  margin-top: 20rpx;
}
</style>

