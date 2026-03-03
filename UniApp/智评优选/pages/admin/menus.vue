<template>
  <view class="menus-container">
    <!-- 店铺选择（仅管理员可见） -->
    <view v-if="authStore.isAdmin" class="shop-selector">
      <picker 
        :value="selectedShopIndex" 
        :range="shops" 
        range-key="name"
        @change="handleShopChange"
      >
        <view class="picker">
          <text>{{ selectedShop ? selectedShop.name : '请选择店铺' }}</text>
          <text class="picker-arrow">▼</text>
        </view>
      </picker>
    </view>
    
    <!-- 当前店铺信息 -->
    <view v-if="selectedShop" class="shop-info">
      <text class="shop-name">{{ selectedShop.name }}</text>
      <text class="shop-category">{{ selectedShop.category }}</text>
    </view>
    
    <!-- 操作栏 -->
    <view class="action-bar">
      <button class="btn-add" @click="handleAddMenu">+ 新增菜品</button>
    </view>
    
    <!-- 菜单列表 -->
    <view class="menus-list">
      <view v-if="loading" class="loading-wrapper">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="menus.length === 0" class="empty-wrapper">
        <text>暂无菜单数据</text>
      </view>
      
      <view v-else>
        <view 
          v-for="menu in menus" 
          :key="menu.id" 
          class="menu-item"
          @click="handleEditMenu(menu)"
        >
          <image 
            v-if="menu.image" 
            :src="getImageUrl(menu.image)" 
            class="menu-image"
            mode="aspectFill"
          />
          <view v-else class="menu-image-placeholder">📷</view>
          
          <view class="menu-info">
            <view class="menu-header">
              <text class="menu-name">{{ menu.name }}</text>
              <text v-if="menu.isRecommended" class="recommended-badge">推荐</text>
            </view>
            <text class="menu-desc">{{ menu.description || '暂无描述' }}</text>
            <view class="menu-footer">
              <text class="menu-price">¥{{ menu.price?.toFixed(2) || '0.00' }}</text>
              <text class="menu-status" :class="{ active: menu.status === 1 }">
                {{ menu.status === 1 ? '正常' : '下线' }}
              </text>
            </view>
          </view>
          
          <view class="menu-actions">
            <text class="action-btn edit" @click.stop="handleEditMenu(menu)">编辑</text>
            <text class="action-btn delete" @click.stop="handleDeleteMenu(menu)">删除</text>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 新增/编辑菜单弹窗 -->
    <view v-if="showModal" class="modal-overlay" @click="closeModal">
      <view class="modal-content" @click.stop>
        <view class="modal-header">
          <text class="modal-title">{{ editingMenu ? '编辑菜品' : '新增菜品' }}</text>
          <text class="modal-close" @click="closeModal">×</text>
        </view>
        
        <view class="modal-body">
          <view class="form-item">
            <text class="form-label">菜品名称 *</text>
            <input 
              v-model="formData.name" 
              placeholder="请输入菜品名称"
              class="form-input"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">菜品描述</text>
            <textarea 
              v-model="formData.description" 
              placeholder="请输入菜品描述"
              class="form-textarea"
              maxlength="200"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">价格 *</text>
            <input 
              v-model.number="formData.price" 
              type="digit"
              placeholder="请输入价格"
              class="form-input"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">菜品图片</text>
            <view class="image-upload">
              <image 
                v-if="formData.image" 
                :src="getImageUrl(formData.image)" 
                class="uploaded-image"
                mode="aspectFill"
              />
              <view v-else class="upload-placeholder" @click="handleUploadImage">
                <text>+</text>
                <text>点击上传</text>
              </view>
            </view>
          </view>
          
          <view class="form-item">
            <text class="form-label">是否推荐</text>
            <switch 
              v-model="formData.isRecommended" 
              :checked="formData.isRecommended === 1"
              @change="handleRecommendedChange"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">排序顺序</text>
            <input 
              v-model.number="formData.sortOrder" 
              type="number"
              placeholder="数字越小越靠前"
              class="form-input"
            />
          </view>
        </view>
        
        <view class="modal-footer">
          <button class="btn-cancel" @click="closeModal">取消</button>
          <button class="btn-submit" @click="handleSubmit">保存</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getShopMenus, createMenu, updateMenu, deleteMenu, getMyShops } from '@/api/admin'
import { listShops } from '@/api/shops'
import { uploadFile } from '@/api/files'
import { useAuthStore } from '@/store/auth'
import { BASE_URL } from '@/utils/constants'

const authStore = useAuthStore()

const shops = ref([])
const selectedShopIndex = ref(0)
const selectedShop = ref(null)
const menus = ref([])
const loading = ref(false)
const showModal = ref(false)
const editingMenu = ref(null)

