<template>
  <view class="tags-container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-else class="tags-content">
      <view class="tags-list">
        <view 
          v-for="tag in tags" 
          :key="tag.id"
          class="tag-item"
          :class="{ active: selectedTags.includes(tag.id) }"
          @click="toggleTag(tag.id)"
        >
          <text>{{ tag.name }}</text>
        </view>
      </view>
      
      <view class="actions">
        <button class="btn-primary" @click="handleSave">保存</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listTags, getMyTags, assignMyTags } from '@/api/tags'

const tags = ref([])
const selectedTags = ref([])
const loading = ref(true)

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
  try {
    await assignMyTags(selectedTags.value)
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('保存标签失败:', error)
  }
}
</script>

<style scoped>
.tags-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.loading {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.tags-content {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 40rpx;
}

.tag-item {
  padding: 15rpx 30rpx;
  background-color: #f5f5f5;
  border-radius: 30rpx;
  font-size: 28rpx;
  color: #666;
  border: 2rpx solid transparent;
}

.tag-item.active {
  background-color: #e6f3ff;
  color: #3c9cff;
  border-color: #3c9cff;
}

.actions {
  margin-top: 40rpx;
}

.btn-primary {
  width: 100%;
  height: 80rpx;
  background-color: #3c9cff;
  color: #fff;
  border-radius: 10rpx;
  line-height: 80rpx;
  text-align: center;
  border: none;
}
</style>

