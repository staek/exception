<template>
  <div class="account">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Account Balance</span>
            </div>
          </template>
          
          <el-table :data="balance" style="width: 100%">
            <el-table-column prop="currency" label="Currency" />
            <el-table-column prop="balance" label="Balance">
              <template #default="scope">
                {{ formatBalance(scope.row.balance, scope.row.currency) }}
              </template>
            </el-table-column>
            <el-table-column prop="avg_buy_price" label="Avg. Buy Price">
              <template #default="scope">
                {{ formatPrice(scope.row.avg_buy_price) }}
              </template>
            </el-table-column>
            <el-table-column prop="current_price" label="Current Price">
              <template #default="scope">
                {{ formatPrice(scope.row.current_price) }}
              </template>
            </el-table-column>
            <el-table-column label="Profit/Loss">
              <template #default="scope">
                <span :class="getProfitClass(scope.row)">
                  {{ calculateProfit(scope.row) }}
                </span>
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
              <span>Recent Orders</span>
            </div>
          </template>
          <el-table :data="recentOrders" style="width: 100%">
            <el-table-column prop="market" label="Market" />
            <el-table-column prop="side" label="Type">
              <template #default="scope">
                <el-tag :type="scope.row.side === 'bid' ? 'success' : 'danger'">
                  {{ scope.row.side === 'bid' ? 'Buy' : 'Sell' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="price" label="Price">
              <template #default="scope">
                {{ formatPrice(scope.row.price) }}
              </template>
            </el-table-column>
            <el-table-column prop="volume" label="Amount" />
            <el-table-column prop="state" label="Status" />
            <el-table-column prop="created_at" label="Date">
              <template #default="scope">
                {{ formatDate(scope.row.created_at) }}
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>Asset Distribution</span>
            </div>
          </template>
          <div class="chart-container">
            <Doughnut
              :data="chartData"
              :options="chartOptions"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { Doughnut } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Legend)

export default {
  name: 'Account',
  components: {
    Doughnut
  },
  setup() {
    const store = useStore()
    const balance = ref([])
    const recentOrders = ref([])

    const chartData = computed(() => ({
      labels: balance.value.map(item => item.currency),
      datasets: [{
        data: balance.value.map(item => item.balance * item.current_price),
        backgroundColor: [
          '#409EFF',
          '#67C23A',
          '#E6A23C',
          '#F56C6C',
          '#909399'
        ]
      }]
    }))

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false
    }

    onMounted(async () => {
      await store.dispatch('fetchBalance')
      balance.value = store.state.balance
      updateChartData()
    })

    const formatBalance = (balance, currency) => {
      if (currency === 'KRW') {
        return new Intl.NumberFormat('ko-KR', {
          style: 'currency',
          currency: 'KRW'
        }).format(balance)
      }
      return `${balance} ${currency}`
    }

    const formatPrice = (price) => {
      return new Intl.NumberFormat('ko-KR', {
        style: 'currency',
        currency: 'KRW'
      }).format(price)
    }

    const formatDate = (date) => {
      return new Date(date).toLocaleString('ko-KR')
    }

    const calculateProfit = (row) => {
      if (row.currency === 'KRW') return '-'
      const profit = (row.current_price - row.avg_buy_price) * row.balance
      const profitPercentage = ((row.current_price - row.avg_buy_price) / row.avg_buy_price) * 100
      return `${formatPrice(profit)} (${profitPercentage.toFixed(2)}%)`
    }

    const getProfitClass = (row) => {
      if (row.currency === 'KRW') return ''
      return row.current_price > row.avg_buy_price ? 'text-success' : 'text-danger'
    }

    return {
      balance,
      recentOrders,
      chartData,
      chartOptions,
      formatBalance,
      formatPrice,
      formatDate,
      calculateProfit,
      getProfitClass
    }
  }
}
</script>

<style lang="scss" scoped>
.account {
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

  .chart-container {
    height: 300px;
  }
}
</style> 