<template>
  <view class="orders-container">
    <!-- 加载状态 -->
    <Loading v-if="loading" text="加载中..." />
    
    <!-- 订单列表 -->
    <view v-else-if="orders.length > 0" class="orders-list">
      <order-card
        v-for="order in orders"
        :key="order.id"
        :order="order"
        @click="handleOrderClick"
      />
    </view>
    
    <!-- 空状态 -->
    <EmptyState
      v-else
      icon="🛒"
      text="暂无订单"
      button-text="去逛逛"
      @button-click="goToHome"
    />
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { listMyOrders } from '@/api/orders'
import OrderCard from '@/components/order-card.vue'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'

const orders = ref([])
const loading = ref(true)

onMounted(() => {
  loadOrders()
})

/**
 * 加载订单列表
 */
const loadOrders = async () => {
  try {
    loading.value = true
    const data = await listMyOrders()
    orders.value = Array.isArray(data) ? data : []
  } catch (error) {
    console.error('加载订单列表失败:', error)
    orders.value = []
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 处理订单点击
 */
const handleOrderClick = (order) => {
  // order-card 组件内部已处理跳转，这里可以添加其他逻辑
  console.log('点击订单:', order)
}

/**
 * 跳转到首页
 */
const goToHome = () => {
  uni.switchTab({ url: '/pages/home/index' })
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

.orders-list {
  display: flex;
  flex-direction: column;
}
</style>

