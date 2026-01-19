<template>
  <view class="tags-container">
    <!-- 加载状态 -->
    <Loading v-if="loading" text="加载中..." />
    
    <!-- 标签内容 -->
    <view v-else class="tags-content">
      <view class="header">
        <view class="header-row">
          <text class="title">偏好设置</text>
          <button class="btn-add" @click="handleCreateTag">+ 新增标签</button>
        </view>
        <text class="subtitle">选择你喜欢的标签，AI 会为你推荐更符合口味的商家</text>
      </view>
      
      <view v-if="tags.length > 0" class="tags-list">
        <view 
          v-for="tag in tags" 
          :key="tag.id"
          class="tag-item"
          :class="{ active: selectedTags.includes(tag.id) }"
          @click="toggleTag(tag.id)"
        >
          <text class="tag-text">{{ tag.name }}</text>
          <text v-if="selectedTags.includes(tag.id)" class="tag-check">✓</text>
        </view>
      </view>
      
      <EmptyState
        v-else
        icon="🏷️"
        text="暂无标签"
      />
      
      <view class="actions">
        <button 
          class="btn-primary" 
          :class="{ disabled: saving }"
          :disabled="saving"
          @click="handleSave"
        >
          <text v-if="!saving">保存</text>
          <text v-else>保存中...</text>
        </button>
      </view>
    </view>

    <!-- 自定义新增标签大弹窗 -->
    <view v-if="showCreateDialog" class="dialog-mask">
      <view class="dialog-container">
        <view class="dialog-title">新增标签</view>
        <input
          class="dialog-input"
          type="text"
          v-model="newTagName"
          placeholder="例如：微辣 / 适合聚会 / 环境好"
          maxlength="20"
        />
        <view class="dialog-tips">标签名称不超过 12 个字符，建议用简短词语</view>
        <view class="dialog-actions">
          <button class="dialog-btn cancel" @click="cancelCreateTag">取消</button>
          <button class="dialog-btn confirm" @click="confirmCreateTag">确定</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { listTags, getMyTags, assignMyTags, createTag } from '@/api/tags'
import { useAuthStore } from '@/store/auth'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'

const authStore = useAuthStore()
const isLoggedIn = computed(() => authStore.isLoggedIn)
const tags = ref([])
const selectedTags = ref([])
const loading = ref(true)
const saving = ref(false)
const showCreateDialog = ref(false)
const newTagName = ref('')

onMounted(() => {
  loadTags()
})

// 页面重新显示时刷新，避免切换账号后显示旧数据
onShow(() => {
  loadTags()
})

const loadTags = async () => {
  // 未登录引导去登录
  if (!isLoggedIn.value) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/user/login' })
    }, 800)
    loading.value = false
    return
  }

  try {
    loading.value = true
    const [allTags, myTags] = await Promise.all([
      listTags('user'),
      getMyTags()
    ])
    
    tags.value = allTags || []
    selectedTags.value = (myTags || []).map(tag => tag.id)
  } catch (error) {
    console.error('加载标签失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

const toggleTag = (tagId) => {
  const index = selectedTags.value.indexOf(tagId)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tagId)
  }
}

/**
 * 用户创建自己的偏好标签
 */
const handleCreateTag = () => {
  if (!isLoggedIn.value) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/user/login' })
    }, 800)
    return
  }
  newTagName.value = ''
  showCreateDialog.value = true
}

const cancelCreateTag = () => {
  showCreateDialog.value = false
  newTagName.value = ''
}

