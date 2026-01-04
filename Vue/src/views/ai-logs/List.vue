<template>
  <!-- AI 调用日志列表页面容器 -->
  <div class="ai-logs-list-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">AI 调用日志</h1>
        <p class="page-subtitle">查看和管理 AI 调用记录</p>
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
        <!-- 调用类型筛选 -->
        <el-form-item label="调用类型">
          <el-select
            v-model="searchForm.type"
            placeholder="请选择调用类型"
            clearable
            style="width: 150px"
            @change="handleSearch"
          >
            <el-option label="全部" :value="null" />
            <el-option label="生成草稿" value="generate" />
            <el-option label="推荐" value="recommend" />
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
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>

        <!-- 用户ID筛选 -->
        <el-form-item label="用户ID">
          <el-input
            v-model.number="searchForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 150px"
            @keyup.enter="handleSearch"
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

    <!-- 日志列表表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="logList"
        v-loading="loading"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'createdAt', order: 'descending' }"
      >
        <el-table-column prop="id" label="日志ID" width="100" sortable />
        <el-table-column prop="userId" label="用户ID" width="100">
          <template #default="{ row }">
            {{ row.userId || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="调用类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === 'generate' ? 'primary' : 'success'" size="small">
              {{ row.type === 'generate' ? '生成草稿' : '推荐' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="latencyMs" label="耗时(ms)" width="120" sortable>
          <template #default="{ row }">
            {{ row.latencyMs || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="Prompt" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="prompt-cell">
              {{ row.prompt || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="响应摘要" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="response-cell">
              {{ row.responseRef || '-' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" sortable prop="createdAt">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
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

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="AI 调用日志详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions v-if="selectedLog" :column="1" border>
        <el-descriptions-item label="日志ID">{{ selectedLog.id }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">
          {{ selectedLog.userId || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="调用类型">
          <el-tag :type="selectedLog.type === 'generate' ? 'primary' : 'success'" size="small">
            {{ selectedLog.type === 'generate' ? '生成草稿' : '推荐' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="selectedLog.status === 1 ? 'success' : 'danger'" size="small">
            {{ selectedLog.status === 1 ? '成功' : '失败' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时(ms)">
          {{ selectedLog.latencyMs || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="Prompt">
          <div class="detail-content">
            {{ selectedLog.prompt || '-' }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="响应摘要">
          <div class="detail-content">
            {{ selectedLog.responseRef || '-' }}
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ formatDateTime(selectedLog.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Search,
  RefreshLeft,
  View
} from '@element-plus/icons-vue'
import { listAiLogs } from '@/api/ai-logs'
import dayjs from 'dayjs'

// 响应式数据
const loading = ref(false)
const logList = ref([])
const detailDialogVisible = ref(false)
const selectedLog = ref(null)

// 搜索表单
const searchForm = reactive({
  type: null,
  status: null,
  userId: null
})

// 分页信息
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

/**
 * 加载日志列表
 */
const loadLogs = async () => {
  loading.value = true
  try {
    const response = await listAiLogs({
      page: pagination.page - 1, // 后端从0开始，前端从1开始
      size: pagination.size,
      type: searchForm.type || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      userId: searchForm.userId || undefined
    })

    logList.value = response.content || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载日志列表失败:', error)
    ElMessage.error('加载日志列表失败')
    logList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

/**
 * 处理搜索
 */
const handleSearch = () => {
  pagination.page = 1
  loadLogs()
}

/**
 * 处理重置
 */
const handleReset = () => {
  searchForm.type = null
  searchForm.status = null
  searchForm.userId = null
  pagination.page = 1
  loadLogs()
}

/**
 * 处理刷新
 */
const handleRefresh = () => {
  loadLogs()
}

/**
 * 处理分页大小变化
 */
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadLogs()
}

/**
 * 处理页码变化
 */
const handlePageChange = (page) => {
  pagination.page = page
  loadLogs()
}

/**
 * 处理查看详情
 */
const handleViewDetail = (log) => {
  selectedLog.value = log
  detailDialogVisible.value = true
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
  loadLogs()
})
</script>

<style scoped>
/* 页面容器 */
.ai-logs-list-container {
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

/* Prompt 和响应内容单元格 */
.prompt-cell,
.response-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 详情对话框内容 */
.detail-content {
  max-height: 200px;
  overflow-y: auto;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  line-height: 1.6;
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

