import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

request.interceptors.request.use(config => {
  config.headers.token = localStorage.getItem('token') || ''
  return config
})

request.interceptors.response.use(
  res => {
    const { code, msg, data } = res.data
    if (code === 0) return data
    ElMessage.error(msg || '操作失败')
    return Promise.reject(new Error(msg))
  },
  err => {
    ElMessage.error(err.response?.data?.msg || '网络异常')
    return Promise.reject(err)
  }
)

export default request
