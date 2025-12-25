<template>
  <!-- 商家详情页面容器 -->
  <div class="shop-detail-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">商家详情</h1>
        <p class="page-subtitle">查看商家完整信息</p>
      </div>
      <div class="header-right">
        <el-button @click="handleEdit">编辑</el-button>
        <el-button @click="handleBack">返回</el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <el-card v-loading="loading" shadow="never">
      <!-- 商家信息卡片 -->
      <el-card v-if="shop" class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-tag :type="shop.status === 1 ? 'success' : 'danger'" size="large">
              {{ shop.status === 1 ? '正常' : '下线' }}
            </el-tag>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="商家ID">
            {{ shop.id }}
          </el-descriptions-item>
          <el-descriptions-item label="商家名称">
            <span class="shop-name">{{ shop.name }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="商家分类">
            <el-tag type="info">{{ shop.category || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="平均评分">
            <el-rate
              :model-value="shop.avgScore || 0"
              disabled
              show-score
              text-color="#ff9900"
              :score-template="`${shop.avgScore || 0}`"
            />
          </el-descriptions-item>
          <el-descriptions-item label="人均价格">
            <span v-if="shop.avgPrice">¥{{ shop.avgPrice }}</span>
            <span v-else class="text-placeholder">-</span>
          </el-descriptions-item>
          <el-descriptions-item label="商家地址" :span="2">
            {{ shop.address || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="经度">
            <span v-if="shop.lng">{{ shop.lng }}</span>
            <span v-else class="text-placeholder">-</span>
          </el-descriptions-item>
          <el-descriptions-item label="纬度">
            <span v-if="shop.lat">{{ shop.lat }}</span>
            <span v-else class="text-placeholder">-</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDateTime(shop.createdAt) }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ formatDateTime(shop.updatedAt) }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 错误提示 -->
      <el-empty v-else-if="!loading && !shop" description="商家不存在或已删除" />
    </el-card>
  </div>
</template>

<script setup>
/**
 * 商家详情页面组件
 * 
 * 功能说明：
 * 1. 展示商家完整信息
 * 2. 提供编辑和返回按钮
 * 3. 支持查看商家状态、评分等信息
 */

import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getShop } from '@/api/shops'

// 使用 Vue Router
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 商家详情数据
const shop = ref(null)

/**
 * 加载商家详情
 * 
 * 功能：
 * 1. 从路由参数获取商家ID
 * 2. 调用API获取商家详情
 * 3. 更新商家数据
 */
const loadShop = async () => {
  // 从路由参数获取商家ID
  const shopId = route.params.id

  if (!shopId) {
    ElMessage.error('商家ID不能为空')
    router.push('/shops')
    return
  }

  loading.value = true

  try {
    // 调用API获取商家详情
    const response = await getShop(shopId)
    shop.value = response
  } catch (error) {
    console.error('加载商家详情失败:', error)
    // 如果商家不存在（404），显示错误提示
    if (error.response && error.response.status === 404) {
      ElMessage.error('商家不存在或已删除')
    } else {
      ElMessage.error('加载商家详情失败')
    }
    // 跳转回商家列表
    router.push('/shops')
  } finally {
    loading.value = false
  }
}

/**
 * 处理编辑操作
 * 
 * 跳转到编辑页面
 */
const handleEdit = () => {
  if (shop.value) {
    router.push(`/shops/${shop.value.id}/edit`)
  }
}

/**
 * 处理返回操作
 * 
 * 返回商家列表页
 */
const handleBack = () => {
  router.push('/shops')
}

/**
 * 格式化日期时间
 * 
 * @param {string} dateTime - ISO 日期时间字符串
 * @returns {string} 格式化后的日期时间字符串
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    return dateTime
  }
}

// 组件挂载时加载商家详情
onMounted(() => {
  loadShop()
})
</script>

<style scoped>
/**
 * 商家详情页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 */

/* 页面容器 */
.shop-detail-container {
  padding: 24px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
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

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 信息卡片 */
.info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.shop-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 占位文本样式 */
.text-placeholder {
  color: #c0c4cc;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .shop-detail-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }
}
</style>

