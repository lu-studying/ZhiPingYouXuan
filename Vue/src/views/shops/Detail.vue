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
            <div class="card-header-right">
              <span class="shop-id">商家ID：{{ shop.id }}</span>
              <el-tag :type="shop.status === 1 ? 'success' : 'danger'" size="large">
                {{ shop.status === 1 ? '正常' : '下线' }}
              </el-tag>
            </div>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="商家标签">
            <div v-if="shopTagsLoading" class="tags-loading">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
            <div v-else-if="shopTags && shopTags.length > 0" class="tags-container">
              <el-tag
                v-for="tag in shopTags"
                :key="tag.id"
                type="info"
                size="small"
                style="margin-right: 8px; margin-bottom: 4px;"
              >
                {{ tag.name }}
              </el-tag>
            </div>
            <span v-else class="text-placeholder">暂无标签</span>
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

      <!-- 推荐菜管理卡片 -->
      <el-card v-if="shop" class="menus-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>推荐菜管理</span>
            <el-button type="primary" size="small" @click="handleAddMenu">
              <el-icon><Plus /></el-icon>
              新增推荐菜
            </el-button>
          </div>
        </template>

        <!-- 推荐菜列表 -->
        <div v-loading="menusLoading" class="menus-list">
          <el-empty v-if="!menusLoading && menus.length === 0" description="暂无推荐菜，点击上方按钮添加" />
          <div v-else class="menus-grid">
            <div v-for="menu in menus" :key="menu.id" class="menu-item">
              <div class="menu-image-wrapper">
                <img
                  v-if="menu.image"
                  :src="getImageUrl(menu.image)"
                  class="menu-image"
                  @error="handleImageError"
                />
                <div v-else class="menu-image-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
                <el-tag v-if="menu.isRecommended" type="danger" class="recommended-badge">推荐</el-tag>
              </div>
              <div class="menu-info">
                <div class="menu-name">{{ menu.name }}</div>
                <div class="menu-desc">{{ menu.description || '暂无描述' }}</div>
                <div class="menu-footer">
                  <span class="menu-price">¥{{ menu.price?.toFixed(2) || '0.00' }}</span>
                  <div class="menu-actions">
                    <el-button type="primary" text size="small" @click="handleEditMenu(menu)">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                    <el-button type="danger" text size="small" @click="handleDeleteMenu(menu)">
                      <el-icon><Delete /></el-icon>
                      删除
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 错误提示 -->
      <el-empty v-else-if="!loading && !shop" description="商家不存在或已删除" />
    </el-card>

    <!-- 新增/编辑推荐菜弹窗 -->
    <el-dialog
      v-model="menuDialogVisible"
      :title="editingMenu ? '编辑推荐菜' : '新增推荐菜'"
      width="600px"
      @close="handleCloseMenuDialog"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="100px"
      >
        <el-form-item label="菜品名称" prop="name">
          <el-input
            v-model="menuForm.name"
            placeholder="请输入菜品名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="菜品描述" prop="description">
          <el-input
            v-model="menuForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入菜品描述"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="menuForm.price"
            :min="0"
            :precision="2"
            :step="1"
            placeholder="请输入价格"
            style="width: 100%"
          >
            <template #prefix>¥</template>
          </el-input-number>
        </el-form-item>

        <el-form-item label="菜品图片">
          <div class="image-upload-wrapper">
            <el-upload
              :action="uploadAction"
              :headers="uploadHeaders"
              :data="uploadData"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleUploadSuccess"
              :on-error="handleUploadError"
            >
              <img v-if="menuForm.image" :src="getImageUrl(menuForm.image)" class="uploaded-image" />
              <el-button v-else type="primary">
                <el-icon><Upload /></el-icon>
                上传图片
              </el-button>
            </el-upload>
            <div class="upload-tip">支持 JPG、PNG 格式，建议尺寸 400x400px</div>
          </div>
        </el-form-item>

        <el-form-item label="是否推荐">
          <el-switch
            v-model="menuForm.isRecommended"
            :active-value="1"
            :inactive-value="0"
          />
        </el-form-item>

        <el-form-item label="排序顺序">
          <el-input-number
            v-model="menuForm.sortOrder"
            :min="0"
            :step="1"
            placeholder="数字越小越靠前"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCloseMenuDialog">取消</el-button>
        <el-button type="primary" :loading="menuSubmitting" @click="handleSubmitMenu">
          保存
        </el-button>
      </template>
    </el-dialog>
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

