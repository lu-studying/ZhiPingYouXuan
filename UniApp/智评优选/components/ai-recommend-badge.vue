<template>
  <view class="ai-badge">
    <text class="ai-icon">✨</text>
    <text class="ai-text">{{ text }}</text>
  </view>
</template>

<script setup>
/**
 * AI 推荐标识组件
 * 
 * Props:
 * - text: 标识文字（可选，默认 'AI 推荐'）
 * - size: 尺寸大小（可选，默认 'normal'，可选值：'small', 'normal', 'large'）
 */
const props = defineProps({
  text: {
    type: String,
    default: 'AI 推荐'
  },
  size: {
    type: String,
    default: 'normal',
    validator: (value) => ['small', 'normal', 'large'].includes(value)
  }
})
</script>

<style scoped>
.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6rpx;
  padding: 6rpx 14rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  box-shadow: 0 2rpx 6rpx rgba(102, 126, 234, 0.3);
}

.ai-icon {
  font-size: 20rpx;
  line-height: 1;
}

.ai-text {
  font-size: 20rpx;
  color: #fff;
  font-weight: 500;
  line-height: 1;
}
</style>

