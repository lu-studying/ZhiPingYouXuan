<template>
  <view class="user-container">
    <view v-if="isLoggedIn" class="user-info">
      <text class="welcome">欢迎，{{ userInfo?.nickname || '用户' }}</text>
      <view class="menu-list">
        <view class="menu-item" @click="goToOrders">
          <text>我的订单</text>
        </view>
        <view class="menu-item" @click="goToReviews">
          <text>我的点评</text>
        </view>
        <view class="menu-item" @click="goToTags">
          <text>偏好设置</text>
        </view>
        <view class="menu-item" @click="handleLogout">
          <text>退出登录</text>
        </view>
      </view>
    </view>
    <view v-else class="login-prompt">
      <text>请先登录</text>
      <button class="btn-login" @click="goToLogin">登录</button>
      <button class="btn-register" @click="goToRegister">注册</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { logout } from '@/api/auth'

const authStore = useAuthStore()
const userInfo = ref(null)

const isLoggedIn = computed(() => {
  return authStore.isLoggedIn
})

onMounted(() => {
  userInfo.value = authStore.userInfo || uni.getStorageSync('userInfo')
})

const goToLogin = () => {
  uni.navigateTo({ url: '/pages/user/login' })
}

const goToRegister = () => {
  uni.navigateTo({ url: '/pages/user/register' })
}

const goToOrders = () => {
  uni.navigateTo({ url: '/pages/user/orders' })
}

const goToReviews = () => {
  uni.navigateTo({ url: '/pages/user/reviews' })
}

const goToTags = () => {
  uni.navigateTo({ url: '/pages/user/tags' })
}

const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        logout()
        authStore.logout()
        uni.showToast({ title: '已退出登录', icon: 'success' })
      }
    }
  })
}
</script>

<style scoped>
.user-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.user-info {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.welcome {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 40rpx;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.menu-item {
  padding: 30rpx;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  font-size: 28rpx;
}

.login-prompt {
  text-align: center;
  padding: 100rpx 0;
}

.btn-login, .btn-register {
  width: 200rpx;
  height: 80rpx;
  margin: 20rpx auto;
  border-radius: 10rpx;
  line-height: 80rpx;
  text-align: center;
}

.btn-login {
  background-color: #3c9cff;
  color: #fff;
}

.btn-register {
  background-color: #fff;
  color: #3c9cff;
  border: 1rpx solid #3c9cff;
}
</style>

