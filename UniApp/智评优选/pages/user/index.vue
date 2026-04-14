<template>
  <view class="user-container">
    <view v-if="isLoggedIn" class="user-info">
      <text class="welcome">欢迎，{{ displayName }}</text>
      <view class="wallet-card" @click="goToWallet">
        <view class="wallet-main">
          <text class="wallet-label">账户余额</text>
          <text class="wallet-value">¥{{ walletBalanceDisplay }}</text>
        </view>
      </view>
      <view class="menu-list">
        <view class="menu-item" @click="handleEditNickname">
          <text>修改昵称</text>
        </view>
        <view class="menu-item" @click="goToOrders">
          <text>我的订单</text>
        </view>
        <view class="menu-item" @click="goToReviews">
          <text>我的点评</text>
        </view>
        <view class="menu-item" @click="goToLikedReviews">
          <text>我的点赞</text>
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
import { onShow } from '@dcloudio/uni-app'
import { useAuthStore } from '@/store/auth'
import { logout } from '@/api/auth'
import { getMyInfo, updateMyInfo, getMyWallet } from '@/api/users'

const authStore = useAuthStore()
const userInfo = ref(null)
const walletBalance = ref(0)

const isLoggedIn = computed(() => {
  return authStore.isLoggedIn
})

/**
 * 获取显示名称
 * 优先级：nickname > mobile > email > '用户'
 */
const displayName = computed(() => {
  if (!userInfo.value) {
    return '用户'
  }
  
  // 优先显示昵称
  if (userInfo.value.nickname) {
    return userInfo.value.nickname
  }
  
  // 如果没有昵称，显示手机号（格式：用户12345678910）
  if (userInfo.value.mobile) {
    return `用户${userInfo.value.mobile}`
  }
  
  // 如果没有手机号，显示邮箱（格式：用户test@example.com）
  if (userInfo.value.email) {
    return `用户${userInfo.value.email}`
  }
  
  return '用户'
})

const walletBalanceDisplay = computed(() => Number(walletBalance.value || 0).toFixed(2))

/**
 * 加载用户信息
 */
const loadUserInfo = async () => {
  // 先用本地缓存展示，避免白屏
  userInfo.value = authStore.userInfo || uni.getStorageSync('userInfo')

  // 只要已登录，就主动向后端拉取最新用户信息，确保昵称/头像实时更新
  if (authStore.isLoggedIn) {
    try {
      const res = await getMyInfo()
      if (res) {
        userInfo.value = res
        authStore.setUserInfo(res)
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
    }
  }
}

const loadWallet = async () => {
  if (!authStore.isLoggedIn) {
    walletBalance.value = 0
    return
  }
  try {
    const res = await getMyWallet()
    walletBalance.value = Number(res?.balance || 0)
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

onMounted(() => {
  loadUserInfo()
  loadWallet()
})

// 页面显示时也刷新一次，解决切换账号后需要刷新才能看到最新昵称的问题
onShow(() => {
  loadUserInfo()
  loadWallet()
})

const goToWallet = () => {
  uni.navigateTo({ url: '/pages/user/wallet' })
}

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

const goToLikedReviews = () => {
  uni.navigateTo({ url: '/pages/user/liked-reviews' })
}

const goToTags = () => {
  uni.navigateTo({ url: '/pages/user/tags' })
}

/**
 * 处理修改昵称
 */
const handleEditNickname = () => {
  const currentNickname = userInfo.value?.nickname || ''
  
  uni.showModal({
    title: '修改昵称',
    editable: true,
    placeholderText: '请输入新昵称',
    content: currentNickname,
    success: async (res) => {
      if (res.confirm) {
        const newNickname = res.content?.trim() || ''
        
        // 验证昵称
        if (!newNickname) {
          uni.showToast({ title: '昵称不能为空', icon: 'none' })
          return
        }
        
        if (newNickname.length > 20) {
          uni.showToast({ title: '昵称不能超过20个字符', icon: 'none' })
          return
        }
        
        // 如果昵称没有变化，直接返回
        if (newNickname === currentNickname) {
          return
        }
        
        try {
          // 调用API更新昵称
          const updated = await updateMyInfo({ nickname: newNickname })
          
          // 更新本地用户信息
          userInfo.value = updated
          authStore.setUserInfo(updated)
          
          uni.showToast({ title: '修改成功', icon: 'success' })
        } catch (error) {
          console.error('修改昵称失败:', error)
          
          // 处理错误信息
          let errorMessage = '修改失败，请稍后重试'
          
          // uni.request 的响应结构：error 就是 res 对象
          // 如果后端返回了错误信息，使用后端的信息
          if (error.data?.error) {
            errorMessage = error.data.error
          } else if (error.data?.message) {
            errorMessage = error.data.message
          } else if (error.message) {
            errorMessage = error.message
          }
          
          uni.showToast({ title: errorMessage, icon: 'none', duration: 2000 })
        }
      }
    }
  })
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

.wallet-card {
  margin-bottom: 16rpx;
  padding: 24rpx;
  border-radius: 12rpx;
  background: linear-gradient(135deg, #fff8e6 0%, #fff 100%);
  border: 1rpx solid #ffe58f;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.wallet-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.wallet-label {
  font-size: 28rpx;
  color: #8c6b00;
}

.wallet-value {
  font-size: 28rpx;
  color: #d48806;
  font-weight: bold;
}

.wallet-card::after {
  content: '>';
  float: right;
  margin-top: 6rpx;
  font-size: 24rpx;
  color: #d48806;
}

.menu-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.menu-item {
  padding: 24rpx;
  border-radius: 12rpx;
  background: linear-gradient(135deg, #fff8e6 0%, #fff 100%);
  border: 1rpx solid #ffe58f;
  font-size: 28rpx;
  color: #8c6b00;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.menu-item::after {
  content: '>';
  font-size: 24rpx;
  color: #d48806;
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

