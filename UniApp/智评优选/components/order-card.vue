<template>
  <view class="order-card" @click="handleClick">
    <!-- 商家信息 -->
    <view class="order-header">
      <view class="shop-info">
        <text class="shop-name">{{ order.shopName || '商家' }}</text>
        <text class="order-time">{{ formatTime(order.visitTime) }}</text>
      </view>
      <view class="amount-wrapper">
        <text class="amount-label">消费</text>
        <text class="amount-value">¥{{ formatAmount(order.amount) }}</text>
      </view>
    </view>
    
    <!-- 消费项明细 -->
    <view v-if="orderItems && orderItems.length > 0" class="order-items">
      <view 
        v-for="(item, index) in orderItems" 
        :key="index"
        class="order-item"
      >
        <text class="item-name">{{ item.name || '消费项' }}</text>
        <text v-if="item.price" class="item-price">¥{{ formatAmount(item.price) }}</text>
      </view>
    </view>
    
    <!-- 订单ID（可选显示） -->
    <view class="order-footer">
      <text class="order-id">订单号：{{ order.id }}</text>
    </view>
  </view>
</template>

<script setup>
import { computed } from 'vue'
import { formatDate } from '@/utils/format'

/**
 * 订单卡片组件
 * 
 * Props:
 * - order: 订单对象
 *   - id: 订单ID
 *   - shopId: 商家ID
 *   - shopName: 商家名称
 *   - amount: 消费金额
 *   - visitTime: 到店时间
 *   - items: 消费项明细（JSON字符串或数组）
 */
const props = defineProps({
  order: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && value.id
    }
  }
})

const emit = defineEmits(['click'])

/**
 * 解析消费项明细
 */
const orderItems = computed(() => {
  if (!props.order.items) {
    return []
  }
  
  // 如果是字符串，尝试解析 JSON
  if (typeof props.order.items === 'string') {
    try {
      const parsed = JSON.parse(props.order.items)
      return Array.isArray(parsed) ? parsed : []
    } catch {
      return []
    }
  }
  
  // 如果是数组，直接返回
  if (Array.isArray(props.order.items)) {
    return props.order.items
  }
  
  return []
})

/**
 * 格式化金额
 */
const formatAmount = (amount) => {
  if (amount === null || amount === undefined) {
    return '0.00'
  }
  return Number(amount).toFixed(2)
}

/**
 * 格式化时间
 */
const formatTime = (date) => {
  if (!date) {
    return ''
  }
  return formatDate(date)
}

/**
 * 处理点击事件
 */
const handleClick = () => {
  if (props.order.shopId) {
    emit('click', props.order)
    uni.navigateTo({
      url: `/pages/shop/detail?shopId=${props.order.shopId}`
    })
  }
}
</script>

<style scoped>
.order-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.order-card:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.shop-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
  flex: 1;
}

.shop-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.order-time {
  font-size: 24rpx;
  color: #999;
}

.amount-wrapper {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4rpx;
}

.amount-label {
  font-size: 24rpx;
  color: #999;
}

.amount-value {
  font-size: 36rpx;
  font-weight: bold;
  color: #ff6b35;
}

/* 消费项明细 */
.order-items {
  margin-bottom: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12rpx 0;
  font-size: 26rpx;
  color: #666;
}

.item-name {
  flex: 1;
}

.item-price {
  color: #333;
  font-weight: 500;
  margin-left: 20rpx;
}

/* 订单底部 */
.order-footer {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.order-id {
  font-size: 22rpx;
  color: #ccc;
}
</style>

