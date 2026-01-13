<template>
  <view class="orders-container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    <view v-else-if="orders.length > 0" class="orders-list">
      <view 
        v-for="order in orders" 
        :key="order.id"
        class="order-item"
      >
        <text class="shop-name">{{ order.shopName || '商家' }}</text>
        <text class="amount">消费金额：¥{{ order.amount }}</text>
        <text class="time">{{ formatDate(order.visitTime) }}</text>
      </view>
    </view>
    <view v-else class="empty">
      <text>暂无订单</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { listMyOrders } from '@/api/orders'
import { formatDate } from '@/utils/format'

const orders = ref([])
const loading = ref(true)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  try {
    loading.value = true
    const data = await listMyOrders()
    orders.value = data || []
  } catch (error) {
    console.error('加载订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 下拉刷新
onPullDownRefresh(() => {
  loadOrders().finally(() => {
    uni.stopPullDownRefresh()
  })
})
</script>

<style scoped>
.orders-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.loading, .empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.order-item {
  background-color: #fff;
  padding: 30rpx;
  border-radius: 10rpx;
}

.shop-name {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.amount {
  display: block;
  font-size: 28rpx;
  color: #ff6600;
  margin-bottom: 10rpx;
}

.time {
  display: block;
  font-size: 24rpx;
  color: #999;
}
</style>

