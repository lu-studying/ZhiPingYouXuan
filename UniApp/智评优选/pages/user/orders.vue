<template>
  <view class="orders-container">
    <view v-if="pendingItems.length > 0" class="checkout-card">
      <view class="checkout-header">
        <text class="checkout-title">确认下单</text>
        <text class="checkout-subtitle">{{ pendingOrder?.shopName || '当前商家' }}</text>
      </view>

      <view class="checkout-items">
        <view v-for="(item, index) in pendingItems" :key="`${item.menuId}-${index}`" class="checkout-item">
          <image
            v-if="item.image"
            class="item-image"
            :src="getImageUrl(item.image)"
            mode="aspectFill"
          />
          <view v-else class="item-image-placeholder">🍽️</view>

          <view class="item-main">
            <text class="item-name">{{ item.name || '未命名商品' }}</text>
            <text class="item-price">单价 {{ formatPrice(item.price || 0) }}</text>
            <text class="item-subtotal">小计 {{ formatPrice((item.price || 0) * (item.quantity || 0)) }}</text>
          </view>

          <view class="item-actions">
            <button class="qty-btn" @click="decreasePendingQty(index)">-</button>
            <text class="qty-text">{{ item.quantity }}</text>
            <button class="qty-btn" @click="increasePendingQty(index)">+</button>
          </view>
        </view>
      </view>

      <view class="checkout-footer">
        <text class="checkout-total">共{{ pendingTotalCount }}份，合计 {{ formatPrice(pendingTotalAmount) }}</text>
        <view class="checkout-footer-actions">
          <button class="btn-clear" @click="clearPendingOrder">清空</button>
          <button class="btn-submit" :disabled="submitting" @click="submitPendingOrder">
            {{ submitting ? '支付中...' : '确认并支付' }}
          </button>
        </view>
      </view>
    </view>

    <!-- 加载状态 -->
    <Loading v-if="loading" text="加载中..." />
    
    <!-- 订单列表 -->
    <view v-else-if="orders.length > 0" class="orders-list">
      <view v-for="order in orders" :key="order.id" class="order-wrap">
        <order-card
          :order="order"
          @click="handleOrderClick"
        />
        <view v-if="order.payStatus === 0" class="order-actions">
          <button class="btn-cancel" @click="handleCancel(order)">取消</button>
          <button class="btn-pay" @click="handlePay(order)">去支付</button>
        </view>
      </view>
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
import { computed, ref, onMounted } from 'vue'
import { onPullDownRefresh, onShow } from '@dcloudio/uni-app'
import { listMyOrders, createOrder, payOrder, cancelOrder } from '@/api/orders'
import OrderCard from '@/components/order-card.vue'
import Loading from '@/components/loading.vue'
import EmptyState from '@/components/empty-state.vue'
import { formatPrice, getImageUrl } from '@/utils/format'

const orders = ref([])
const loading = ref(true)
const pendingOrder = ref(null)
const submitting = ref(false)

const pendingItems = computed(() => pendingOrder.value?.items || [])
const pendingTotalCount = computed(() => {
  return pendingItems.value.reduce((sum, item) => sum + Number(item.quantity || 0), 0)
})
const pendingTotalAmount = computed(() => {
  return pendingItems.value.reduce((sum, item) => {
    return sum + Number(item.price || 0) * Number(item.quantity || 0)
  }, 0)
})

onMounted(() => {
  loadPendingOrder()
  loadOrders()
})

