<template>
  <!-- 用户详情页面容器 -->
  <div class="user-detail-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-with-back">
          <el-button text @click="handleBack" class="back-button" title="返回用户列表">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div>
            <h1 class="page-title">用户详情</h1>
            <p class="page-subtitle">查看用户完整信息</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="10" animated />
    </div>

    <!-- 用户信息卡片 -->
    <el-card v-else-if="user" class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>基本信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="user.status === 1 ? 'success' : 'danger'" size="small">
            {{ user.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="手机号">
          {{ user.mobile || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ user.email || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="昵称">
          {{ user.nickname || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-image
            v-if="user.avatar"
            :src="user.avatar"
            style="width: 80px; height: 80px; border-radius: 4px"
            fit="cover"
            :preview-src-list="[user.avatar]"
          />
          <span v-else class="no-avatar">未设置</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(user.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ formatDateTime(user.updatedAt) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 用户点评列表卡片 -->
    <el-card v-if="user" class="reviews-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>用户点评（{{ reviewPagination.total }} 条）</span>
          <el-button type="primary" text @click="loadUserReviews" :loading="reviewsLoading">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      <el-table
        :data="reviewList"
        v-loading="reviewsLoading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="id" label="点评ID" width="100" sortable />
        <el-table-column label="商家" min-width="150">
          <template #default="{ row }">
            <el-link
              v-if="row.shopId"
              type="primary"
              :underline="false"
              @click="handleViewShop(row.shopId)"
            >
              商家ID: {{ row.shopId }}
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="150" sortable prop="rating">
          <template #default="{ row }">
            <div class="rating-cell">
              <el-rate
                :model-value="row.rating || 0"
                disabled
                :size="16"
                show-score
                text-color="#ff9900"
                :score-template="`${row.rating || 0}`"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="点评内容" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="content-cell">
              {{ row.content || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="点赞数" width="100" sortable prop="likeCount">
          <template #default="{ row }">
            <div class="like-count-cell">
              <span class="like-number">{{ row.likeCount || 0 }}</span>
              <span class="like-icon">❤️</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="AI生成" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isAiGenerated ? 'success' : 'info'" size="small">
              {{ row.isAiGenerated ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" sortable prop="createdAt">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="reviewPagination.page"
          v-model:page-size="reviewPagination.size"
          :page-sizes="[10, 20, 50]"
          :total="reviewPagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleReviewSizeChange"
          @current-change="handleReviewPageChange"
        />
      </div>
    </el-card>

    <!-- 用户不存在提示 -->
    <el-empty v-else description="用户不存在" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Refresh
} from '@element-plus/icons-vue'
import { getUser, getUserReviews } from '@/api/users'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const user = ref(null)
const reviewList = ref([])
const reviewsLoading = ref(false)

// 点评分页信息
const reviewPagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

/**
 * 加载用户详情
 */
const loadUserDetail = async () => {
  const userId = route.params.id
  if (!userId) {
    ElMessage.error('用户ID不能为空')
    router.push('/users')
    return
  }

  loading.value = true
  try {
    const userDetail = await getUser(userId)
    user.value = userDetail
    // 加载用户详情后，自动加载第一页点评
    loadUserReviews()
  } catch (error) {
    console.error('加载用户详情失败:', error)
    ElMessage.error('加载用户详情失败')
    user.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 加载用户点评列表
 */
const loadUserReviews = async () => {
  if (!user.value) {
    return
  }

  reviewsLoading.value = true
  try {
    const response = await getUserReviews(user.value.id, {
      page: reviewPagination.page - 1, // 后端从0开始，前端从1开始
      size: reviewPagination.size
    })

    reviewList.value = response.content || []
    reviewPagination.total = response.total || 0
  } catch (error) {
    console.error('加载用户点评列表失败:', error)
    ElMessage.error('加载用户点评列表失败')
    reviewList.value = []
    reviewPagination.total = 0
  } finally {
    reviewsLoading.value = false
  }
}

/**
 * 处理返回
 */
const handleBack = () => {
  router.push('/users')
}

/**
 * 处理查看商家
 */
const handleViewShop = (shopId) => {
  router.push(`/shops/${shopId}`)
}

/**
 * 处理点评分页大小变化
 */
const handleReviewSizeChange = (size) => {
  reviewPagination.size = size
  reviewPagination.page = 1
  loadUserReviews()
}

/**
 * 处理点评页码变化
 */
const handleReviewPageChange = (page) => {
  reviewPagination.page = page
  loadUserReviews()
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

// 组件挂载时加载数据
onMounted(() => {
  loadUserDetail()
})
</script>

<style scoped>
/* 页面容器 */
.user-detail-container {
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

/* 标题和返回按钮组合 */
.title-with-back {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.back-button {
  margin-top: 4px;
  padding: 8px;
  font-size: 18px;
  color: #606266;
  transition: color 0.3s;
}

.back-button:hover {
  color: #409eff;
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

/* 加载状态 */
.loading-container {
  margin-bottom: 20px;
}

/* 信息卡片 */
.info-card,
.reviews-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 无头像样式 */
.no-avatar {
  color: #909399;
  font-size: 14px;
}

/* 评分单元格 */
.rating-cell {
  display: flex;
  align-items: center;
  min-width: 140px;
}

.rating-cell :deep(.el-rate) {
  display: flex;
  align-items: center;
  flex-wrap: nowrap;
}

.rating-cell :deep(.el-rate__item) {
  margin-right: 2px;
}

.rating-cell :deep(.el-rate__text) {
  margin-left: 8px;
  font-size: 14px;
  white-space: nowrap;
}

/* 内容单元格 */
.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 点赞数单元格 */
.like-count-cell {
  display: flex;
  align-items: center;
  gap: 4px;
}

.like-number {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.like-icon {
  font-size: 16px;
  line-height: 1;
  display: inline-flex;
  align-items: center;
}

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-detail-container {
    padding: 16px;
  }
}
</style>