const confirmCreateTag = async () => {
  const name = (newTagName.value || '').trim()
  if (!name) {
    uni.showToast({ title: '标签名称不能为空', icon: 'none' })
    return
  }
  if (name.length > 12) {
    uni.showToast({ title: '标签名称不能超过12个字符', icon: 'none' })
    return
  }

  // 本地先做一次重名判断（忽略大小写/空格）
  const normalized = name.replace(/\s+/g, '').toLowerCase()
  const exists = (tags.value || []).some(t => (t?.name || '').replace(/\s+/g, '').toLowerCase() === normalized)
  if (exists) {
    uni.showToast({ title: '该标签已存在', icon: 'none' })
    return
  }

  try {
    const created = await createTag({ name, type: 'user' })
    // 刷新标签列表（拿到服务端最终结果）
    await loadTags()
    // 自动选中新建标签
    if (created?.id && !selectedTags.value.includes(created.id)) {
      selectedTags.value.push(created.id)
    } else if (created?.name) {
      const hit = (tags.value || []).find(t => t?.name === created.name)
      if (hit?.id && !selectedTags.value.includes(hit.id)) {
        selectedTags.value.push(hit.id)
      }
    }
    uni.showToast({ title: '创建成功', icon: 'success', duration: 1200 })
    showCreateDialog.value = false
    newTagName.value = ''
  } catch (error) {
    console.error('创建标签失败:', error)
    const msg = error?.data?.error || error?.data?.message || error?.message || '创建失败，请稍后重试'
    uni.showToast({ title: msg, icon: 'none', duration: 2000 })
  }
}

const handleSave = async () => {
  if (saving.value) return
  if (!isLoggedIn.value) {
    uni.showToast({ title: '请先登录', icon: 'none' })
    setTimeout(() => {
      uni.navigateTo({ url: '/pages/user/login' })
    }, 800)
    return
  }
  
  try {
    saving.value = true
    await assignMyTags(selectedTags.value)
    uni.showToast({ 
      title: '保存成功', 
      icon: 'success',
      duration: 1500
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存标签失败:', error)
    const msg = error?.data?.error || error?.data?.message || error?.message || '保存失败，请稍后重试'
    uni.showToast({ 
      title: msg, 
      icon: 'none',
      duration: 2000
    })
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.tags-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.tags-content {
  background-color: #fff;
  padding: 40rpx 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
}

.header {
  margin-bottom: 40rpx;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
}

.btn-add {
  height: 56rpx;
  padding: 0 18rpx;
  border-radius: 28rpx;
  line-height: 56rpx;
  font-size: 24rpx;
  color: #667eea;
  background: rgba(102, 126, 234, 0.12);
  border: 1rpx solid rgba(102, 126, 234, 0.35);
}

.btn-add:active {
  opacity: 0.85;
}

.subtitle {
  display: block;
  font-size: 24rpx;
  color: #999;
  line-height: 1.6;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx 32rpx;
  background-color: #f5f5f5;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
  transition: all 0.3s;
  position: relative;
}

.tag-item:active {
  transform: scale(0.95);
}

.tag-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-color: transparent;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

.tag-text {
  font-weight: 500;
}

.tag-check {
  font-size: 24rpx;
  font-weight: bold;
}

.actions {
  margin-top: 40rpx;
  padding-top: 40rpx;
  border-top: 1rpx solid #f0f0f0;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 12rpx;
  line-height: 88rpx;
  text-align: center;
  border: none;
  font-size: 32rpx;
  font-weight: 500;
  transition: opacity 0.3s;
}

.btn-primary.disabled {
  opacity: 0.6;
}

.btn-primary:active:not(.disabled) {
  opacity: 0.8;
}

/* 自定义弹窗样式 */
.dialog-mask {
  position: fixed;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.dialog-container {
  width: 80%;
  max-width: 640rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 40rpx 32rpx 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.15);
}

.dialog-title {
  text-align: center;
  font-size: 32rpx;
  font-weight: 600;
  margin-bottom: 30rpx;
  color: #333;
}

.dialog-input {
  width: 100%;
  height: 80rpx;
  border-radius: 12rpx;
  border: 1rpx solid #ddd;
  padding: 0 20rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  background-color: #f9f9f9;
}

.dialog-tips {
  margin-top: 16rpx;
  font-size: 22rpx;
  color: #999;
}

.dialog-actions {
  margin-top: 32rpx;
  display: flex;
  justify-content: flex-end;
  gap: 20rpx;
}

.dialog-btn {
  min-width: 140rpx;
  height: 72rpx;
  border-radius: 36rpx;
  line-height: 72rpx;
  font-size: 28rpx;
  padding: 0 24rpx;
}

.dialog-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.dialog-btn.confirm {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.dialog-btn:active {
  opacity: 0.85;
}
</style>

