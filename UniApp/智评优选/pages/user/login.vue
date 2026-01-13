<template>
  <view class="login-container">
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
          placeholder="请输入密码"
          class="input"
        />
      </view>
      
      <view class="form-item">
        <button class="btn-primary" @click="handleLogin">登录</button>
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
const authStore = useAuthStore()

const handleLogin = async () => {
  if (!account.value.trim()) {
    uni.showToast({ title: '请输入账号', icon: 'none' })
    return
  }
  
  if (!password.value.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  try {
    const res = await login(account.value, password.value)
    
    if (res.token) {
      authStore.setToken(res.token)
      authStore.setUserInfo(res.userInfo || {})
      
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
  }
}

const goToRegister = () => {
  uni.navigateTo({ url: '/pages/user/register' })
}
</script>

<style scoped>
.login-container {
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

