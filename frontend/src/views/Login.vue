<template>
  <div class="login-bg">
    <el-card class="login-card">
      <h2 class="title">CRM 管理系统</h2>
      <el-form :model="form" @keyup.enter="login">
        <el-form-item>
          <el-input v-model="form.empNo" placeholder="员工编号" :prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="login">登 录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'

const router = useRouter()
const form = reactive({ empNo: '', password: '' })
const loading = ref(false)

async function login() {
  if (!form.empNo || !form.password) return ElMessage.warning('请输入员工编号和密码')
  loading.value = true
  try {
    const data = await request.post('/login', form)
    localStorage.setItem('token', data.token)
    localStorage.setItem('empName', data.employee.name)
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-bg {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f3a5f, #2b6cb0);
}
.login-card { width: 380px; padding: 30px 10px; }
.title { text-align: center; margin-bottom: 24px; color: #303133; }
</style>