onShow(() => {
  loadPendingOrder()
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

const loadPendingOrder = () => {
  const draft = uni.getStorageSync('pending_checkout_order')
  if (!draft || !Array.isArray(draft.items) || draft.items.length === 0) {
    pendingOrder.value = null
    return
  }
  pendingOrder.value = {
    shopId: Number(draft.shopId),
    shopName: draft.shopName || '',
    items: draft.items
      .map(item => ({
        menuId: Number(item.menuId),
        name: item.name || '',
        image: item.image || '',
        price: Number(item.price || 0),
        quantity: Math.max(1, Number(item.quantity || 1))
      }))
      .filter(item => item.menuId)
  }
}

const persistPendingOrder = () => {
  if (!pendingOrder.value || !pendingOrder.value.items?.length) {
    clearPendingOrder()
    return
  }
  uni.setStorageSync('pending_checkout_order', pendingOrder.value)
}

const increasePendingQty = (index) => {
  pendingOrder.value.items[index].quantity += 1
  persistPendingOrder()
}

const decreasePendingQty = (index) => {
  const item = pendingOrder.value.items[index]
  if (!item) return
  if (item.quantity <= 1) {
    pendingOrder.value.items.splice(index, 1)
  } else {
    item.quantity -= 1
  }
  persistPendingOrder()
}

const clearPendingOrder = () => {
  pendingOrder.value = null
  uni.removeStorageSync('pending_checkout_order')
}

const submitPendingOrder = async () => {
  if (!pendingOrder.value?.shopId || pendingItems.value.length === 0) {
    uni.showToast({ title: '暂无可支付商品', icon: 'none' })
    return
  }
  if (submitting.value) return

  try {
    submitting.value = true
    const createRes = await createOrder(pendingOrder.value.shopId, {
      items: JSON.stringify(
        pendingItems.value.map(item => ({
          menuId: item.menuId,
          quantity: item.quantity
        }))
      ),
      visitTime: new Date().toISOString()
    })
    await payOrder(createRes.id, 'MOCK_WECHAT')
    uni.showToast({ title: '支付成功', icon: 'success' })
    clearPendingOrder()
    loadOrders()
  } catch (e) {
    console.error('下单支付失败:', e)
    uni.showToast({ title: e?.data?.message || '下单或支付失败', icon: 'none' })
  } finally {
    submitting.value = false
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

const handlePay = async (order) => {
  try {
    await payOrder(order.id)
    uni.showToast({ title: '支付成功', icon: 'success' })
    loadOrders()
  } catch (e) {
    uni.showToast({ title: e?.data?.message || '支付失败', icon: 'none' })
  }
}

const handleCancel = async (order) => {
  try {
    await cancelOrder(order.id)
    uni.showToast({ title: '已取消', icon: 'success' })
    loadOrders()
  } catch (e) {
    uni.showToast({ title: '取消失败', icon: 'none' })
  }
}

// 下拉刷新
onPullDownRefresh(() => {
  Promise.all([loadOrders(), Promise.resolve(loadPendingOrder())]).finally(() => {
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

.checkout-card {
  background: #fff;
  border-radius: 16rpx;
  padding: 22rpx;
  margin-bottom: 20rpx;
}

.checkout-header {
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  margin-bottom: 18rpx;
}

.checkout-title {
  font-size: 30rpx;
  color: #222;
  font-weight: 600;
}

.checkout-subtitle {
  font-size: 24rpx;
  color: #666;
}

.checkout-items {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.checkout-item {
  display: flex;
  align-items: center;
  background: #fafafa;
  border-radius: 12rpx;
  padding: 12rpx;
}

.item-image {
  width: 108rpx;
  height: 108rpx;
  border-radius: 10rpx;
  margin-right: 12rpx;
  flex-shrink: 0;
}

.item-image-placeholder {
  width: 108rpx;
  height: 108rpx;
  border-radius: 10rpx;
  margin-right: 12rpx;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6rpx;
  min-width: 0;
}

.item-name {
  font-size: 28rpx;
  color: #222;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-price, .item-subtotal {
  font-size: 24rpx;
  color: #666;
}

.item-subtotal {
  color: #ff6b35;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.qty-btn {
  width: 48rpx;
  height: 48rpx;
  line-height: 48rpx;
  border-radius: 8rpx;
  background: #f2f3f5;
  border: none;
  padding: 0;
  font-size: 30rpx;
  color: #333;
}

.qty-text {
  min-width: 32rpx;
  text-align: center;
  font-size: 24rpx;
  color: #333;
}

.checkout-footer {
  margin-top: 20rpx;
  padding-top: 16rpx;
  border-top: 1rpx solid #f0f0f0;
}

.checkout-total {
  display: block;
  font-size: 26rpx;
  color: #222;
  margin-bottom: 12rpx;
}

.checkout-footer-actions {
  display: flex;
  justify-content: flex-end;
  gap: 14rpx;
}

.btn-clear, .btn-submit {
  height: 56rpx;
  line-height: 56rpx;
  border-radius: 28rpx;
  padding: 0 24rpx;
  font-size: 24rpx;
  border: none;
}

.btn-clear {
  background: #f2f3f5;
  color: #666;
}

.btn-submit {
  background: #ff6b35;
  color: #fff;
}

.orders-list {
  display: flex;
  flex-direction: column;
}

.order-wrap {
  margin-bottom: 10rpx;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin: -6rpx 10rpx 20rpx;
}

.btn-pay, .btn-cancel {
  height: 56rpx;
  line-height: 56rpx;
  border-radius: 28rpx;
  padding: 0 26rpx;
  font-size: 24rpx;
  border: none;
}

.btn-pay {
  background: #3c9cff;
  color: #fff;
}

.btn-cancel {
  background: #f2f3f5;
  color: #666;
}
</style>

