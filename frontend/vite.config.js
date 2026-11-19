import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      // 前端所有 /api 请求转发到后端，避免跨域
      '/api': 'http://localhost:8080'
    }
  }
})
