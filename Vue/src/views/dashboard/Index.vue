<template>
  <!-- 仪表盘页面容器 -->
  <div class="dashboard-container">
    <!-- 页面标题和用户信息 -->
    <div class="dashboard-header">
      <div class="header-left">
        <h1 class="page-title">仪表盘</h1>
        <p class="page-subtitle">数据概览与统计</p>
      </div>
      <!-- 用户头像下拉菜单 -->
      <div class="header-right">
        <el-dropdown trigger="hover" @command="handleCommand">
          <div class="user-avatar-wrapper">
            <el-avatar :size="40" class="user-avatar">
              <el-icon><UserFilled /></el-icon>
            </el-avatar>
            <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item disabled>
                <div class="user-info-item">
                  <div class="user-info-label">用户名</div>
                  <div class="user-info-value">{{ userDisplayName }}</div>
                </div>
              </el-dropdown-item>
              <el-dropdown-item disabled>
                <div class="user-info-item">
                  <div class="user-info-label">登录时间</div>
                  <div class="user-info-value">{{ loginTime }}</div>
                </div>
              </el-dropdown-item>
              <el-dropdown-item divided command="logout">
                <el-icon><SwitchButton /></el-icon>
                <span style="margin-left: 8px;">退出登录</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="stats-row">
      <!-- 商家总数卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #409eff;">
              <el-icon :size="32"><ShopFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.shopCount || 0 }}</div>
              <div class="stat-label">商家总数</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-link type="primary" :underline="false" @click="goToShops">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </el-card>
      </el-col>

      <!-- AI 调用总次数卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #9c27b0;">
              <el-icon :size="32"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.aiCallCount || 0 }}</div>
              <div class="stat-label">AI 调用次数</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-link type="primary" :underline="false" @click="goToAiLogs">
              查看日志 <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </el-card>
      </el-col>

      <!-- 点评总数卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #e6a23c;">
              <el-icon :size="32"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.reviewCount || 0 }}</div>
              <div class="stat-label">点评总数</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-link type="primary" :underline="false" @click="goToReviews">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </el-card>
      </el-col>

      <!-- 订单总数卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #f56c6c;">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.orderCount || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
          <div class="stat-footer">
            <el-link type="primary" :underline="false" @click="goToOrders">
              查看详情 <el-icon><ArrowRight /></el-icon>
            </el-link>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- AI 统计卡片区域 -->
    <el-row :gutter="20" class="stats-row">
      <!-- 正常商家数卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #67c23a;">
              <el-icon :size="32"><CircleCheckFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayNewShops || 0 }}</div>
              <div class="stat-label">今日新增商家</div>
            </div>
          </div>
     
        </el-card>
      </el-col>

      <!-- AI 调用成功率卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #00bcd4;">
              <el-icon :size="32"><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ formatPercentage(stats.aiCallSuccessRate) }}</div>
              <div class="stat-label">AI 调用成功率</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 今日新增点评卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #ff9800;">
              <el-icon :size="32"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayNewReviews || 0 }}</div>
              <div class="stat-label">今日新增点评</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 今日新增订单卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background-color: #4caf50;">
              <el-icon :size="32"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.todayNewOrders || 0 }}</div>
              <div class="stat-label">今日新增订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格区域 -->
    <el-row :gutter="20" class="data-row">
      <!-- 最近商家列表 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>最近商家</span>
              <el-button type="primary" text @click="goToShops">查看更多</el-button>
            </div>
          </template>
          <el-table
            :data="recentShops"
            style="width: 100%"
            :loading="loading.shops"
            stripe
          >
            <el-table-column prop="name" label="商家名称" min-width="120" />
            <el-table-column prop="category" label="分类" width="100" />
            <el-table-column prop="avgScore" label="评分" width="120">
              <template #default="{ row }">
                <el-rate
                  :model-value="row.avgScore || 0"
                  disabled
                  show-score
                  text-color="#ff9900"
                  :score-template="`${row.avgScore || 0}`"
                />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" text size="small" @click="viewShop(row.id)">
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 快速操作 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="12">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <span>快速操作</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" size="large" @click="goToShops">
              <el-icon><Plus /></el-icon>
              新增商家
            </el-button>
            <el-button type="success" size="large" @click="goToShops">
              <el-icon><List /></el-icon>
              商家管理
            </el-button>
            <el-button type="warning" size="large" @click="goToReviews">
              <el-icon><ChatDotRound /></el-icon>
              点评管理
            </el-button>
            <el-button type="info" size="large" @click="goToOrders">
              <el-icon><Document /></el-icon>
              订单管理
            </el-button>
            <el-button type="primary" size="large" @click="goToUsers">
              <el-icon><User /></el-icon>
              用户管理
            </el-button>
            <el-button type="success" size="large" @click="goToAiLogs">
              <el-icon><Star /></el-icon>
              AI调用日志
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
/**
 * 仪表盘页面组件
 * 
 * 功能说明：
 * 1. 显示核心数据统计（商家总数、点评总数、订单总数等）
 * 2. 显示最近商家列表
 * 3. 提供快速操作入口
 * 4. 数据自动刷新
 */

