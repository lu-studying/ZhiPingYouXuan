<template>
  <!-- 商家列表页面容器 -->
  <div class="shops-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <div class="title-with-back">
          <el-button text @click="handleBackToDashboard" class="back-button" title="返回仪表盘">
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <div>
            <h1 class="page-title">商家管理</h1>
            <p class="page-subtitle">管理所有商家信息</p>
          </div>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增商家
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" :inline="true" class="search-form">
        <!-- 关键词搜索 -->
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入商家名称或地址"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <!-- 分类筛选 -->
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.category"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" value="" />
            <el-option label="火锅" value="火锅" />
            <el-option label="川菜" value="川菜" />
            <el-option label="日料" value="日料" />
            <el-option label="西餐" value="西餐" />
            <el-option label="咖啡" value="咖啡" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <!-- 状态筛选 -->
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="全部" value="" />
            <el-option label="正常" :value="1" />
            <el-option label="下线" :value="0" />
          </el-select>
        </el-form-item>

        <!-- 评分筛选 -->
        <el-form-item label="最低评分">
          <el-input-number
            v-model="searchForm.minScore"
            :min="0"
            :max="5"
            :precision="1"
            :step="0.5"
            placeholder="最低评分"
            style="width: 120px"
          />
        </el-form-item>

        <!-- 价格筛选 -->
        <el-form-item label="最高价格">
          <el-input-number
            v-model="searchForm.maxPrice"
            :min="0"
            :step="10"
            placeholder="最高价格"
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
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商家列表表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="shops"
        v-loading="loading"
        stripe
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <!-- 商家ID -->
        <el-table-column prop="id" label="ID" width="80" sortable="custom" />

        <!-- 商家名称 -->
        <el-table-column prop="name" label="商家名称" min-width="150" show-overflow-tooltip />

        <!-- 分类 -->
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ row.category || '-' }}</el-tag>
          </template>
        </el-table-column>

        <!-- 地址 -->
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />

        <!-- 平均评分 -->
        <el-table-column prop="avgScore" label="评分" width="150" sortable="custom">
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

        <!-- 人均价格 -->
        <el-table-column prop="avgPrice" label="人均价格" width="120" sortable="custom">
          <template #default="{ row }">
            <span v-if="row.avgPrice">¥{{ row.avgPrice }}</span>
            <span v-else class="text-placeholder">-</span>
          </template>
        </el-table-column>

        <!-- 状态 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '下线' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 创建时间 -->
        <el-table-column prop="createdAt" label="创建时间" width="180" sortable="custom">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="handleView(row.id)" title="查看详情">
                <el-icon><View /></el-icon>
                查看
              </el-button>
              <el-button type="warning" text size="small" @click="handleEdit(row.id)" title="编辑商家">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" text size="small" @click="handleDelete(row)" title="删除商家">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
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
  </div>
</template>

<script setup>
/**
 * 商家列表页面组件
 * 
 * 功能说明：
 * 1. 商家列表展示（表格形式）
 * 2. 支持分页
 * 3. 搜索功能（关键词搜索）
 * 4. 筛选功能（分类、评分、价格、状态）
 * 5. 操作按钮（查看、编辑、删除）
 * 6. 新增商家按钮
 */

import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listShops, deleteShop } from '@/api/shops'

// 使用 Vue Router
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 商家列表数据
const shops = ref([])

// 搜索表单
const searchForm = reactive({
  keyword: '',      // 关键词搜索
  category: '',    // 分类筛选
  status: '',      // 状态筛选（1正常/0下线）
  minScore: null,  // 最低评分
  maxPrice: null   // 最高价格
})

// 分页信息
const pagination = reactive({
  page: 1,         // 当前页码（从1开始，但API需要从0开始）
  size: 10,        // 每页大小
  total: 0         // 总记录数
})

/**
 * 加载商家列表
 * 
 * 功能：
 * 1. 根据搜索条件和分页信息加载商家列表
 * 2. 更新分页信息
 */
