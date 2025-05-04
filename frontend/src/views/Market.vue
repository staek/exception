<template>
  <div class="market">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Market Overview</span>
              <el-input
                v-model="search"
                placeholder="Search markets"
                style="width: 200px"
              />
            </div>
          </template>
          
          <el-table
            :data="filteredMarkets"
            style="width: 100%"
            @row-click="handleRowClick"
          >
            <el-table-column prop="market" label="Market" />
            <el-table-column prop="korean_name" label="Name" />
            <el-table-column prop="price" label="Price">
              <template #default="scope">
                {{ formatPrice(scope.row.price) }}
              </template>
            </el-table-column>
            <el-table-column prop="change" label="24h Change">
              <template #default="scope">
                <span :class="scope.row.change >= 0 ? 'text-success' : 'text-danger'">
                  {{ scope.row.change }}%
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="volume" label="24h Volume">
              <template #default="scope">
                {{ formatVolume(scope.row.volume) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-4">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Order Book</span>
            </div>
          </template>
          <div v-if="selectedMarket">
            <el-table :data="orderbook.asks" style="width: 100%">
              <el-table-column prop="price" label="Price" />
              <el-table-column prop="size" label="Size" />
              <el-table-column prop="total" label="Total" />
            </el-table>
          </div>
          <div v-else class="text-center">
            Select a market to view order book
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Price Chart</span>
            </div>
          </template>
          <div v-if="selectedMarket" class="chart-container">
            <Line
              :data="chartData"
              :options="chartOptions"
            />
          </div>
          <div v-else class="text-center">
            Select a market to view price chart
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { Line } from 'vue-chartjs'
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

export default {
  name: 'Market',
  components: {
    Line
  },
  setup() {
    const store = useStore()
    const search = ref('')
    const selectedMarket = ref(null)
    const orderbook = ref({ asks: [], bids: [] })
    const chartData = ref({
      labels: [],
      datasets: [{
        label: 'Price',
        data: [],
        borderColor: '#409EFF',
        tension: 0.1
      }]
    })

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false
    }

    const markets = computed(() => store.state.markets)
    const filteredMarkets = computed(() => {
      return markets.value.filter(market => 
        market.market.toLowerCase().includes(search.value.toLowerCase()) ||
        market.korean_name.toLowerCase().includes(search.value.toLowerCase())
      )
    })

    onMounted(async () => {
      await store.dispatch('fetchMarkets')
      await store.dispatch('fetchCurrentPrices', markets.value.map(m => m.market).join(','))
    })

    const handleRowClick = async (row) => {
      selectedMarket.value = row.market
      await store.dispatch('fetchOrderbook', row.market)
      orderbook.value = store.getters.getOrderbook(row.market)
      updateChartData()
    }

    const updateChartData = () => {
      // Simulated price data
      const prices = Array.from({ length: 24 }, () => Math.random() * 1000 + 50000)
      chartData.value = {
        labels: Array.from({ length: 24 }, (_, i) => `${i}:00`),
        datasets: [{
          label: 'Price',
          data: prices,
          borderColor: '#409EFF',
          tension: 0.1
        }]
      }
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW'
      }).format(price)
    }

    const formatVolume = (volume) => {
      return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW',
        notation: 'compact',
        maximumFractionDigits: 1
      }).format(volume)
    }

    return {
      search,
      markets,
      filteredMarkets,
      selectedMarket,
      orderbook,
      chartData,
      chartOptions,
      handleRowClick,
      formatPrice,
      formatVolume
    }
  }
}
</script>

<style lang="scss" scoped>
.market {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
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

  .text-center {
    text-align: center;
    padding: 20px;
    color: #909399;
  }

  .chart-container {
    height: 400px;
  }
}
</style> 