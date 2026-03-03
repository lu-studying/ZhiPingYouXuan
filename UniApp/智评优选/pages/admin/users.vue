<template>
  <view class="users-container">
    <view class="search-bar">
      <input 
        v-model="keyword" 
        placeholder="搜索手机号或邮箱"
        class="search-input"
        @confirm="handleSearch"
      />
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>
    
    <view class="users-list">
      <view v-if="loading" class="loading-wrapper">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="users.length === 0" class="empty-wrapper">
        <text>暂无用户数据</text>
      </view>
      
      <view v-else>
        <view 
          v-for="user in users" 
          :key="user.id" 
          class="user-item"
        >
          <view class="user-info">
            <text class="user-id">ID: {{ user.id }}</text>
            <text class="user-account">{{ user.mobile || user.email || '未设置' }}</text>
            <text class="user-nickname">{{ user.nickname || '未设置昵称' }}</text>
          </view>
          <view class="user-meta">
            <text class="user-status" :class="{ active: user.status === 1 }">
              {{ user.status === 1 ? '正常' : '禁用' }}
            </text>
            <text class="user-time">{{ formatTime(user.createdAt) }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <view v-if="total > size" class="pagination">
      <button 
        class="page-btn" 
        :disabled="page === 0"
        @click="prevPage"
      >
        上一页
      </button>
      <text class="page-info">{{ page + 1 }} / {{ totalPages }}</text>
      <button 
        class="page-btn" 
        :disabled="page >= totalPages - 1"
        @click="nextPage"
      >
        下一页
      </button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList } from '@/api/admin'
import { useAuthStore } from '@/store/auth'

const authStore = useAuthStore()

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(0)
const size = ref(10)
const total = ref(0)

const totalPages = computed(() => {
  return Math.ceil(total.value / size.value)
})

/**
 * 加载用户列表
 */
const loadUsers = async () => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    const res = await getUserList({
      page: page.value,
      size: size.value,
      keyword: keyword.value.trim()
    })
    
    users.value = res.content || []
    total.value = res.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
    const message = error?.data?.message || error?.data?.error || '加载失败'
    uni.showToast({
      title: message,
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  page.value = 0
  loadUsers()
}

/**
 * 上一页
 */
const prevPage = () => {
  if (page.value > 0) {
    page.value--
    loadUsers()
  }
}

/**
 * 下一页
 */
const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++
    loadUsers()
  }
}

/**
 * 格式化时间
 */
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

onMounted(() => {
  // 检查权限
  if (!authStore.isAdmin) {
    uni.showToast({
      title: '无权限访问',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
    return
  }
  
  loadUsers()
})
</script>

<style scoped>
.users-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  background: #fff;
  padding: 30rpx;
  display: flex;
  gap: 20rpx;
  align-items: center;
}

.search-input {
  flex: 1;
  height: 70rpx;
  padding: 0 24rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.search-btn {
  padding: 0 30rpx;
  height: 70rpx;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.users-list {
  padding: 20rpx;
}

.loading-wrapper,
.empty-wrapper {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.user-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  flex: 1;
}

.user-id {
  display: block;
  font-size: 24rpx;
  color: #999;
  margin-bottom: 8rpx;
}

.user-account {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.user-nickname {
  display: block;
  font-size: 26rpx;
  color: #666;
}

.user-meta {
  text-align: right;
}

.user-status {
  display: block;
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
  font-size: 24rpx;
  background: #f0f0f0;
  color: #999;
  margin-bottom: 12rpx;
}

.user-status.active {
  background: #d4edda;
  color: #28a745;
}

.user-time {
  display: block;
  font-size: 24rpx;
  color: #999;
}

.pagination {
  padding: 40rpx;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 30rpx;
}

.page-btn {
  padding: 12rpx 30rpx;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  font-size: 28rpx;
}

.page-btn:disabled {
  opacity: 0.5;
}

.page-info {
  font-size: 28rpx;
  color: #666;
}
</style>

