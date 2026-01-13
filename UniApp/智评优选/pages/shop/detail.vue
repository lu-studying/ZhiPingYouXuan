<template>
  <view class="shop-detail-container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-else-if="shop" class="shop-info">
      <text class="shop-name">{{ shop.name }}</text>
      <text class="shop-category">{{ shop.category }}</text>
      <text class="shop-address">{{ shop.address }}</text>
    </view>
    <view v-else class="error">
      <text>加载失败</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { getShop } from '@/api/shops'

const shop = ref(null)
const loading = ref(true)

onMounted(() => {
  // 从路由参数获取 shopId
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const shopId = currentPage.options?.shopId
  
  if (shopId) {
    loadShopDetail(shopId)
  } else {
    loading.value = false
  }
})

const loadShopDetail = async (shopId) => {
  try {
    loading.value = true
    const data = await getShop(shopId)
    shop.value = data
  } catch (error) {
    console.error('加载商家详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 下拉刷新
onPullDownRefresh(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const shopId = currentPage.options?.shopId
  if (shopId) {
    loadShopDetail(shopId).finally(() => {
      uni.stopPullDownRefresh()
    })
  } else {
    uni.stopPullDownRefresh()
  }
})
</script>

<style scoped>
.shop-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.loading, .error {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.shop-info {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.shop-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.shop-category {
  display: block;
  font-size: 28rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.shop-address {
  display: block;
  font-size: 28rpx;
  color: #666;
}
</style>