const loadShops = async () => {
  loading.value = true

  try {
    // 构建查询参数
    const params = {
      page: pagination.page - 1, // API 页码从0开始，前端从1开始
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      category: searchForm.category || undefined,
      minScore: searchForm.minScore || undefined,
      maxPrice: searchForm.maxPrice || undefined
    }

    // 调用 API 获取商家列表
    const response = await listShops(params)

    // 更新商家列表
    shops.value = response.content || []

    // 更新分页信息
    pagination.total = response.total || 0

    // 如果有状态筛选，在前端过滤（因为后端API不支持status筛选）
    if (searchForm.status !== '') {
      shops.value = shops.value.filter(shop => shop.status === searchForm.status)
    }
  } catch (error) {
    console.error('加载商家列表失败:', error)
    ElMessage.error('加载商家列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 * 
 * 功能：
 * 1. 重置到第一页
 * 2. 重新加载商家列表
 */
const handleSearch = () => {
  pagination.page = 1
  loadShops()
}

/**
 * 处理重置
 * 
 * 功能：
 * 1. 清空所有搜索条件
 * 2. 重置到第一页
 * 3. 重新加载商家列表
 */
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.status = ''
  searchForm.minScore = null
  searchForm.maxPrice = null
  pagination.page = 1
  loadShops()
}

/**
 * 处理分页大小变化
 * 
 * @param {number} size - 新的每页大小
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1 // 重置到第一页
  loadShops()
}

/**
 * 处理页码变化
 * 
 * @param {number} page - 新的页码
 */
const handlePageChange = (page) => {
  pagination.page = page
  loadShops()
}

/**
 * 处理排序变化
 * 
 * @param {Object} sort - 排序信息
 */
const handleSortChange = (sort) => {
  // 注意：后端API目前不支持排序，这里只是示例
  // 如果需要排序，可以在前端对数据进行排序
  console.log('排序变化:', sort)
}

/**
 * 处理查看商家详情
 * 
 * @param {number} id - 商家ID
 */
const handleView = (id) => {
  router.push(`/shops/${id}`)
}

/**
 * 处理编辑商家
 * 
 * @param {number} id - 商家ID
 */
const handleEdit = (id) => {
  router.push(`/shops/${id}/edit`)
}

/**
 * 处理删除商家
 * 
 * @param {Object} shop - 商家对象
 */
const handleDelete = async (shop) => {
  try {
    // 确认删除对话框
    await ElMessageBox.confirm(
      `确定要删除商家"${shop.name}"吗？此操作不可恢复！`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // 调用删除 API
    await deleteShop(shop.id)

    // 显示成功提示
    ElMessage.success('删除成功')

    // 重新加载商家列表
    loadShops()
  } catch (error) {
    // 如果用户取消删除，不显示错误
    if (error !== 'cancel') {
      console.error('删除商家失败:', error)
      ElMessage.error('删除商家失败')
    }
  }
}

/**
 * 处理新增商家
 * 
 * 跳转到新增商家页面
 */
const handleCreate = () => {
  router.push('/shops/create')
}

/**
 * 处理返回仪表盘
 * 
 * 跳转到仪表盘页面
 */
const handleBackToDashboard = () => {
  router.push('/dashboard')
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

// 组件挂载时加载商家列表
onMounted(() => {
  loadShops()
  
  // 如果从创建页面跳转过来，显示成功提示
  if (route.query.created === 'success') {
    ElMessage.success('商家创建成功')
    // 清除 query 参数，避免刷新时重复提示
    router.replace({ path: '/shops' })
  }
})
</script>

<style scoped>
/**
 * 商家列表页面样式
 * 
 * scoped: 样式只作用于当前组件，不会影响其他组件
 */

/* 页面容器 */
.shops-list-container {
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

.header-right {
  display: flex;
  align-items: center;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 20px;
}

/* 分页组件 */
.pagination-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 占位文本样式 */
.text-placeholder {
  color: #c0c4cc;
}

/* 操作按钮组样式 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: nowrap;
}

.action-buttons .el-button {
  padding: 4px 8px;
  margin: 0;
  white-space: nowrap;
}

.action-buttons .el-button .el-icon {
  margin-right: 4px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .shops-list-container {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
  }

  .search-form {
    flex-direction: column;
  }

  .search-form :deep(.el-form-item) {
    width: 100%;
  }
}
</style>
