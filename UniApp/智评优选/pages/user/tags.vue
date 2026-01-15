<template>
  <view class="tags-container">
    <!-- 加载状态 -->
    <Loading v-if="loading" text="加载中..." />
    
    <!-- 标签内容 -->
    <view v-else class="tags-content">
      <view class="header">
        <text class="title">偏好设置</text>
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
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listTags, getMyTags, assignMyTags } from '@/api/tags'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'

const tags = ref([])
const selectedTags = ref([])
const loading = ref(true)
const saving = ref(false)

onMounted(() => {
  loadTags()
})

const loadTags = async () => {
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

const handleSave = async () => {
  if (saving.value) return
  
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
    uni.showToast({ 
      title: error?.message || '保存失败，请稍后重试', 
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

.title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 12rpx;
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
</style>

