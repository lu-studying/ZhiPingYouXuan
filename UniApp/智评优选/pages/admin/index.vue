<template>
  <view class="admin-container">
    <view class="header">
      <view class="user-info">
        <text class="welcome">欢迎，{{ displayName }}</text>
        <text class="role-badge" :class="roleClass">{{ roleText }}</text>
      </view>
      <text class="logout-btn" @click="handleLogout">退出</text>
    </view>
    
    <view class="menu-grid">
      <!-- 管理员菜单 -->
      <view v-if="authStore.isAdmin" class="menu-item" @click="goToUsers">
        <view class="menu-icon admin">👥</view>
        <text class="menu-title">用户管理</text>
        <text class="menu-desc">查看和管理用户</text>
      </view>
      
      <!-- 商家菜单 -->
      <view v-if="authStore.isMerchant || authStore.isAdmin" class="menu-item" @click="goToMenus">
        <view class="menu-icon merchant">🍽️</view>
        <text class="menu-title">菜单管理</text>
        <text class="menu-desc">管理推荐菜品</text>
      </view>
      
      <!-- 商家店铺信息（待实现） -->
      <view v-if="authStore.isMerchant || authStore.isAdmin" class="menu-item" @click="goToShopInfo">
        <view class="menu-icon shop">🏪</view>
        <text class="menu-title">店铺信息</text>
        <text class="menu-desc">查看和编辑店铺</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/store/auth'
import { getMyInfo } from '@/api/users'

const authStore = useAuthStore()

const userInfo = ref(null)

const displayName = computed(() => {
  if (userInfo.value) {
    if (userInfo.value.nickname) {
      return userInfo.value.nickname
    }
    if (userInfo.value.mobile) {
      return `用户${userInfo.value.mobile}`
    }
    if (userInfo.value.email) {
      return `用户${userInfo.value.email}`
    }
  }
  return '管理员'
})

const roleText = computed(() => {
  if (authStore.isAdmin) {
    return '运营管理员'
  }
  if (authStore.isMerchant) {
    return '商家'
  }
  return '用户'
})

const roleClass = computed(() => {
  if (authStore.isAdmin) {
    return 'admin'
  }
  if (authStore.isMerchant) {
    return 'merchant'
  }
  return 'user'
})

/**
 * 加载用户信息
 */
const loadUserInfo = async () => {
  try {
    const info = await getMyInfo()
    userInfo.value = info
    authStore.setUserInfo(info)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

/**
 * 跳转到用户管理
 */
const goToUsers = () => {
  uni.navigateTo({
    url: '/pages/admin/users'
  })
}

/**
 * 跳转到菜单管理
 */
const goToMenus = () => {
  uni.navigateTo({
    url: '/pages/admin/menus'
  })
}

/**
 * 跳转到店铺信息（待实现）
 */
const goToShopInfo = () => {
  uni.showToast({
    title: '功能开发中',
    icon: 'none'
  })
}

/**
 * 退出登录
 */
const handleLogout = () => {
  uni.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        authStore.logout()
        uni.reLaunch({
          url: '/pages/admin/login'
        })
      }
    }
  })
}

onMounted(() => {
  // 检查登录状态
  if (!authStore.isLoggedIn) {
    uni.reLaunch({
      url: '/pages/admin/login'
    })
    return
  }
  
  // 检查角色权限
  if (!authStore.isAdmin && !authStore.isMerchant) {
    uni.showToast({
      title: '无管理权限',
      icon: 'none'
    })
    setTimeout(() => {
      uni.reLaunch({
        url: '/pages/admin/login'
      })
    }, 1500)
    return
  }
  
  loadUserInfo()
})
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  flex: 1;
}

.welcome {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
}

.role-badge {
  display: inline-block;
  padding: 8rpx 20rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
  background: rgba(255, 255, 255, 0.2);
}

.role-badge.admin {
  background: rgba(255, 193, 7, 0.3);
}

.role-badge.merchant {
  background: rgba(40, 167, 69, 0.3);
}

.logout-btn {
  font-size: 28rpx;
  padding: 12rpx 24rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8rpx;
}

.menu-grid {
  padding: 40rpx;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30rpx;
}

.menu-item {
  background: #fff;
  border-radius: 20rpx;
  padding: 40rpx;
  text-align: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: transform 0.2s;
}

.menu-item:active {
  transform: scale(0.98);
}

.menu-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  margin: 0 auto 24rpx;
}

.menu-icon.admin {
  background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
}

.menu-icon.merchant {
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
}

.menu-icon.shop {
  background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
}

.menu-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.menu-desc {
  display: block;
  font-size: 24rpx;
  color: #999;
}
</style>