const formData = ref({
  name: '',
  description: '',
  price: null,
  image: '',
  isRecommended: 0,
  sortOrder: 0
})

/**
 * 获取图片完整URL
 */
const getImageUrl = (imagePath) => {
  if (!imagePath) return ''
  if (imagePath.startsWith('http')) {
    return imagePath
  }
  return BASE_URL + imagePath
}

/**
 * 加载店铺列表（仅管理员）
 */
const loadShops = async () => {
  if (!authStore.isAdmin) {
    return
  }
  
  try {
    const res = await listShops({ page: 0, size: 100 })
    shops.value = res.content || []
    if (shops.value.length > 0) {
      selectedShop.value = shops.value[0]
      loadMenus()
    }
  } catch (error) {
    console.error('加载店铺列表失败:', error)
  }
}

/**
 * 加载当前商家店铺（仅商家）
 */
const loadMyShop = async () => {
  try {
    const shopList = await getMyShops()
    if (shopList && shopList.length > 0) {
      selectedShop.value = shopList[0]
      shops.value = shopList
      selectedShopIndex.value = 0
      loadMenus()
    } else {
      uni.showToast({
        title: '您还没有关联的店铺',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('加载店铺失败:', error)
    const message = error?.data?.message || error?.data?.error || '加载失败'
    uni.showToast({
      title: message,
      icon: 'none'
    })
  }
}

/**
 * 加载菜单列表
 */
const loadMenus = async () => {
  if (!selectedShop.value) {
    return
  }
  
  loading.value = true
  
  try {
    const res = await getShopMenus(selectedShop.value.id)
    menus.value = res || []
  } catch (error) {
    console.error('加载菜单列表失败:', error)
    const message = error?.data?.message || error?.data?.error || '加载失败'
    uni.showToast({
      title: message,
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

/**
 * 店铺选择变化
 */
const handleShopChange = (e) => {
  selectedShopIndex.value = e.detail.value
  selectedShop.value = shops.value[e.detail.value]
  loadMenus()
}

/**
 * 打开新增菜单弹窗
 */
const handleAddMenu = () => {
  if (!selectedShop.value) {
    uni.showToast({
      title: '请先选择店铺',
      icon: 'none'
    })
    return
  }
  
  editingMenu.value = null
  formData.value = {
    name: '',
    description: '',
    price: null,
    image: '',
    isRecommended: 0,
    sortOrder: 0
  }
  showModal.value = true
}

/**
 * 打开编辑菜单弹窗
 */
const handleEditMenu = (menu) => {
  editingMenu.value = menu
  formData.value = {
    name: menu.name || '',
    description: menu.description || '',
    price: menu.price || null,
    image: menu.image || '',
    isRecommended: menu.isRecommended || 0,
    sortOrder: menu.sortOrder || 0
  }
  showModal.value = true
}

/**
 * 关闭弹窗
 */
const closeModal = () => {
  showModal.value = false
  editingMenu.value = null
}

/**
 * 推荐开关变化
 */
const handleRecommendedChange = (e) => {
  formData.value.isRecommended = e.detail.value ? 1 : 0
}

/**
 * 上传图片
 */
const handleUploadImage = () => {
  uni.chooseImage({
    count: 1,
    success: async (res) => {
      const tempFilePath = res.tempFilePaths[0]
      
      uni.showLoading({
        title: '上传中...'
      })
      
      try {
        const uploadRes = await uploadFile(tempFilePath)
        formData.value.image = uploadRes.url || uploadRes.path || ''
        uni.hideLoading()
        uni.showToast({
          title: '上传成功',
          icon: 'success'
        })
      } catch (error) {
        uni.hideLoading()
        console.error('上传失败:', error)
        uni.showToast({
          title: '上传失败',
          icon: 'none'
        })
      }
    }
  })
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  // 表单验证
  if (!formData.value.name || !formData.value.name.trim()) {
    uni.showToast({
      title: '请输入菜品名称',
      icon: 'none'
    })
    return
  }
  
  if (!formData.value.price || formData.value.price <= 0) {
    uni.showToast({
      title: '请输入有效的价格',
      icon: 'none'
    })
    return
  }
  
  if (!selectedShop.value) {
    uni.showToast({
      title: '请选择店铺',
      icon: 'none'
    })
    return
  }
  
  uni.showLoading({
    title: '保存中...'
  })
  
  try {
    const menuData = {
      shopId: selectedShop.value.id,
      name: formData.value.name.trim(),
      description: formData.value.description?.trim() || '',
      price: formData.value.price,
      image: formData.value.image || '',
      isRecommended: formData.value.isRecommended,
      sortOrder: formData.value.sortOrder || 0
    }
    
    if (editingMenu.value) {
      // 更新
      await updateMenu(editingMenu.value.id, menuData)
      uni.showToast({
        title: '更新成功',
        icon: 'success'
      })
    } else {
      // 新增
      await createMenu(menuData)
      uni.showToast({
        title: '创建成功',
        icon: 'success'
      })
    }
    
    closeModal()
    loadMenus()
  } catch (error) {
    console.error('保存失败:', error)
    const message = error?.data?.message || error?.data?.error || '保存失败'
    uni.showToast({
      title: message,
      icon: 'none'
    })
  } finally {
    uni.hideLoading()
  }
}

/**
 * 删除菜单
 */
const handleDeleteMenu = (menu) => {
  uni.showModal({
    title: '确认删除',
    content: `确定要删除菜品"${menu.name}"吗？`,
    success: async (res) => {
      if (res.confirm) {
        uni.showLoading({
          title: '删除中...'
        })
        
        try {
          await deleteMenu(menu.id)
          uni.showToast({
            title: '删除成功',
            icon: 'success'
          })
          loadMenus()
        } catch (error) {
          console.error('删除失败:', error)
          const message = error?.data?.message || error?.data?.error || '删除失败'
          uni.showToast({
            title: message,
            icon: 'none'
          })
        } finally {
          uni.hideLoading()
        }
      }
    }
  })
}

onMounted(() => {
  // 检查权限
  if (!authStore.isAdmin && !authStore.isMerchant) {
    uni.showToast({
      title: '无权限访问',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
    return
  }
  
  if (authStore.isAdmin) {
    loadShops()
  } else {
    // 商家账号：加载自己的店铺
    loadMyShop()
  }
})
</script>

<style scoped>
.menus-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.shop-selector {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.picker {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
}

.picker-arrow {
  color: #999;
}

.shop-info {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.shop-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
}

.shop-category {
  display: block;
  font-size: 26rpx;
  color: #999;
}

.action-bar {
  padding: 20rpx 30rpx;
}

.btn-add {
  width: 100%;
  height: 80rpx;
  background: #667eea;
  color: #fff;
  border: none;
  border-radius: 8rpx;
  font-size: 30rpx;
}

.menus-list {
  padding: 20rpx;
}

.loading-wrapper,
.empty-wrapper {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
  font-size: 28rpx;
}

.menu-item {
  background: #fff;
  border-radius: 12rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  gap: 20rpx;
}

.menu-image,
.menu-image-placeholder {
  width: 160rpx;
  height: 160rpx;
  border-radius: 8rpx;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 60rpx;
  flex-shrink: 0;
}

.menu-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.menu-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.menu-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.recommended-badge {
  padding: 4rpx 12rpx;
  background: #ff4757;
  color: #fff;
  border-radius: 4rpx;
  font-size: 20rpx;
}

.menu-desc {
  font-size: 26rpx;
  color: #666;
  margin: 12rpx 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.menu-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.menu-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff4757;
}

.menu-status {
  padding: 6rpx 16rpx;
  border-radius: 6rpx;
  font-size: 24rpx;
  background: #f0f0f0;
  color: #999;
}

.menu-status.active {
  background: #d4edda;
  color: #28a745;
}

.menu-actions {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
  justify-content: center;
}

.action-btn {
  padding: 12rpx 24rpx;
  border-radius: 6rpx;
  font-size: 26rpx;
  text-align: center;
}

.action-btn.edit {
  background: #667eea;
  color: #fff;
}

.action-btn.delete {
  background: #ff4757;
  color: #fff;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  width: 90%;
  max-width: 600rpx;
  max-height: 80vh;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 30rpx;
  border-bottom: 2rpx solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.modal-close {
  font-size: 48rpx;
  color: #999;
  line-height: 1;
}

.modal-body {
  flex: 1;
  padding: 30rpx;
  overflow-y: auto;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 12rpx;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 20rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 8rpx;
  font-size: 28rpx;
  box-sizing: border-box;
}

.form-textarea {
  min-height: 120rpx;
}

.image-upload {
  margin-top: 12rpx;
}

.uploaded-image,
.upload-placeholder {
  width: 200rpx;
  height: 200rpx;
  border-radius: 8rpx;
  background: #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #999;
}

.modal-footer {
  padding: 30rpx;
  border-top: 2rpx solid #f0f0f0;
  display: flex;
  gap: 20rpx;
}

.btn-cancel,
.btn-submit {
  flex: 1;
  height: 80rpx;
  border: none;
  border-radius: 8rpx;
  font-size: 30rpx;
}

.btn-cancel {
  background: #f0f0f0;
  color: #666;
}

.btn-submit {
  background: #667eea;
  color: #fff;
}
</style>