import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, Plus, Edit, Delete, Picture, Upload } from '@element-plus/icons-vue'
import { getShop } from '@/api/shops'
import { getShopTags } from '@/api/tags'
import { getMenusByShopId, createMenu, updateMenu, deleteMenu } from '@/api/menus'

// 使用 Vue Router
const router = useRouter()
const route = useRoute()

// 加载状态
const loading = ref(false)

// 商家详情数据
const shop = ref(null)

// 商家标签数据
const shopTags = ref([])
const shopTagsLoading = ref(false)

// 推荐菜数据
const menus = ref([])
const menusLoading = ref(false)
const menuDialogVisible = ref(false)
const menuSubmitting = ref(false)
const editingMenu = ref(null)
const menuFormRef = ref(null)

// 推荐菜表单数据
const menuForm = ref({
  name: '',
  description: '',
  price: null,
  image: '',
  isRecommended: 1,
  sortOrder: 0
})

// 推荐菜表单验证规则
const menuRules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' },
    { max: 50, message: '菜品名称不能超过50个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入价格', trigger: 'blur' },
    { type: 'number', min: 0, message: '价格不能为负数', trigger: 'blur' }
  ]
}

// 图片上传配置
const uploadAction = computed(() => {
  return '/api/files/upload'
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: token ? `Bearer ${token}` : ''
  }
})

const uploadData = computed(() => ({
  pathPrefix: 'menu/'
}))

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
    // 加载商家标签
    loadShopTags(shopId)
    // 加载推荐菜列表
    loadMenus(shopId)
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
 * 加载商家标签
 * 
 * @param {number} shopId - 商家ID
 */
const loadShopTags = async (shopId) => {
  shopTagsLoading.value = true
  try {
    const tags = await getShopTags(shopId)
    shopTags.value = tags || []
  } catch (error) {
    console.error('加载商家标签失败:', error)
    shopTags.value = []
  } finally {
    shopTagsLoading.value = false
  }
}

/**
 * 加载推荐菜列表
 * 
 * @param {number} shopId - 商家ID
 */
const loadMenus = async (shopId) => {
  menusLoading.value = true
  try {
    const menusList = await getMenusByShopId(shopId)
    menus.value = Array.isArray(menusList) ? menusList : []
    // 按排序顺序排序
    menus.value.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0))
  } catch (error) {
    console.error('加载推荐菜列表失败:', error)
    ElMessage.error('加载推荐菜列表失败')
    menus.value = []
  } finally {
    menusLoading.value = false
  }
}

/**
 * 获取图片完整URL
 */
const getImageUrl = (imagePath) => {
  if (!imagePath) return ''
  
  // 如果已经是完整 URL，直接返回
  if (imagePath.startsWith('http')) {
    return imagePath
  }
  

  
  // 其他相对路径，如果是 / 开头，直接返回（可能是其他静态资源）
  if (imagePath.startsWith('/')) {
    return imagePath
  }
  
  // 如果是不带 / 的相对路径，添加 /uploads/ 前缀（兼容旧数据）
  return `/uploads/${imagePath}`
}

/**
 * 处理图片加载错误
 */
const handleImageError = (event) => {
  event.target.style.display = 'none'
}

/**
 * 打开新增推荐菜弹窗
 */
const handleAddMenu = () => {
  editingMenu.value = null
  menuForm.value = {
    name: '',
    description: '',
    price: null,
    image: '',
    isRecommended: 1,
    sortOrder: 0
  }
  menuDialogVisible.value = true
}

/**
 * 打开编辑推荐菜弹窗
 */
