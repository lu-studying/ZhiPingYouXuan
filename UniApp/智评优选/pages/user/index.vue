<template>
  <view class="user-container">
    <view v-if="isLoggedIn" class="user-info">
      <text class="welcome">欢迎，{{ displayName }}</text>
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
import { getMyInfo, updateMyInfo } from '@/api/users'

const authStore = useAuthStore()
const userInfo = ref(null)

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

/**
 * 加载用户信息
 */
const loadUserInfo = async () => {
  // 先从本地存储获取
  userInfo.value = authStore.userInfo || uni.getStorageSync('userInfo')
  
  // 如果本地没有，尝试从后端获取
  if (!userInfo.value && authStore.isLoggedIn) {
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

onMounted(() => {
  loadUserInfo()
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

