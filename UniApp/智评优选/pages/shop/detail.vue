<template>
  <view class="shop-detail-container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-else-if="shop" class="content">
      <!-- 商家信息 -->
      <view class="shop-info-section">
        <text class="shop-name">{{ shop.name }}</text>
        <text class="shop-category">{{ shop.category }}</text>
        <text class="shop-address">{{ shop.address || '地址未知' }}</text>
        <view class="rating-section">
          <rating-stars :rating="shop.avgScore || 0" :show-score="true" />
        </view>
        <view class="price-section">
          <text class="price-text" v-if="shop.avgPrice !== null && shop.avgPrice !== undefined">
            {{ formatAvgPrice(shop.avgPrice) }}
          </text>
          <text class="price-text" v-else style="color: #999;">价格未知</text>
        </view>
      </view>
      
      <!-- AI 推荐点评区域 -->
      <view v-if="recommendedReviews.length > 0" class="ai-recommend-section">
        <view class="section-header">
          <text class="section-title">✨ AI 为你推荐</text>
        </view>
        <view 
          v-for="item in recommendedReviews" 
          :key="item.review.id"
          class="review-item"
        >
          <review-card 
            :review="item.review" 
            :shop-id="shopId"
            :show-recommend-reason="true"
            :recommend-reason="item.reason"
          />
        </view>
      </view>
      
      <!-- 全部点评列表 -->
      <view class="reviews-section">
        <view class="section-header">
          <text class="section-title">全部点评</text>
          <text class="review-count" v-if="reviewsTotal > 0">({{ reviewsTotal }})</text>
        </view>
        <view v-if="reviews.length > 0">
          <view 
            v-for="review in reviews" 
            :key="review.id"
            class="review-item"
          >
            <review-card 
              :review="review" 
              :shop-id="shopId"
            />
          </view>
        </view>
        <view v-else-if="!reviewsLoading" class="empty-state">
          <text>暂无点评</text>
        </view>
        <view v-if="reviewsLoading" class="loading-more">
          <text>加载中...</text>
        </view>
      </view>
    </view>
    <view v-else class="error">
      <text>加载失败</text>
    </view>
    
    <!-- 创建点评入口按钮 -->
    <view class="create-review-btn" @click="goToCreateReview">
      <text class="btn-text">✍️ 写点评</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import { getShop } from '@/api/shops'
import { recommendReviews, listReviews } from '@/api/reviews'
import { formatAvgPrice } from '@/utils/format'
import RatingStars from '@/components/rating-stars.vue'
import ReviewCard from '@/components/review-card.vue'
import { useAuthStore } from '@/store/auth'

const shop = ref(null)
const loading = ref(true)
const shopId = ref(null)

const recommendedReviews = ref([])
const reviews = ref([])
const reviewsLoading = ref(false)
const reviewsTotal = ref(0)
const reviewsPage = ref(0)
const reviewsHasMore = ref(true)

const authStore = useAuthStore()

onMounted(() => {
  // 从路由参数获取 shopId
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  shopId.value = currentPage.options?.shopId
  
  if (shopId.value) {
    loadShopDetail(shopId.value)
    loadRecommendedReviews()
    loadReviews()
  } else {
    loading.value = false
  }
})

/**
 * 加载商家详情
 */
const loadShopDetail = async (id) => {
  try {
    loading.value = true
    const data = await getShop(id)
    console.log('商家详情数据:', data)
    shop.value = data
  } catch (error) {
    console.error('加载商家详情失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 加载 AI 推荐点评
 */
const loadRecommendedReviews = async () => {
  if (!shopId.value) return
  
  try {
    const data = await recommendReviews(shopId.value, { limit: 3 })
    console.log('AI 推荐点评数据:', data)
    recommendedReviews.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载推荐点评失败:', error)
    recommendedReviews.value = []
  }
}

/**
 * 加载点评列表
 */
const loadReviews = async (isLoadMore = false) => {
  if (!shopId.value || reviewsLoading.value) return
  
  if (!isLoadMore) {
    reviewsPage.value = 0
    reviewsHasMore.value = true
  }
  
  if (!reviewsHasMore.value) return
  
  try {
    reviewsLoading.value = true
    const res = await listReviews(shopId.value, {
      page: reviewsPage.value,
      size: 10
    })
    
    console.log('点评列表数据:', res)
    const newReviews = res.content || []
    reviewsTotal.value = res.total || 0
    
    if (isLoadMore) {
      reviews.value = [...reviews.value, ...newReviews]
    } else {
      reviews.value = newReviews
    }
    
    // 判断是否还有更多
    reviewsHasMore.value = reviews.value.length < reviewsTotal.value
    reviewsPage.value++
  } catch (error) {
    console.error('加载点评列表失败:', error)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    reviewsLoading.value = false
  }
}

/**
 * 跳转到创建点评页
 */
const goToCreateReview = () => {
  if (!authStore.isLoggedIn) {
    uni.showModal({
      title: '提示',
      content: '请先登录',
      success: (res) => {
        if (res.confirm) {
          uni.navigateTo({
            url: '/pages/user/login?redirect=/pages/shop/detail&shopId=' + shopId.value
          })
        }
      }
    })
    return
  }
  
  uni.navigateTo({
    url: `/pages/review/create?shopId=${shopId.value}`
  })
}

// 下拉刷新
onPullDownRefresh(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.shopId
  
  if (id) {
    Promise.all([
      loadShopDetail(id),
      loadRecommendedReviews(),
      loadReviews(false)
    ]).finally(() => {
      uni.stopPullDownRefresh()
    })
  } else {
    uni.stopPullDownRefresh()
  }
})

// 上拉加载更多
onReachBottom(() => {
  if (reviewsHasMore.value && !reviewsLoading.value) {
    loadReviews(true)
  }
})
</script>

<style scoped>
.shop-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 120rpx; /* 为底部按钮留出空间 */
}

.loading, .error {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

/* 商家信息区域 */
.shop-info-section {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
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
  margin-bottom: 12rpx;
}

.shop-address {
  display: block;
  font-size: 26rpx;
  color: #666;
  margin-bottom: 20rpx;
  line-height: 1.5;
}

.rating-section {
  margin-bottom: 12rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.price-section {
  padding-top: 12rpx;
  border-top: 1rpx solid #f0f0f0;
}

.price-text {
  font-size: 28rpx;
  color: #ff6b35;
  font-weight: 500;
}

/* 区域标题 */
.section-header {
  display: flex;
  align-items: center;
  gap: 12rpx;
  margin-bottom: 20rpx;
  padding: 0 10rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.review-count {
  font-size: 24rpx;
  color: #999;
}

/* AI 推荐区域 */
.ai-recommend-section {
  margin-top: 20rpx;
}

.review-item {
  margin-bottom: 20rpx;
}

/* 全部点评区域 */
.reviews-section {
  margin-top: 20rpx;
}

.empty-state {
  text-align: center;
  padding: 60rpx 0;
  color: #999;
  font-size: 28rpx;
}

.loading-more {
  text-align: center;
  padding: 40rpx 0;
  color: #999;
  font-size: 26rpx;
}

/* 创建点评按钮 */
.create-review-btn {
  position: fixed;
  bottom: 40rpx;
  left: 50%;
  transform: translateX(-50%);
  width: 200rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
  z-index: 100;
}

.create-review-btn:active {
  transform: translateX(-50%) scale(0.95);
}

.btn-text {
  font-size: 28rpx;
  color: #fff;
  font-weight: 500;
}
</style>
