<template>
  <!-- 仪表盘页面容器 -->
  <div class="dashboard-container">
    <h1>欢迎来到管理后台</h1>
    <p>登录成功！仪表盘页面正在开发中...</p>
    <!-- 退出登录按钮 -->
    <el-button type="primary" @click="handleLogout">退出登录</el-button>
  </div>
</template>

<script setup>
/**
 * 仪表盘页面组件（占位页面）
 * 
 * 功能说明：
 * 1. 登录成功后的默认跳转页面
 * 2. 提供退出登录功能
 * 3. 后续将添加数据统计、图表等功能
 */

import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { logout } from '@/api/auth'
import { ElMessage } from 'element-plus'

// 使用 Vue Router 的 composables
const router = useRouter() // 用于编程式导航

// 使用 Vuex store
const store = useStore() // 用于调用 actions 清除登录状态

/**
 * 处理退出登录
 * 
 * 流程：
 * 1. 调用 logout API（清除本地存储）
 * 2. 调用 Vuex logout action（清除 store 中的状态）
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
</script>

<style scoped>
/**
 * 仪表盘页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 */

/* 容器样式 */
.dashboard-container {
  padding: 40px; /* 内边距 */
  text-align: center; /* 文本居中 */
}

/* 标题样式 */
h1 {
  font-size: 32px; /* 字体大小 */
  margin-bottom: 16px; /* 下边距 */
  color: #303133; /* 文字颜色（Element Plus 主色调） */
}

/* 段落样式 */
p {
  font-size: 16px; /* 字体大小 */
  color: #909399; /* 文字颜色（Element Plus 次要文字颜色） */
  margin-bottom: 24px; /* 下边距 */
}
</style>

