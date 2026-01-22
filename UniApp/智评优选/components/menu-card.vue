<template>
  <view class="menu-card" @click="handleClick">
    <view class="menu-image-wrapper" v-if="menu.image">
      <image 
        class="menu-image" 
        :src="menu.image" 
        mode="aspectFill"
        :lazy-load="true"
      />
    </view>
    <view class="menu-image-placeholder" v-else>
      <text class="placeholder-icon">🍽️</text>
    </view>
    <view class="menu-info">
      <view class="menu-header">
        <text class="menu-name">{{ menu.name }}</text>
        <view class="recommended-badge" v-if="menu.isRecommended === 1">
          <text class="badge-text">⭐ 推荐</text>
        </view>
      </view>
      <text class="menu-description" v-if="menu.description">
        {{ menu.description }}
      </text>
      <view class="menu-footer">
        <text class="menu-price">{{ formatPrice(menu.price) }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { formatPrice } from '@/utils/format'

const props = defineProps({
  menu: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.menu)
}

// formatPrice 已经在 utils/format.js 中定义，这里直接使用
</script>

<style scoped>
.menu-card {
  display: flex;
  flex-direction: row;
  background-color: #fff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  margin-bottom: 20rpx;
  transition: transform 0.2s, box-shadow 0.2s;
}

.menu-card:active {
  transform: scale(0.98);
  box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.1);
}

.menu-image-wrapper {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
  background-color: #f5f5f5;
}

.menu-image {
  width: 100%;
  height: 100%;
}

.menu-image-placeholder {
  width: 200rpx;
  height: 200rpx;
  flex-shrink: 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-icon {
  font-size: 60rpx;
}

.menu-info {
  flex: 1;
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12rpx;
}

.menu-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.recommended-badge {
  padding: 4rpx 12rpx;
  border-radius: 12rpx;
  background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%);
  margin-left: 12rpx;
  flex-shrink: 0;
}

.badge-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: 500;
}

.menu-description {
  font-size: 24rpx;
  color: #666;
  line-height: 1.5;
  margin-bottom: 12rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.menu-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.menu-price {
  font-size: 32rpx;
  font-weight: bold;
  color: #ff6b35;
}
</style>

