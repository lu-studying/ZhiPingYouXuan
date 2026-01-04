<template>
  <!-- 订单列表页面容器 -->
  <div class="orders-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-with-back">
          <el-button text @click="handleBackToDashboard" class="back-button" title="返回仪表盘">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div>
            <h1 class="page-title">订单管理</h1>
            <p class="page-subtitle">管理所有订单/消费记录</p>
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
        <!-- 商家筛选 -->
        <el-form-item label="商家">
          <el-select
            v-model="searchForm.shopId"
            placeholder="请选择商家"
            clearable
            filterable
            style="width: 200px"
            @change="handleSearch"
          >
            <el-option label="全部商家" :value="null" />
            <el-option
              v-for="shop in shopList"
              :key="shop.id"
              :label="shop.name"
              :value="shop.id"
            />
          </el-select>
        </el-form-item>

        <!-- 时间范围筛选 -->
        <el-form-item label="到店时间">
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

        <!-- 金额范围筛选 -->
        <el-form-item label="消费金额">
          <el-input-number
            v-model="searchForm.minAmount"
            :precision="2"
            :step="10"
            :min="0"
            placeholder="最低金额"
            style="width: 120px"
          />
          <span style="margin: 0 8px">-</span>
          <el-input-number
            v-model="searchForm.maxAmount"
            :precision="2"
            :step="10"
            :min="0"
            placeholder="最高金额"
            style="width: 120px"
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
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

    <!-- 订单列表表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="orderList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="id" label="订单ID" width="100" sortable />
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
        <el-table-column label="消费金额" width="120" sortable>
          <template #default="{ row }">
            <span class="amount-text">¥ {{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="到店时间" width="180" sortable prop="visitTime">
          <template #default="{ row }">
            {{ formatDateTime(row.visitTime) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" sortable prop="createdAt">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="消费明细" min-width="200">
          <template #default="{ row }">
            <el-popover
              v-if="row.items"
              placement="top"
              :width="300"
              trigger="hover"
            >
              <template #reference>
                <el-button text type="primary" size="small">查看明细</el-button>
              </template>
              <div class="items-detail">
                <pre>{{ formatItems(row.items) }}</pre>
              </div>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="handleViewDetail(row)" title="查看详情">
              <el-icon><View /></el-icon>
              详情
            </el-button>
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

    <!-- 订单详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="订单详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="selectedOrder" :column="2" border>
        <el-descriptions-item label="订单ID">{{ selectedOrder.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ selectedOrder.userId }}</el-descriptions-item>
        <el-descriptions-item label="商家">
          <el-link
            v-if="selectedOrder.shopId"
            type="primary"
            :underline="false"
            @click="handleViewShop(selectedOrder.shopId)"
          >
            {{ getShopName(selectedOrder.shopId) }}
          </el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="消费金额">
          <span class="amount-text">¥ {{ formatAmount(selectedOrder.amount) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="到店时间">
          {{ formatDateTime(selectedOrder.visitTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(selectedOrder.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="消费明细" :span="2">
          <div v-if="selectedOrder.items" class="items-detail">
            <pre>{{ formatItems(selectedOrder.items) }}</pre>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Refresh,
  Search,
  RefreshLeft,
  View
} from '@element-plus/icons-vue'
import { listMyOrders, listOrdersByShop } from '@/api/orders'
import { listShops } from '@/api/shops'
import dayjs from 'dayjs'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const orderList = ref([])
const shopList = ref([])
const detailDialogVisible = ref(false)
const selectedOrder = ref(null)
const currentShopId = ref(null) // 记录当前加载的商家ID

// 搜索表单
const searchForm = reactive({
  shopId: null,
  dateRange: null,
  minAmount: null,
  maxAmount: null
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

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

// 存储所有订单数据（未筛选、未分页）
const allOrdersData = ref([])

/**
 * 加载订单列表
 */
const loadOrders = async () => {
  loading.value = true
  try {
    let orders = []

    // 如果选择了商家，获取该商家的订单
    if (searchForm.shopId) {
      const response = await listOrdersByShop(searchForm.shopId)
      orders = response.content || []
      currentShopId.value = searchForm.shopId
    } else {
      // 否则获取当前用户的订单
      const response = await listMyOrders()
      orders = Array.isArray(response) ? response : []
      currentShopId.value = null
    }

    // 保存所有订单数据
    allOrdersData.value = orders

    // 应用筛选和分页
    applyFiltersAndPagination()
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
    allOrdersData.value = []
    orderList.value = []
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
  let filtered = [...allOrdersData.value]

  // 时间范围筛选
  if (searchForm.dateRange && searchForm.dateRange.length === 2) {
    const [startDate, endDate] = searchForm.dateRange
    filtered = filtered.filter(order => {
      if (!order.visitTime) return false
      const visitDate = dayjs(order.visitTime).format('YYYY-MM-DD')
      return visitDate >= startDate && visitDate <= endDate
    })
  }

  // 金额范围筛选
  if (searchForm.minAmount !== null && searchForm.minAmount !== undefined) {
    filtered = filtered.filter(order => {
      const amount = parseFloat(order.amount) || 0
      return amount >= searchForm.minAmount
    })
  }

  if (searchForm.maxAmount !== null && searchForm.maxAmount !== undefined) {
    filtered = filtered.filter(order => {
      const amount = parseFloat(order.amount) || 0
      return amount <= searchForm.maxAmount
    })
  }

  // 更新总数
  pagination.total = filtered.length

  // 应用分页
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  orderList.value = filtered.slice(start, end)
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.page = 1
  
  // 如果商家ID改变了，需要重新加载数据
  if (currentShopId.value !== searchForm.shopId) {
    loadOrders()
  } else {
    // 否则只需要重新应用筛选和分页
    applyFiltersAndPagination()
  }
}

/**
 * 处理重置
 */
const handleReset = () => {
  searchForm.shopId = null
  searchForm.dateRange = null
  searchForm.minAmount = null
  searchForm.maxAmount = null
  pagination.page = 1
  loadOrders()
}

/**
 * 处理刷新
 */
const handleRefresh = () => {
  loadOrders()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadOrders()
}

/**
 * 处理页码变化
 */
const handlePageChange = (page) => {
  pagination.page = page
  loadOrders()
}

/**
 * 处理查看详情
 */
const handleViewDetail = (order) => {
  selectedOrder.value = order
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
 * 格式化金额
 */
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return parseFloat(amount).toFixed(2)
}

/**
 * 格式化日期时间
 */
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

/**
 * 格式化消费明细
 */
const formatItems = (items) => {
  if (!items) return '-'
  try {
    const parsed = typeof items === 'string' ? JSON.parse(items) : items
    return JSON.stringify(parsed, null, 2)
  } catch (error) {
    return items
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadShopList()
  loadOrders()
})
</script>

<style scoped>
/* 页面容器 */
.orders-list-container {
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

/* 表格卡片 */
.table-card {
  margin-bottom: 20px;
}

/* 金额文本样式 */
.amount-text {
  font-weight: 600;
  color: #f56c6c;
  font-size: 14px;
}

/* 消费明细样式 */
.items-detail {
  max-height: 200px;
  overflow-y: auto;
  background-color: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
}

.items-detail pre {
  margin: 0;
  font-size: 12px;
  line-height: 1.5;
  color: #606266;
  white-space: pre-wrap;
  word-wrap: break-word;
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
