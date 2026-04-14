<template>
  <view class="wallet-container">
    <view class="wallet-card">
      <text class="wallet-label">当前余额</text>
      <text class="wallet-value">¥{{ balanceDisplay }}</text>
    </view>

    <view class="action-list">
      <button class="action-btn recharge" @click="handleRecharge">充值</button>
      <button class="action-btn withdraw" @click="handleWithdraw">提现</button>
    </view>
  </view>
</template>

<script setup>
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyWallet, rechargeWallet, withdrawWallet } from '@/api/users'

const balance = ref(0)

const balanceDisplay = computed(() => Number(balance.value || 0).toFixed(2))

const loadWallet = async () => {
  try {
    const res = await getMyWallet()
    balance.value = Number(res?.balance || 0)
  } catch (error) {
    uni.showToast({ title: '加载余额失败', icon: 'none' })
  }
}

onShow(() => {
  loadWallet()
})

const askAmount = (title, callback) => {
  uni.showModal({
    title,
    editable: true,
    placeholderText: '请输入金额（如 100.00）',
    success: async (res) => {
      if (!res.confirm) return
      const text = (res.content || '').trim()
      const amount = Number(text)
      if (!text || Number.isNaN(amount) || amount <= 0) {
        uni.showToast({ title: '请输入正确金额', icon: 'none' })
        return
      }
      await callback(Number(amount.toFixed(2)))
    }
  })
}

const handleRecharge = () => {
  askAmount('账户充值', async (amount) => {
    try {
      const res = await rechargeWallet(amount)
      balance.value = Number(res?.balance || 0)
      uni.showToast({ title: '充值成功', icon: 'success' })
    } catch (error) {
      uni.showToast({ title: error?.data?.message || '充值失败', icon: 'none' })
    }
  })
}

const handleWithdraw = () => {
  askAmount('账户提现', async (amount) => {
    try {
      const res = await withdrawWallet(amount)
      balance.value = Number(res?.balance || 0)
      uni.showToast({ title: '提现成功', icon: 'success' })
    } catch (error) {
      uni.showToast({ title: error?.data?.message || '提现失败', icon: 'none' })
    }
  })
}
</script>

<style scoped>
.wallet-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20rpx;
}

.wallet-card {
  background: linear-gradient(135deg, #fff8e6 0%, #fff 100%);
  border: 1rpx solid #ffe58f;
  border-radius: 12rpx;
  padding: 28rpx;
  margin-bottom: 20rpx;
}

.wallet-label {
  display: block;
  font-size: 24rpx;
  color: #8c6b00;
  margin-bottom: 10rpx;
}

.wallet-value {
  display: block;
  font-size: 52rpx;
  color: #d48806;
  font-weight: bold;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-btn {
  height: 76rpx;
  line-height: 76rpx;
  border-radius: 38rpx;
  border: none;
  font-size: 26rpx;
}

.action-btn.recharge {
  background: #3c9cff;
  color: #fff;
}

.action-btn.withdraw {
  background: #fff;
  color: #3c9cff;
  border: 1rpx solid #3c9cff;
}
</style>
