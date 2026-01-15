<template>
  <view class="register-container">
    <view class="form">
      <view class="form-header">
        <text class="title">注册</text>
        <text class="subtitle">创建新账号</text>
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
          placeholder="请输入密码（至少6位）"
          class="input"
          :class="{ error: passwordError }"
          @blur="validatePassword"
          @input="clearPasswordError"
        />
        <text v-if="passwordError" class="error-text">{{ passwordError }}</text>
        <text v-if="passwordStrength" class="strength-text" :class="passwordStrengthClass">
          {{ passwordStrengthText }}
        </text>
      </view>
      
      <view class="form-item">
        <text class="label">确认密码</text>
        <input 
          v-model="confirmPassword" 
          type="password"
          placeholder="请再次输入密码"
          class="input"
          :class="{ error: confirmPasswordError }"
          @blur="validateConfirmPassword"
          @input="clearConfirmPasswordError"
        />
        <text v-if="confirmPasswordError" class="error-text">{{ confirmPasswordError }}</text>
      </view>
      
      <view class="form-item">
        <button 
          class="btn-primary" 
          :class="{ disabled: submitting }"
          :disabled="submitting"
          @click="handleRegister"
        >
          <text v-if="!submitting">注册</text>
          <text v-else>注册中...</text>
        </button>
      </view>
      
      <view class="form-item">
        <text class="link" @click="goToLogin">已有账号？去登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { register } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const account = ref('')
const password = ref('')
const confirmPassword = ref('')
const accountError = ref('')
const passwordError = ref('')
const confirmPasswordError = ref('')
const submitting = ref(false)
const authStore = useAuthStore()

/**
 * 密码强度检测
 */
const passwordStrength = computed(() => {
  const value = password.value.trim()
  if (!value) return null
  
  if (value.length < 6) return null
  
  let strength = 0
  if (value.length >= 6) strength++
  if (value.length >= 8) strength++
  if (/[a-z]/.test(value) && /[A-Z]/.test(value)) strength++
  if (/\d/.test(value)) strength++
  if (/[^a-zA-Z0-9]/.test(value)) strength++
  
  return strength
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === null) return ''
  if (strength <= 2) return '密码强度：弱'
  if (strength <= 3) return '密码强度：中'
  return '密码强度：强'
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength === null) return ''
  if (strength <= 2) return 'strength-weak'
  if (strength <= 3) return 'strength-medium'
  return 'strength-strong'
})

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
  const phoneRegex = /^1[3-9]\d{9}$/
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
 * 验证确认密码
 */
const validateConfirmPassword = () => {
  const value = confirmPassword.value.trim()
  if (!value) {
    confirmPasswordError.value = '请确认密码'
    return false
  }
  
  if (value !== password.value.trim()) {
    confirmPasswordError.value = '两次密码不一致'
    return false
  }
  
  confirmPasswordError.value = ''
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
 * 清除确认密码错误
 */
const clearConfirmPasswordError = () => {
  if (confirmPasswordError.value) {
    confirmPasswordError.value = ''
  }
}

/**
 * 处理注册
 */
const handleRegister = async () => {
  // 验证表单
  if (!validateAccount() || !validatePassword() || !validateConfirmPassword()) {
    return
  }
  
  if (submitting.value) return
  
  try {
    submitting.value = true
    const res = await register(account.value.trim(), password.value)
    
    if (res.token) {
      authStore.setToken(res.token)
      authStore.setUserInfo(res.userInfo || {})
      
      uni.showToast({ title: '注册成功', icon: 'success' })
      
      setTimeout(() => {
        uni.switchTab({ url: '/pages/home/index' })
      }, 1500)
    }
  } catch (error) {
    console.error('注册失败:', error)
    const errorMsg = error?.message || error?.data?.message || '注册失败，请稍后重试'
    uni.showToast({ title: errorMsg, icon: 'none', duration: 2000 })
  } finally {
    submitting.value = false
  }
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}
</script>

<style scoped>
.register-container {
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

.strength-text {
  display: block;
  font-size: 24rpx;
  margin-top: 8rpx;
}

.strength-weak {
  color: #ff4757;
}

.strength-medium {
  color: #ffa502;
}

.strength-strong {
  color: #2ed573;
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

