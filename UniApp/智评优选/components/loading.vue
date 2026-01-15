<template>
  <view class="loading-container">
    <view class="loading-spinner">
      <view class="spinner-dot" v-for="i in 3" :key="i" :style="getDotStyle(i)"></view>
    </view>
    <text v-if="text" class="loading-text">{{ text }}</text>
  </view>
</template>

<script setup>
/**
 * 加载状态组件
 * 
 * Props:
 * - text: 加载提示文字（可选）
 * - size: 加载动画大小（可选，默认 'normal'，可选值：'small', 'normal', 'large'）
 */
const props = defineProps({
  text: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'normal',
    validator: (value) => ['small', 'normal', 'large'].includes(value)
  }
})

/**
 * 获取圆点样式
 */
const getDotStyle = (index) => {
  const sizeMap = {
    small: { dotSize: '12rpx', gap: '8rpx' },
    normal: { dotSize: '16rpx', gap: '10rpx' },
    large: { dotSize: '20rpx', gap: '12rpx' }
  }
  
  const { dotSize, gap } = sizeMap[props.size]
  const delay = (index - 1) * 0.2
  
  return {
    width: dotSize,
    height: dotSize,
    marginRight: index < 3 ? gap : '0',
    animationDelay: `${delay}s`
  }
}
</script>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40rpx 0;
}

.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.spinner-dot {
  border-radius: 50%;
  background-color: #3c9cff;
  animation: bounce 1.4s infinite ease-in-out both;
}

.spinner-dot:nth-child(1) {
  animation-delay: -0.32s;
}

.spinner-dot:nth-child(2) {
  animation-delay: -0.16s;
}

.spinner-dot:nth-child(3) {
  animation-delay: 0s;
}

@keyframes bounce {
  0%, 80%, 100% {
    transform: scale(0);
    opacity: 0.5;
  }
  40% {
    transform: scale(1);
    opacity: 1;
  }
}

.loading-text {
  margin-top: 20rpx;
  font-size: 26rpx;
  color: #999;
}
</style>

