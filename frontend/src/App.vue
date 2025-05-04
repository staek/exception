<template>
  <div class="app-container">
    <el-container>
      <el-header>
        <nav class="main-nav">
          <div class="logo">Upbit Exchange</div>
          <el-menu mode="horizontal" router>
            <el-menu-item index="/">Home</el-menu-item>
            <el-menu-item index="/market">Market</el-menu-item>
            <el-menu-item index="/trade">Trade</el-menu-item>
            <el-menu-item index="/account">Account</el-menu-item>
          </el-menu>
        </nav>
      </el-header>
      
      <el-main>
        <router-view></router-view>
      </el-main>
      
      <el-footer>
        <p>&copy; 2024 Upbit Exchange. All rights reserved.</p>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style lang="scss">
.app-container {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.main-nav {
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  
  .logo {
    font-size: 24px;
    font-weight: bold;
    color: #409EFF;
    margin-right: 40px;
  }
}

.el-main {
  padding: 20px;
}

.el-footer {
  text-align: center;
  color: #909399;
  padding: 20px;
  background-color: #fff;
  border-top: 1px solid #EBEEF5;
}
</style> 