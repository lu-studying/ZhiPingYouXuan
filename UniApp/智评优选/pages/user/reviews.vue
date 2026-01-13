<template>
  <view class="reviews-container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-else-if="reviews.length > 0" class="reviews-list">
      <view 
        v-for="review in reviews" 
        :key="review.id"
        class="review-item"
      >
        <text class="rating">评分：{{ review.rating }}星</text>
        <text class="content">{{ review.content }}</text>
        <text class="time">{{ formatDate(review.createdAt) }}</text>
      </view>
    </view>
    <view v-else class="empty">
      <text>暂无点评</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getMyReviews } from '@/api/users'
import { formatDate } from '@/utils/format'

const reviews = ref([])
const loading = ref(true)

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  try {
    loading.value = true
    const res = await getMyReviews({ page: 0, size: 10 })
    reviews.value = res.content || []
  } catch (error) {
    console.error('加载点评列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 下拉刷新
onPullDownRefresh(() => {
  loadReviews().finally(() => {
    uni.stopPullDownRefresh()
  })
})
</script>

<style scoped>
.reviews-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.loading, .empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.review-item {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.rating {
  display: block;
  font-size: 28rpx;
  color: #ff6600;
  margin-bottom: 10rpx;
}

.content {
  display: block;
  font-size: 28rpx;
  color: #333;
  margin-bottom: 10rpx;
  line-height: 1.6;
}

.time {
  display: block;
  font-size: 24rpx;
  color: #999;
}
</style>