const handleEditMenu = (menu) => {
  editingMenu.value = menu
  menuForm.value = {
    name: menu.name || '',
    description: menu.description || '',
    price: menu.price || null,
    image: menu.image || '',
    isRecommended: menu.isRecommended || 0,
    sortOrder: menu.sortOrder || 0
  }
  menuDialogVisible.value = true
}

/**
 * 关闭推荐菜弹窗
 */
const handleCloseMenuDialog = () => {
  menuDialogVisible.value = false
  editingMenu.value = null
  menuFormRef.value?.resetFields()
}

/**
 * 上传前验证
 */
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

/**
 * 上传成功回调
 */
const handleUploadSuccess = (response) => {
  console.log('[Upload] 上传成功响应:', response)
  
  if (response && response.url) {
    // 后端返回的是相对路径，如 /uploads/menu/2024/01/15/xxx.jpg
    menuForm.value.image = response.url
    console.log('[Upload] 保存的图片路径:', menuForm.value.image)
    ElMessage.success('图片上传成功')
  } else if (response && response.relativeUrl) {
    menuForm.value.image = response.relativeUrl
    console.log('[Upload] 保存的图片路径（relativeUrl）:', menuForm.value.image)
    ElMessage.success('图片上传成功')
  } else {
    console.error('[Upload] 响应格式错误:', response)
    ElMessage.error('上传成功但未返回图片路径')
  }
}

/**
 * 上传失败回调
 */
const handleUploadError = () => {
  ElMessage.error('图片上传失败')
}

/**
 * 提交推荐菜表单
 */
const handleSubmitMenu = async () => {
  if (!menuFormRef.value) return

  await menuFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (!shop.value) {
      ElMessage.error('商家信息不存在')
      return
    }

    menuSubmitting.value = true

    try {
      const menuData = {
        shopId: shop.value.id,
        name: menuForm.value.name.trim(),
        description: menuForm.value.description?.trim() || '',
        price: menuForm.value.price,
        image: menuForm.value.image || '',
        isRecommended: menuForm.value.isRecommended,
        sortOrder: menuForm.value.sortOrder || 0
      }

      if (editingMenu.value) {
        // 更新
        await updateMenu(editingMenu.value.id, menuData)
        ElMessage.success('推荐菜更新成功')
      } else {
        // 新增
        await createMenu(menuData)
        ElMessage.success('推荐菜添加成功')
      }

      handleCloseMenuDialog()
      // 重新加载推荐菜列表
      loadMenus(shop.value.id)
    } catch (error) {
      console.error('保存推荐菜失败:', error)
      const errorMessage = error?.response?.data?.message || error?.message || '保存失败'
      ElMessage.error(errorMessage)
    } finally {
      menuSubmitting.value = false
    }
  })
}

/**
 * 删除推荐菜
 */
const handleDeleteMenu = async (menu) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除推荐菜"${menu.name}"吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await deleteMenu(menu.id)
    ElMessage.success('删除成功')
    
    // 重新加载推荐菜列表
    if (shop.value) {
      loadMenus(shop.value.id)
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除推荐菜失败:', error)
      ElMessage.error('删除失败')
    }
  }
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

.card-header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.shop-id {
  font-size: 13px;
  color: #606266;
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

/* 标签容器样式 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.tags-loading {
  display: inline-flex;
  align-items: center;
}

/* 推荐菜管理卡片 */
.menus-card {
  margin-top: 20px;
}

.menus-list {
  min-height: 100px;
}

.menus-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.menu-item {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  background: #fff;
}

.menu-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.menu-image-wrapper {
  position: relative;
  width: 100%;
  height: 200px;
  background: #f5f7fa;
  overflow: hidden;
}

.menu-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.menu-image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 48px;
}

.recommended-badge {
  position: absolute;
  top: 8px;
  right: 8px;
}

.menu-info {
  padding: 16px;
}

.menu-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-desc {
  font-size: 13px;
  color: #909399;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 36px;
}

.menu-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.menu-actions {
  display: flex;
  gap: 8px;
}

/* 图片上传 */
.image-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.uploaded-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
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