import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
// Element Plus 图标（已在 main.js 中全局注册，可以直接使用）
// 注意：图标组件名称需要与 Element Plus Icons 库中的名称一致
import { listShops, getShopCount } from '@/api/shops'
import { getDashboardStats } from '@/api/dashboard'
import { isTokenExpired } from '@/utils/jwt'
import { logout } from '@/api/auth'

// 使用 Vue Router
const router = useRouter()

// 使用 Vuex store（用于获取用户信息和清除登录状态）
const store = useStore()

// 获取用户信息
const userInfo = computed(() => store.getters['auth/userInfo'])

// 计算用户显示名称（手机号或邮箱）
const userDisplayName = computed(() => {
  if (userInfo.value && userInfo.value.mobileOrEmail) {
    return userInfo.value.mobileOrEmail
  }
  return '未登录'
})

// 获取登录时间（从 localStorage 读取，如果不存在则显示当前时间）
const loginTime = computed(() => {
  const savedLoginTime = localStorage.getItem('loginTime')
  if (savedLoginTime) {
    const date = new Date(savedLoginTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  }
  return '未知'
})

// 统计数据
const stats = reactive({
  shopCount: 0,              // 商家总数
  reviewCount: 0,            // 点评总数
  orderCount: 0,             // 订单总数
  aiCallCount: 0,            // AI 调用总次数
  aiCallSuccessRate: 0,      // AI 调用成功率（0.0 - 1.0）
  todayNewShops: 0,          // 今日新增商家数
  todayNewReviews: 0,        // 今日新增点评数
  todayNewOrders: 0          // 今日新增订单数
})

// 最近商家列表
const recentShops = ref([])

// 加载状态
const loading = reactive({
  shops: false
})

/**
 * 加载统计数据
 * 
 * 功能：
 * 1. 获取仪表盘统计数据（商家数、点评数、订单数、AI调用统计、今日新增数据等）
 * 2. 获取最近商家列表
 */
const loadStats = async () => {
  loading.shops = true

  try {
    // 1. 获取仪表盘统计数据
    const statsResponse = await getDashboardStats()
    
    // 更新统计数据
    stats.shopCount = statsResponse.shopCount || 0
    stats.reviewCount = statsResponse.reviewCount || 0
    stats.orderCount = statsResponse.orderCount || 0
    stats.aiCallCount = statsResponse.aiCallCount || 0
    stats.aiCallSuccessRate = statsResponse.aiCallSuccessRate || 0
    stats.todayNewShops = statsResponse.todayNewShops || 0
    stats.todayNewReviews = statsResponse.todayNewReviews || 0
    stats.todayNewOrders = statsResponse.todayNewOrders || 0
    
    // 2. 获取最近商家列表（第一页，每页10条）
    const shopsResponse = await listShops({ page: 0, size: 10 })
    recentShops.value = shopsResponse.content || []
    
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    loading.shops = false
  }
}

/**
 * 跳转到商家管理页面
 */
const goToShops = () => {
  router.push('/shops')
}

/**
 * 跳转到点评管理页面
 */
const goToReviews = () => {
  router.push('/reviews')
}

/**
 * 跳转到订单管理页面
 */
const goToOrders = () => {
  router.push('/orders')
}

/**
 * 跳转到用户管理页面
 */
const goToUsers = () => {
  router.push('/users')
}

/**
 * 跳转到 AI 调用日志页面
 */
const goToAiLogs = () => {
  router.push('/ai-logs')
}

/**
 * 格式化百分比
 * 
 * @param {number} rate - 成功率（0.0 - 1.0）
 * @returns {string} 格式化后的百分比字符串（如 "95.5%"）
 */
const formatPercentage = (rate) => {
  if (rate === null || rate === undefined) {
    return '0%'
  }
  return (rate * 100).toFixed(1) + '%'
}

/**
 * 查看商家详情
 * 
 * @param {number} shopId - 商家ID
 */
const viewShop = (shopId) => {
  router.push(`/shops/${shopId}`)
}

/**
 * 处理下拉菜单命令
 * 
 * @param {string} command - 命令名称（如 'logout'）
 */
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  }
}

