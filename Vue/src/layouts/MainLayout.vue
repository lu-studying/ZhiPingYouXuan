<template>
  <!-- 主布局容器 -->
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="app-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo">
            <el-icon :size="24" style="color: #409eff; margin-right: 8px">
              <Shop />
            </el-icon>
            <span class="logo-text">智评优选管理端</span>
          </div>
        </div>
        <div class="header-center">
          <!-- 导航菜单 -->
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            class="nav-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/dashboard">
              <el-icon><Odometer /></el-icon>
              <span>仪表盘</span>
            </el-menu-item>
            <el-menu-item index="/shops">
              <el-icon><Shop /></el-icon>
              <span>商家管理</span>
            </el-menu-item>
            <el-menu-item index="/reviews">
              <el-icon><ChatDotRound /></el-icon>
              <span>点评管理</span>
            </el-menu-item>
            <el-menu-item index="/orders">
              <el-icon><Document /></el-icon>
              <span>订单管理</span>
            </el-menu-item>
            <el-menu-item index="/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/ai-logs">
              <el-icon><List /></el-icon>
              <span>AI调用日志</span>
            </el-menu-item>
          </el-menu>
        </div>
        <div class="header-right">
          <!-- 用户头像下拉菜单 -->
          <el-dropdown trigger="hover" @command="handleCommand">
            <div class="user-avatar-wrapper">
              <el-avatar :size="36" class="user-avatar">
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
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span style="margin-left: 8px;">退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主内容区域 -->
    <el-main class="app-main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import {
  Shop,
  Odometer,
  ChatDotRound,
  Document,
  User,
  List,
  UserFilled,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { logout } from '@/api/auth'
import { isTokenExpired } from '@/utils/jwt'

const router = useRouter()
const route = useRoute()
const store = useStore()

// 获取用户信息
const userInfo = computed(() => store.getters['auth/userInfo'])

// 计算用户显示名称
const userDisplayName = computed(() => {
  if (userInfo.value && userInfo.value.mobileOrEmail) {
    return userInfo.value.mobileOrEmail
  }
  return '未登录'
})

// 计算当前激活的菜单项
const activeMenu = computed(() => {
  const path = route.path
  // 根据路径匹配菜单项
  if (path.startsWith('/shops')) return '/shops'
  if (path.startsWith('/reviews')) return '/reviews'
  if (path.startsWith('/orders')) return '/orders'
  if (path.startsWith('/users')) return '/users'
  if (path.startsWith('/ai-logs')) return '/ai-logs'
  if (path.startsWith('/dashboard')) return '/dashboard'
  return '/dashboard'
})

/**
 * 处理菜单选择
 */
const handleMenuSelect = (index) => {
  router.push(index)
}

/**
 * 处理下拉菜单命令
 */
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  }
}

/**
 * 处理退出登录
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

// Token 过期检查
onMounted(() => {
  const token = localStorage.getItem('token')
  if (token && isTokenExpired(token)) {
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    store.dispatch('auth/logout')
    ElMessage.warning('登录已过期，请重新登录')
    router.push('/login')
  }
})
</script>

<style scoped>
/* 主布局容器 */
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部导航栏 */
.app-header {
  background-color: #ffffff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0;
  height: 60px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.header-left {
  display: flex;
  align-items: center;
  min-width: 200px;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.logo-text {
  margin-left: 4px;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
  background-color: transparent;
}

.nav-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  border-bottom: 2px solid transparent;
}

.nav-menu :deep(.el-menu-item:hover) {
  background-color: #f5f7fa;
  border-bottom-color: #409eff;
}

.nav-menu :deep(.el-menu-item.is-active) {
  color: #409eff;
  border-bottom-color: #409eff;
  background-color: transparent;
}

.header-right {
  display: flex;
  align-items: center;
  min-width: 150px;
  justify-content: flex-end;
}

/* 用户头像区域 */
.user-avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
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

/* 主内容区域 */
.app-main {
  flex: 1;
  padding: 0;
  background-color: #f5f7fa;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .logo-text {
    display: none;
  }
  
  .nav-menu :deep(.el-menu-item span) {
    display: none;
  }
  
  .nav-menu :deep(.el-menu-item) {
    padding: 0 12px;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .header-center {
    display: none;
  }
}
</style>

