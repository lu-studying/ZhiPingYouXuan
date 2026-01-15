<template>
  <view class="empty-state-container">
    <view class="empty-icon">
      <text class="icon-text">{{ icon }}</text>
    </view>
    <text class="empty-text">{{ text }}</text>
    <view v-if="buttonText" class="empty-button-wrapper">
      <button class="empty-button" @click="handleButtonClick">
        {{ buttonText }}
      </button>
    </view>
  </view>
</template>

<script setup>
/**
 * 空状态组件
 * 
 * Props:
 * - icon: 图标文字（可选，默认 '📭'）
 * - text: 提示文字（可选，默认 '暂无数据'）
 * - buttonText: 按钮文字（可选，不传则不显示按钮）
 */
const props = defineProps({
  icon: {
    type: String,
    default: '📭'
  },
  text: {
    type: String,
    default: '暂无数据'
  },
  buttonText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['buttonClick'])

/**
 * 处理按钮点击
 */
const handleButtonClick = () => {
  emit('buttonClick')
}
</script>

<style scoped>
.empty-state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 40rpx;
  min-height: 400rpx;
}

.empty-icon {
  margin-bottom: 30rpx;
}

.icon-text {
  font-size: 120rpx;
  line-height: 1;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
  text-align: center;
  line-height: 1.6;
}

.empty-button-wrapper {
  width: 100%;
  max-width: 400rpx;
}

.empty-button {
  width: 100%;
  height: 80rpx;
  background-color: #3c9cff;
  color: #fff;
  border-radius: 40rpx;
  line-height: 80rpx;
  text-align: center;
  border: none;
  font-size: 28rpx;
  font-weight: 500;
}

.empty-button:active {
  background-color: #2d7ce6;
  transform: scale(0.98);
}
</style>

