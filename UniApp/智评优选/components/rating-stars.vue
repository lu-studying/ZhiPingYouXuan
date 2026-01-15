<template>
  <view class="rating-stars">
    <view 
      v-for="i in 5" 
      :key="i"
      class="star"
      :class="{ 
        active: i <= rating,
        editable: editable
      }"
      @click="handleStarClick(i)"
    >
      <text class="star-icon">★</text>
    </view>
    <text v-if="showScore" class="score">{{ formatRating(rating) }}</text>
  </view>
</template>

<script setup>
import { formatRating } from '@/utils/format'

/**
 * 评分组件
 * 
 * Props:
 * - rating: 评分值（0-5）
 * - editable: 是否可编辑（默认 false）
 * - showScore: 是否显示分数（默认 false）
 */
const props = defineProps({
  rating: {
    type: Number,
    default: 0,
    validator: (value) => value >= 0 && value <= 5
  },
  editable: {
    type: Boolean,
    default: false
  },
  showScore: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:rating', 'change'])

/**
 * 处理星星点击事件
 */
const handleStarClick = (value) => {
  if (!props.editable) {
    return
  }
  
  console.log('点击评分:', value) // 调试日志
  emit('update:rating', value)
  emit('change', value)
}
</script>

<style scoped>
.rating-stars {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.star {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 50rpx;
  height: 50rpx;
  position: relative;
  z-index: 1;
}

.star-icon {
  font-size: 40rpx;
  color: #ddd;
  transition: color 0.2s;
  user-select: none;
  pointer-events: none;
}

.star.active .star-icon {
  color: #ffd700;
}

.star.editable {
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

.star.editable:active {
  transform: scale(0.9);
}

/* 确保点击区域足够大 */
.star.editable::before {
  content: '';
  position: absolute;
  top: -10rpx;
  left: -10rpx;
  right: -10rpx;
  bottom: -10rpx;
  z-index: -1;
}

.score {
  margin-left: 10rpx;
  font-size: 28rpx;
  color: #666;
  font-weight: 500;
}
</style>

