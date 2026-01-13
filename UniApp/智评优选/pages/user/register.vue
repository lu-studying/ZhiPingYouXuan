<template>
  <view class="register-container">
    <view class="form">
      <view class="form-item">
        <text class="label">账号</text>
        <input 
          v-model="account" 
          placeholder="请输入手机号或邮箱"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">密码</text>
        <input 
          v-model="password" 
          type="password"
          placeholder="请输入密码（至少6位）"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <text class="label">确认密码</text>
        <input 
          v-model="confirmPassword" 
          type="password"
          placeholder="请再次输入密码"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <button class="btn-primary" @click="handleRegister">注册</button>
      </view>
      
      <view class="form-item">
        <text class="link" @click="goToLogin">已有账号？去登录</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { register } from '@/api/auth'
import { useAuthStore } from '@/store/auth'

const account = ref('')
const password = ref('')
const confirmPassword = ref('')
const authStore = useAuthStore()

const handleRegister = async () => {
  if (!account.value.trim()) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return
  }
  
  if (!password.value.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  if (password.value.length < 6) {
    uni.showToast({ title: '密码至少6位', icon: 'none' })
    return
  }
  
  if (password.value !== confirmPassword.value) {
    uni.showToast({ title: '两次密码不一致', icon: 'none' })
    return
  }
  
  try {
    const res = await register(account.value, password.value)
    
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
  }
}

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form {
  width: 100%;
  background-color: #fff;
  padding: 60rpx 40rpx;
  border-radius: 20rpx;
}

.form-item {
  margin-bottom: 40rpx;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 20rpx;
}

.input {
  width: 100%;
  height: 80rpx;
  padding: 0 20rpx;
  border: 1rpx solid #ddd;
  border-radius: 10rpx;
  font-size: 28rpx;
}

.btn-primary {
  width: 100%;
  height: 80rpx;
  background-color: #3c9cff;
  color: #fff;
  border-radius: 10rpx;
  line-height: 80rpx;
  text-align: center;
  border: none;
  margin-top: 40rpx;
}

.link {
  display: block;
  text-align: center;
  color: #3c9cff;
  font-size: 26rpx;
  margin-top: 20rpx;
}
</style>

