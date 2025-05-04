<template>
  <div class="trade">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Trade</span>
              <el-select v-model="selectedMarket" placeholder="Select market">
                <el-option
                  v-for="market in markets"
                  :key="market"
                  :label="market"
                  :value="market"
                />
              </el-select>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="Buy" name="buy">
              <el-form :model="buyForm" label-width="120px">
                <el-form-item label="Price">
                  <el-input-number
                    v-model="buyForm.price"
                    :min="0"
                    :precision="0"
                    :step="1000"
                  />
                </el-form-item>
                <el-form-item label="Amount">
                  <el-input-number
                    v-model="buyForm.amount"
                    :min="0"
                    :precision="8"
                    :step="0.0001"
                  />
                </el-form-item>
                <el-form-item label="Total">
                  {{ formatPrice(buyForm.price * buyForm.amount) }}
                </el-form-item>
                <el-form-item>
                  <el-button type="success" @click="handleBuy">Buy</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <el-tab-pane label="Sell" name="sell">
              <el-form :model="sellForm" label-width="120px">
                <el-form-item label="Price">
                  <el-input-number
                    v-model="sellForm.price"
                    :min="0"
                    :precision="0"
                    :step="1000"
                  />
                </el-form-item>
                <el-form-item label="Amount">
                  <el-input-number
                    v-model="sellForm.amount"
                    :min="0"
                    :precision="8"
                    :step="0.0001"
                  />
                </el-form-item>
                <el-form-item label="Total">
                  {{ formatPrice(sellForm.price * sellForm.amount) }}
                </el-form-item>
                <el-form-item>
                  <el-button type="danger" @click="handleSell">Sell</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <el-card class="mt-4">
          <template #header>
            <div class="card-header">
              <span>Order History</span>
            </div>
          </template>
          <el-table :data="orderHistory" style="width: 100%">
            <el-table-column prop="market" label="Market" />
            <el-table-column prop="side" label="Type">
              <template #default="scope">
                <el-tag :type="scope.row.side === 'bid' ? 'success' : 'danger'">
                  {{ scope.row.side === 'bid' ? 'Buy' : 'Sell' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="Price" />
            <el-table-column prop="volume" label="Amount" />
            <el-table-column prop="state" label="Status" />
            <el-table-column label="Actions">
              <template #default="scope">
                <el-button
                  v-if="scope.row.state === 'wait'"
                  type="danger"
                  size="small"
                  @click="handleCancel(scope.row.uuid)"
                >
                  Cancel
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Order Book</span>
            </div>
          </template>
          <div v-if="selectedMarket">
            <div class="order-book">
              <div class="asks">
                <div v-for="ask in orderbook.asks" :key="ask.price" class="order-row">
                  <span class="price">{{ formatPrice(ask.price) }}</span>
                  <span class="amount">{{ ask.size }}</span>
                  <span class="total">{{ formatPrice(ask.price * ask.size) }}</span>
                </div>
              </div>
              <div class="current-price">
                {{ formatPrice(currentPrice) }}
              </div>
              <div class="bids">
                <div v-for="bid in orderbook.bids" :key="bid.price" class="order-row">
                  <span class="price">{{ formatPrice(bid.price) }}</span>
                  <span class="amount">{{ bid.size }}</span>
                  <span class="total">{{ formatPrice(bid.price * bid.size) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="text-center">
            Select a market to view order book
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'Trade',
  setup() {
    const store = useStore()
    const selectedMarket = ref('')
    const activeTab = ref('buy')
    const markets = ref(['KRW-BTC', 'KRW-ETH', 'KRW-XRP'])
    const orderHistory = ref([])
    const orderbook = ref({ asks: [], bids: [] })
    const currentPrice = ref(0)

    const buyForm = ref({
      price: 0,
      amount: 0
    })

    const sellForm = ref({
      price: 0,
      amount: 0
    })

    onMounted(async () => {
      await store.dispatch('fetchMarkets')
      await store.dispatch('fetchCurrentPrices', markets.value.join(','))
    })

    watch(selectedMarket, async (newMarket) => {
      if (newMarket) {
        await store.dispatch('fetchOrderbook', newMarket)
        orderbook.value = store.getters.getOrderbook(newMarket)
        currentPrice.value = store.getters.getMarketPrice(newMarket)
      }
    })

    const handleBuy = async () => {
      try {
        await store.dispatch('placeOrder', {
          market: selectedMarket.value,
          side: 'bid',
          price: buyForm.value.price.toString(),
          volume: buyForm.value.amount.toString()
        })
        ElMessage.success('Buy order placed successfully')
      } catch (error) {
        ElMessage.error('Failed to place buy order')
      }
    }

    const handleSell = async () => {
      try {
        await store.dispatch('placeOrder', {
          market: selectedMarket.value,
          side: 'ask',
          price: sellForm.value.price.toString(),
          volume: sellForm.value.amount.toString()
        })
        ElMessage.success('Sell order placed successfully')
      } catch (error) {
        ElMessage.error('Failed to place sell order')
      }
    }

    const handleCancel = async (uuid) => {
      try {
        await store.dispatch('cancelOrder', uuid)
        ElMessage.success('Order cancelled successfully')
      } catch (error) {
        ElMessage.error('Failed to cancel order')
      }
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW'
      }).format(price)
    }

    return {
      selectedMarket,
      activeTab,
      markets,
      orderHistory,
      orderbook,
      currentPrice,
      buyForm,
      sellForm,
      handleBuy,
      handleSell,
      handleCancel,
      formatPrice
    }
  }
}
</script>

<style lang="scss" scoped>
.trade {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .mt-4 {
    margin-top: 20px;
  }

  .text-center {
    text-align: center;
    padding: 20px;
    color: #909399;
  }

  .order-book {
    .order-row {
      display: flex;
      justify-content: space-between;
      padding: 4px 0;
      font-size: 14px;

      .price {
        flex: 1;
      }

      .amount {
        flex: 1;
        text-align: right;
      }

      .total {
        flex: 1;
        text-align: right;
      }
    }

    .current-price {
      text-align: center;
      padding: 10px;
      font-size: 18px;
      font-weight: bold;
      background-color: #f5f7fa;
      margin: 10px 0;
    }

    .asks {
      .order-row {
        color: #F56C6C;
      }
    }

    .bids {
      .order-row {
        color: #67C23A;
      }
    }
  }
}
</style> 