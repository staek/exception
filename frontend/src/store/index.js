import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    markets: [],
    currentPrices: {},
    orderbook: {},
    balance: [],
    orders: []
  },
  
  mutations: {
    SET_MARKETS(state, markets) {
      state.markets = markets
    },
    SET_CURRENT_PRICES(state, prices) {
      state.currentPrices = prices
    },
    SET_ORDERBOOK(state, orderbook) {
      state.orderbook = orderbook
    },
    SET_BALANCE(state, balance) {
      state.balance = balance
    },
    SET_ORDERS(state, orders) {
      state.orders = orders
    },
    UPDATE_PRICE(state, { market, price }) {
      state.currentPrices[market] = price
    }
  },
  
  actions: {
    async fetchMarkets({ commit }) {
      try {
        const response = await axios.get('/api/v1/upbit/markets')
        commit('SET_MARKETS', response.data)
      } catch (error) {
        console.error('Error fetching markets:', error)
      }
    },
    
    async fetchCurrentPrices({ commit }, markets) {
      try {
        const response = await axios.get(`/api/v1/upbit/ticker?markets=${markets}`)
        commit('SET_CURRENT_PRICES', response.data)
      } catch (error) {
        console.error('Error fetching current prices:', error)
      }
    },
    
    async fetchOrderbook({ commit }, markets) {
      try {
        const response = await axios.get(`/api/v1/upbit/orderbook?markets=${markets}`)
        commit('SET_ORDERBOOK', response.data)
      } catch (error) {
        console.error('Error fetching orderbook:', error)
      }
    },
    
    async fetchBalance({ commit }) {
      try {
        const response = await axios.get('/api/v1/upbit/accounts')
        commit('SET_BALANCE', response.data)
      } catch (error) {
        console.error('Error fetching balance:', error)
      }
    },
    
    async placeOrder({ commit }, orderRequest) {
      try {
        const response = await axios.post('/api/v1/upbit/orders', orderRequest)
        return response.data
      } catch (error) {
        console.error('Error placing order:', error)
        throw error
      }
    },
    
    async cancelOrder({ commit }, uuid) {
      try {
        const response = await axios.delete(`/api/v1/upbit/order?uuid=${uuid}`)
        return response.data
      } catch (error) {
        console.error('Error canceling order:', error)
        throw error
      }
    }
  },
  
  getters: {
    getMarketPrice: (state) => (market) => {
      return state.currentPrices[market]
    },
    getOrderbook: (state) => (market) => {
      return state.orderbook[market]
    },
    getBalance: (state) => (currency) => {
      return state.balance.find(b => b.currency === currency)
    }
  }
}) 