/**
 * 处理退出登录
 * 
 * 流程：
 * 1. 清除本地存储的 token 和用户信息
 * 2. 清除 Vuex store 中的认证状态
 * 3. 显示成功提示
 * 4. 跳转到登录页
 */
const handleLogout = () => {
  // 清除本地存储的 token 和用户信息
  logout()
  
  // 清除 Vuex store 中的认证状态
  store.dispatch('auth/logout')
  
  // 显示成功提示
  ElMessage.success('已退出登录')
  
  // 跳转到登录页
  router.push('/login')
}

// Token 过期检查定时器
let tokenCheckTimer = null

/**
 * 检查 token 是否过期
 * 
 * 功能：
 * 1. 定期检查 token 是否过期
 * 2. 如果过期，清除本地存储并跳转到登录页
 */
const checkTokenExpiration = () => {
  const token = localStorage.getItem('token')
  
  if (token && isTokenExpired(token)) {
    // Token 已过期，清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    // 清除 Vuex store 中的认证状态
    store.dispatch('auth/logout')
    
    // 显示提示
    ElMessage.warning('登录已过期，请重新登录')
    
    // 跳转到登录页
    router.push('/login')
    
    // 清除定时器
    if (tokenCheckTimer) {
      clearInterval(tokenCheckTimer)
      tokenCheckTimer = null
    }
  }
}

// 组件挂载时加载数据并启动定时检查
onMounted(() => {
  loadStats()
  
  // 每30分钟检查一次 token 是否过期
  // 如果 token 过期时间更长，可以相应调整检查间隔
  tokenCheckTimer = setInterval(checkTokenExpiration, 300000)
  
  // 立即检查一次（防止页面加载时 token 已经过期）
  checkTokenExpiration()
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (tokenCheckTimer) {
    clearInterval(tokenCheckTimer)
    tokenCheckTimer = null
  }
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (tokenCheckTimer) {
    clearInterval(tokenCheckTimer)
    tokenCheckTimer = null
  }
})
</script>

<style scoped>
/**
 * 仪表盘页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 */

/* 页面容器 */
.dashboard-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left {
  flex: 1;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 用户头像区域 */
.header-right {
  display: flex;
  align-items: center;
}

/* 移除 el-dropdown 的所有边框样式 */
.header-right :deep(.el-dropdown) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

.header-right :deep(.el-dropdown__caret-button) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

.header-right :deep(.el-dropdown__caret-button:hover),
.header-right :deep(.el-dropdown__caret-button:focus),
.header-right :deep(.el-dropdown__caret-button:active) {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

.user-avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  border: none !important;
  outline: none !important;
  transition: background-color 0.3s;
}

.user-avatar-wrapper:hover {
  background-color: #f5f7fa;
}

.user-avatar {
  background-color: #409eff;
  color: #ffffff;
}

.dropdown-icon {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
  transition: transform 0.3s;
}

.user-avatar-wrapper:hover .dropdown-icon {
  transform: rotate(180deg);
}

/* 下拉菜单用户信息样式 */
.user-info-item {
  padding: 4px 0;
}

.user-info-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.user-info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 统计卡片行 */
.stats-row {
  margin-bottom: 20px;
}

/* 统计卡片 */
.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  margin-right: 16px;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-footer {
  border-top: 1px solid #ebeef5;
  padding-top: 12px;
  text-align: right;
}

/* 数据表格行 */
.data-row {
  margin-bottom: 20px;
}

/* 数据卡片 */
.data-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 快速操作区域 */
.quick-actions {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

/* 覆盖 Element Plus 的默认样式：相邻按钮的 margin-left */
.quick-actions .el-button + .el-button {
  margin-left: 0 !important;
}

.quick-actions .el-button {
  width: 100%;
  height: 60px;
  font-size: 16px;
}

.quick-actions .el-button .el-icon {
  margin-right: 8px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
