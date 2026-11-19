<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">CRM 管理系统</div>
      <el-menu :default-active="$route.path" router background-color="#1f2d3d" text-color="#bfcbd9"
               active-text-color="#409eff" unique-opened>
        <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon>工作台</el-menu-item>
        <el-menu-item index="/clue"><el-icon><Opportunity /></el-icon>线索</el-menu-item>
        <el-menu-item index="/customer"><el-icon><User /></el-icon>客户</el-menu-item>
        <el-menu-item index="/opportunity"><el-icon><TrendCharts /></el-icon>商机</el-menu-item>
        <el-menu-item index="/order"><el-icon><Tickets /></el-icon>订单</el-menu-item>
        <el-sub-menu index="product">
          <template #title><el-icon><Goods /></el-icon><span>产品管理</span></template>
          <el-menu-item index="/product/category">产品分类</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="setting">
          <template #title><el-icon><Setting /></el-icon><span>设置</span></template>
          <el-menu-item index="/employee">员工管理</el-menu-item>
          <el-menu-item index="/role">角色管理</el-menu-item>
          <el-menu-item index="/department">部门管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <div class="right">
          {{ empName }}
          <el-button link type="danger" @click="logout">退出</el-button>
        </div>
      </el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const empName = localStorage.getItem('empName') || ''

function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100%; }
.aside { background: #1f2d3d; }
.logo {
  height: 60px; line-height: 60px; text-align: center;
  color: #fff; font-weight: bold; font-size: 16px; background: #18222e;
}
.header {
  display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #e6e6e6; background: #fff;
}
.right { display: flex; align-items: center; gap: 8px; color: #606266; }
.el-menu { border-right: none; }
</style>
