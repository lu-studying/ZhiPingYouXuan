<template>
  <view class="shop-card" @click="handleClick">
    <view class="shop-header">
      <view class="shop-info">
        <text class="shop-name">{{ shop.name }}</text>
        <text class="shop-category">{{ shop.category }}</text>
      </view>
      <view v-if="shop.avgScore !== null && shop.avgScore !== undefined" class="rating-wrapper">
        <rating-stars :rating="shop.avgScore" :show-score="true" />
      </view>
    </view>
    
    <view class="shop-body">
      <text class="shop-address">{{ shop.address || '地址未知' }}</text>
      <view v-if="shop.avgPrice !== null && shop.avgPrice !== undefined" class="price-info">
        <text class="price-text">{{ formatAvgPrice(shop.avgPrice) }}</text>
      </view>
    </view>
    
    <view v-if="shop.tags && shop.tags.length > 0" class="shop-tags">
      <view 
        v-for="tag in shop.tags.slice(0, 3)" 
        :key="tag.id"
        class="tag-item"
      >
        <text>{{ tag.name }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import RatingStars from './rating-stars.vue'
import { formatAvgPrice } from '@/utils/format'

/**
 * 商家卡片组件
 * 
 * Props:
 * - shop: 商家对象
 *   - id: 商家ID
 *   - name: 商家名称
 *   - category: 商家分类
 *   - address: 商家地址
 *   - avgScore: 平均评分（0-5）
 *   - avgPrice: 人均价格
 *   - tags: 标签数组（可选）
 */
const props = defineProps({
  shop: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && value.id && value.name
    }
  }
})

const emit = defineEmits(['click'])

/**
 * 处理卡片点击事件
 */
const handleClick = () => {
  emit('click', props.shop)
  
  // 跳转到商家详情页
  uni.navigateTo({
    url: `/pages/shop/detail?shopId=${props.shop.id}`
  })
}
</script>

<style scoped>
.shop-card {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.shop-card:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.12);
}

.shop-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.shop-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.shop-name {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
}

.shop-category {
  font-size: 24rpx;
  color: #999;
}

.rating-wrapper {
  display: flex;
  align-items: center;
  margin-left: 20rpx;
}

.shop-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.shop-address {
  flex: 1;
  font-size: 26rpx;
  color: #666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.price-info {
  margin-left: 20rpx;
}

.price-text {
  font-size: 26rpx;
  color: #ff6b35;
  font-weight: 500;
}

.shop-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.tag-item {
  padding: 6rpx 16rpx;
  background-color: #f5f5f5;
  border-radius: 20rpx;
  font-size: 22rpx;
  color: #666;
}

.tag-item text {
  font-size: 22rpx;
}
</style>

