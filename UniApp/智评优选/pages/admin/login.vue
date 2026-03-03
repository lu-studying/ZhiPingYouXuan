<template>
  <view class="login-container">
    <view class="form">
      <view class="form-header">
        <text class="title">管理端登录</text>
        <text class="subtitle">运营管理 / 商家管理</text>
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
        <text class="link" @click="goToUserLogin">返回用户登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { getMyInfo } from '@/api/users'

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
  
  const phoneRegex = /^1[1-9]\d{9}$/
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
  
  passwordError.value = ''
  return true
}

const clearAccountError = () => {
  accountError.value = ''
}

const clearPasswordError = () => {
  passwordError.value = ''
}

/**
 * 处理登录
 */
const handleLogin = async () => {
  // 表单验证
  if (!validateAccount() || !validatePassword()) {
    return
  }
  
  if (submitting.value) {
    return
  }
  
  submitting.value = true
  
  try {
    // 调用登录接口
    const res = await login(account.value.trim(), password.value.trim())
    
    if (res && res.token) {
      // 保存 token
      authStore.setToken(res.token)
      
      // 获取用户信息
      try {
        const userInfo = await getMyInfo()
        authStore.setUserInfo(userInfo)
      } catch (err) {
        console.error('获取用户信息失败:', err)
      }
      
      // 检查角色权限
      const roles = authStore.roles
      if (!roles.includes('ADMIN') && !roles.includes('MERCHANT')) {
        uni.showToast({
          title: '该账号无管理权限',
          icon: 'none',
          duration: 2000
        })
        authStore.logout()
        submitting.value = false
        return
      }
      
      // 跳转到管理端首页
      uni.reLaunch({
        url: '/pages/admin/index'
      })
    } else {
      throw new Error('登录失败：未返回 token')
    }
  } catch (error) {
    console.error('登录失败:', error)
    const message = error?.data?.message || error?.message || '登录失败，请稍后重试'
    uni.showToast({
      title: message,
      icon: 'none',
      duration: 2000
    })
  } finally {
    submitting.value = false
  }
}

/**
 * 返回用户登录页
 */
const goToUserLogin = () => {
  uni.navigateTo({
    url: '/pages/user/login'
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.form {
  width: 100%;
  max-width: 600rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
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
  margin-bottom: 16rpx;
}

.subtitle {
  display: block;
  font-size: 28rpx;
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
  background: #f8f8f8;
  box-sizing: border-box;
}

.input.error {
  border-color: #ff4757;
}

.error-text {
  display: block;
  color: #ff4757;
  font-size: 24rpx;
  margin-top: 12rpx;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 12rpx;
  font-size: 32rpx;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-primary.disabled {
  opacity: 0.6;
}

.link {
  display: block;
  text-align: center;
  color: #667eea;
  font-size: 26rpx;
  text-decoration: underline;
}
</style>

