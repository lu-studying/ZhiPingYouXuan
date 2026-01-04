<template>
  <!-- 点评列表页面容器 -->
  <div class="reviews-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-with-back">
          <el-button text @click="handleBackToDashboard" class="back-button" title="返回仪表盘">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div>
            <h1 class="page-title">点评管理</h1>
            <p class="page-subtitle">管理所有商家点评</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <!-- 商家筛选（必选） -->
        <el-form-item label="商家" required>
          <el-select
            v-model="searchForm.shopId"
            placeholder="请选择商家（必选）"
            clearable
            filterable
            style="width: 200px"
            @change="handleShopChange"
          >
            <el-option
              v-for="shop in shopList"
              :key="shop.id"
              :label="shop.name"
              :value="shop.id"
            />
          </el-select>
          <span class="form-tip">请先选择商家以查看点评</span>
        </el-form-item>

        <!-- 评分筛选 -->
        <el-form-item label="评分">
          <el-select
            v-model="searchForm.rating"
            placeholder="请选择评分"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" :value="null" />
            <el-option label="5星" :value="5" />
            <el-option label="4星" :value="4" />
            <el-option label="3星" :value="3" />
            <el-option label="2星" :value="2" />
            <el-option label="1星" :value="1" />
          </el-select>
        </el-form-item>

        <!-- 状态筛选 -->
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="全部" :value="null" />
            <el-option label="正常" :value="1" />
            <el-option label="屏蔽" :value="0" />
          </el-select>
        </el-form-item>

        <!-- 时间范围筛选 -->
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
            @change="handleSearch"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :disabled="!searchForm.shopId">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 提示信息 -->
    <el-alert
      v-if="!searchForm.shopId"
      title="请先选择商家以查看点评"
      type="info"
      :closable="false"
      show-icon
      style="margin-bottom: 20px"
    />

    <!-- 点评列表表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="reviewList"
        v-loading="loading"
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
              {{ getShopName(row.shopId) }}
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="userId" label="用户ID" width="100" />
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
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '屏蔽' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" sortable prop="createdAt">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleViewDetail(row)" title="查看详情">
                <el-icon><View /></el-icon>
                详情
              </el-button>
              <el-button
                v-if="row.shopId"
                type="info"
                text
                size="small"
                @click="handleViewShop(row.shopId)"
                title="查看商家"
              >
                <el-icon><Shop /></el-icon>
                商家
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 点评详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="点评详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="selectedReview" :column="2" border>
        <el-descriptions-item label="点评ID">{{ selectedReview.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedReview.userId }}</el-descriptions-item>
        <el-descriptions-item label="商家">
          <el-link
            v-if="selectedReview.shopId"
            type="primary"
            :underline="false"
            @click="handleViewShop(selectedReview.shopId)"
          >
            {{ getShopName(selectedReview.shopId) }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate
            :model-value="selectedReview.rating || 0"
            disabled
            show-score
            text-color="#ff9900"
            :score-template="`${selectedReview.rating || 0}`"
          />
        </el-descriptions-item>
        <el-descriptions-item label="点赞数">
          <div class="like-count-cell">
            <span class="like-number">{{ selectedReview.likeCount || 0 }}</span>
            <span class="like-icon">❤️</span>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="AI生成">
          <el-tag :type="selectedReview.isAiGenerated ? 'success' : 'info'" size="small">
            {{ selectedReview.isAiGenerated ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedReview.status === 1 ? 'success' : 'danger'" size="small">
            {{ selectedReview.status === 1 ? '正常' : '屏蔽' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(selectedReview.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="点评内容" :span="2">
          <div class="review-content-detail">
            {{ selectedReview.content || '-' }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item v-if="selectedReview.images" label="图片" :span="2">
          <div class="review-images">
            <el-image
              v-for="(img, index) in parseImages(selectedReview.images)"
              :key="index"
              :src="img"
              :preview-src-list="parseImages(selectedReview.images)"
              :initial-index="index"
              fit="cover"
              style="width: 100px; height: 100px; margin-right: 8px; margin-bottom: 8px"
            />
          </div>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button
          v-if="selectedReview && selectedReview.shopId"
          type="primary"
          @click="handleViewShop(selectedReview.shopId)"
        >
          查看商家
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Refresh,
  Search,
  RefreshLeft,
  View,
  Shop
} from '@element-plus/icons-vue'
import { listReviews } from '@/api/reviews'
import { listShops } from '@/api/shops'
import dayjs from 'dayjs'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const reviewList = ref([])
const shopList = ref([])
const detailDialogVisible = ref(false)
const selectedReview = ref(null)
const currentShopId = ref(null) // 记录当前加载的商家ID

// 搜索表单
const searchForm = reactive({
  shopId: null,
  rating: null,
  status: null,
  dateRange: null
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 存储所有点评数据（未筛选、未分页）
const allReviewsData = ref([])

/**
 * 获取商家名称
 */
const getShopName = (shopId) => {
  const shop = shopList.value.find(s => s.id === shopId)
  return shop ? shop.name : `商家ID: ${shopId}`
}

/**
 * 加载商家列表（用于筛选）
 */
const loadShopList = async () => {
  try {
    const response = await listShops({ page: 0, size: 1000 }) // 获取所有商家用于筛选
    shopList.value = response.content || []
  } catch (error) {
    console.error('加载商家列表失败:', error)
  }
}

/**
 * 处理商家选择变化
 */
const handleShopChange = () => {
  pagination.page = 1
  if (searchForm.shopId) {
    loadReviews()
  } else {
    reviewList.value = []
    allReviewsData.value = []
    pagination.total = 0
  }
}

/**
 * 加载点评列表
 */
const loadReviews = async () => {
  if (!searchForm.shopId) {
    ElMessage.warning('请先选择商家')
    return
  }

  loading.value = true
  try {
    // 由于后端接口是按商家分页查询，我们获取足够多的数据用于前端筛选
    // 如果数据量很大，可以考虑实现真正的后端分页
    const response = await listReviews(searchForm.shopId, {
      page: 0,
      size: 1000 // 获取足够多的数据，前端进行筛选和分页
    })

    // 保存所有点评数据
    allReviewsData.value = response.content || []
    currentShopId.value = searchForm.shopId

    // 应用筛选和分页
    applyFiltersAndPagination()
  } catch (error) {
    console.error('加载点评列表失败:', error)
    ElMessage.error('加载点评列表失败')
    allReviewsData.value = []
    reviewList.value = []
    pagination.total = 0
    currentShopId.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 应用筛选条件和分页
 */
const applyFiltersAndPagination = () => {
  let filtered = [...allReviewsData.value]

  // 评分筛选
  if (searchForm.rating !== null && searchForm.rating !== undefined) {
    filtered = filtered.filter(review => review.rating === searchForm.rating)
  }

  // 状态筛选
  if (searchForm.status !== null && searchForm.status !== undefined) {
    filtered = filtered.filter(review => review.status === searchForm.status)
  }

  // 时间范围筛选
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [startDate, endDate] = searchForm.dateRange
    filtered = filtered.filter(review => {
      if (!review.createdAt) return false
      const createDate = dayjs(review.createdAt).format('YYYY-MM-DD')
      return createDate >= startDate && createDate <= endDate
    })
  }

  // 更新总数
  pagination.total = filtered.length

  // 应用分页
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  reviewList.value = filtered.slice(start, end)
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.page = 1
  
  // 如果商家ID改变了，需要重新加载数据
  if (currentShopId.value !== searchForm.shopId) {
    if (searchForm.shopId) {
      loadReviews()
    }
  } else {
    // 否则只需要重新应用筛选和分页
    if (allReviewsData.value.length > 0) {
      applyFiltersAndPagination()
    }
  }
}

/**
 * 处理重置
 */
const handleReset = () => {
  searchForm.rating = null
  searchForm.status = null
  searchForm.dateRange = null
  pagination.page = 1
  if (allReviewsData.value.length > 0) {
    applyFiltersAndPagination()
  }
}

/**
 * 处理刷新
 */
const handleRefresh = () => {
  if (searchForm.shopId) {
    loadReviews()
  } else {
    ElMessage.warning('请先选择商家')
  }
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  if (allReviewsData.value.length > 0) {
    applyFiltersAndPagination()
  }
}

/**
 * 处理页码变化
 */
const handlePageChange = (page) => {
  pagination.page = page
  if (allReviewsData.value.length > 0) {
    applyFiltersAndPagination()
  }
}

/**
 * 处理查看详情
 */
const handleViewDetail = (review) => {
  selectedReview.value = review
  detailDialogVisible.value = true
}

/**
 * 处理查看商家
 */
const handleViewShop = (shopId) => {
  router.push(`/shops/${shopId}`)
}

/**
 * 处理返回仪表盘
 */
const handleBackToDashboard = () => {
  router.push('/dashboard')
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 解析图片JSON
 */
const parseImages = (images) => {
  if (!images) return []
  try {
    const parsed = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(parsed) ? parsed : []
  } catch (error) {
    return []
  }
}

// 组件挂载时加载商家列表
onMounted(() => {
  loadShopList()
})
</script>

<style scoped>
/* 页面容器 */
.reviews-list-container {
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

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 20px;
}

/* 内容单元格 */
.content-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
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

/* 点评内容详情 */
.review-content-detail {
  max-height: 200px;
  overflow-y: auto;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* 点评图片 */
.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 4px;
  flex-wrap: nowrap;
}

.action-buttons .el-button {
  padding: 8px 10px;
}

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-direction: column;
  }

  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 12px;
  }
}
</style>
