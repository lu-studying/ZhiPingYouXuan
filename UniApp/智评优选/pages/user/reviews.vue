<template>
  <view class="reviews-container">
    <!-- 加载状态 -->
    <Loading v-if="loading" text="加载中..." />
    
    <!-- 点评列表 -->
    <view v-else-if="reviews.length > 0" class="reviews-list">
      <review-card
        v-for="review in reviews"
        :key="review.id"
        :review="review"
        :shop-id="review.shopId"
      />
      
      <!-- 加载更多 -->
      <view v-if="loadingMore" class="loading-more">
        <Loading text="加载更多..." size="small" />
      </view>
      
      <!-- 没有更多 -->
      <view v-if="!hasMore && reviews.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </view>
    
    <!-- 空状态 -->
    <EmptyState
      v-else
      icon="📝"
      text="暂无点评"
      button-text="去写点评"
      @button-click="goToHome"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getMyReviews } from '@/api/users'
import ReviewCard from '@/components/review-card.vue'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'

const reviews = ref([])
const loading = ref(false) // 改为 false，避免阻止首次加载
const loadingMore = ref(false)
const page = ref(0)
const hasMore = ref(true)
const pageSize = 10

onMounted(() => {
  console.log('onMounted: 开始加载点评列表')
  loadReviews(false)
})

/**
 * 加载点评列表
 */
const loadReviews = async (isLoadMore = false) => {
  // 防止重复请求：如果是加载更多，检查是否正在加载
  if (isLoadMore && (loading.value || loadingMore.value)) {
    console.log('loadReviews: 正在加载中，跳过重复请求')
    return
  }
  
  // 如果是首次加载，检查是否正在加载
  if (!isLoadMore && loading.value) {
    console.log('loadReviews: 首次加载中，跳过重复请求')
    return
  }
  
  if (!isLoadMore) {
    page.value = 0
    hasMore.value = true
    loading.value = true
    console.log('loadReviews: 开始首次加载')
  } else {
    if (!hasMore.value) {
      console.log('loadReviews: 没有更多数据')
      return
    }
    loadingMore.value = true
    console.log('loadReviews: 开始加载更多')
  }
  
  try {
    console.log('loadReviews: 调用 getMyReviews API, page=', page.value, 'size=', pageSize)
    const res = await getMyReviews({ 
      page: page.value, 
      size: pageSize 
    })
    
    console.log('loadReviews: API 返回结果:', res)
    
    const newReviews = res.content || []
    const total = res.total || 0
    
    if (isLoadMore) {
      reviews.value = [...reviews.value, ...newReviews]
    } else {
      reviews.value = newReviews
    }
    
    // 判断是否还有更多
    hasMore.value = reviews.value.length < total
    page.value++
    console.log('loadReviews: 加载完成，共', reviews.value.length, '条，总计', total)
  } catch (error) {
    console.error('loadReviews: 加载点评列表失败:', error)
    if (!isLoadMore) {
      reviews.value = []
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
    loadingMore.value = false
    console.log('loadReviews: 清理加载状态')
  }
}

/**
 * 跳转到首页
 */
const goToHome = () => {
  uni.switchTab({ url: '/pages/home/index' })
}

// 下拉刷新
onPullDownRefresh(() => {
  loadReviews(false).finally(() => {
    uni.stopPullDownRefresh()
  })
})

// 上拉加载更多
onReachBottom(() => {
  if (hasMore.value && !loadingMore.value) {
    loadReviews(true)
  }
})
</script>

<style scoped>
.reviews-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.reviews-list {
  display: flex;
  flex-direction: column;
}

.loading-more {
  padding: 40rpx 0;
}

.no-more {
  text-align: center;
  padding: 30rpx 0;
  color: #ccc;
  font-size: 24rpx;
}
</style>

