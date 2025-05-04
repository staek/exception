<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="welcome-card">
          <h1>Welcome to Upbit Exchange</h1>
          <p>Your trusted cryptocurrency trading platform</p>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Market Overview</span>
            </div>
          </template>
          <el-table :data="marketData" style="width: 100%">
            <el-table-column prop="market" label="Market" />
            <el-table-column prop="price" label="Price" />
            <el-table-column prop="change" label="24h Change">
              <template #default="scope">
                <span :class="scope.row.change >= 0 ? 'text-success' : 'text-danger'">
                  {{ scope.row.change }}%
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Quick Trade</span>
            </div>
          </template>
          <el-form :model="tradeForm" label-width="120px">
            <el-form-item label="Market">
              <el-select v-model="tradeForm.market" placeholder="Select market">
                <el-option
                  v-for="market in markets"
                  :key="market"
                  :label="market"
                  :value="market"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="Type">
              <el-radio-group v-model="tradeForm.type">
                <el-radio label="buy">Buy</el-radio>
                <el-radio label="sell">Sell</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="Amount">
              <el-input-number v-model="tradeForm.amount" :min="0" :precision="8" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTrade">Trade</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

export default {
  name: 'Home',
  setup() {
    const store = useStore()
    const markets = ref(['KRW-BTC', 'KRW-ETH', 'KRW-XRP'])
    const marketData = ref([])
    const tradeForm = ref({
      market: '',
      type: 'buy',
      amount: 0
    })

    onMounted(async () => {
      await store.dispatch('fetchMarkets')
      await store.dispatch('fetchCurrentPrices', markets.value.join(','))
      updateMarketData()
    })

    const updateMarketData = () => {
      marketData.value = markets.value.map(market => ({
        market,
        price: store.getters.getMarketPrice(market),
        change: ((Math.random() * 10) - 5).toFixed(2)
      }))
    }

    const handleTrade = async () => {
      try {
        await store.dispatch('placeOrder', {
          market: tradeForm.value.market,
          side: tradeForm.value.type,
          volume: tradeForm.value.amount.toString()
        })
        ElMessage.success('Order placed successfully')
      } catch (error) {
        ElMessage.error('Failed to place order')
      }
    }

    return {
      markets,
      marketData,
      tradeForm,
      handleTrade
    }
  }
}
</script>

<style lang="scss" scoped>
.home {
  .welcome-card {
    text-align: center;
    padding: 40px;
    background: linear-gradient(135deg, #409EFF 0%, #36D1DC 100%);
    color: white;
    
    h1 {
      font-size: 2.5em;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 1.2em;
      opacity: 0.9;
    }
  }

  .mt-4 {
    margin-top: 20px;
  }

  .text-success {
    color: #67C23A;
  }

  .text-danger {
    color: #F56C6C;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style> 