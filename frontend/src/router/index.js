import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Market from '../views/Market.vue'
import Trade from '../views/Trade.vue'
import Account from '../views/Account.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/market',
    name: 'Market',
    component: Market
  },
  {
    path: '/trade',
    name: 'Trade',
    component: Trade
  },
  {
    path: '/account',
    name: 'Account',
    component: Account
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 