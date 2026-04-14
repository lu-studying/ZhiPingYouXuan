<template>
  <view class="reviews-container">
    <Loading v-if="loading" text="加载中..." />

    <view v-else-if="reviews.length > 0" class="reviews-list">
      <view
        v-for="review in reviews"
        :key="review.id"
        class="clickable-wrap"
        @click="goToReview(review)"
      >
        <review-card
          :review="review"
          :shop-id="review.shopId"
          :show-shop-brief="true"
        />
      </view>

      <view v-if="loadingMore" class="loading-more">
        <Loading text="加载更多..." size="small" />
      </view>

      <view v-if="!hasMore && reviews.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </view>

    <EmptyState
      v-else
      icon="👍"
      text="暂无点赞点评"
      button-text="去逛逛"
      @button-click="goToHome"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getMyLikedReviews } from '@/api/users'
import ReviewCard from '@/components/review-card.vue'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'

const reviews = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const page = ref(0)
const hasMore = ref(true)
const pageSize = 10

onMounted(() => {
  loadReviews(false)
})

const loadReviews = async (isLoadMore = false) => {
  if (isLoadMore && (loading.value || loadingMore.value)) return
  if (!isLoadMore && loading.value) return

  if (!isLoadMore) {
    page.value = 0
    hasMore.value = true
    loading.value = true
  } else {
    if (!hasMore.value) return
    loadingMore.value = true
  }

  try {
    const res = await getMyLikedReviews({
      page: page.value,
      size: pageSize
    })
    const newReviews = res.content || []
    const total = res.total || 0

    if (isLoadMore) {
      reviews.value = [...reviews.value, ...newReviews]
    } else {
      reviews.value = newReviews
    }

    hasMore.value = reviews.value.length < total
    page.value++
  } catch (error) {
    if (!isLoadMore) {
      reviews.value = []
    }
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const goToHome = () => {
  uni.switchTab({ url: '/pages/home/index' })
}

const goToReview = (review) => {
  if (!review?.shopId || !review?.id) return
  uni.navigateTo({
    url: `/pages/shop/detail?shopId=${review.shopId}`
  })
}

onPullDownRefresh(() => {
  loadReviews(false).finally(() => {
    uni.stopPullDownRefresh()
  })
})

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

.clickable-wrap:active {
  opacity: 0.92;
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
