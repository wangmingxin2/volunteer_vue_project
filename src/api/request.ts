import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 使用小写的 satoken
    const satoken = localStorage.getItem('satoken')
    if (satoken) {
      config.headers['satoken'] = satoken // 使用小写的 satoken 作为请求头
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  },
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    console.log('响应数据:', res)

    if (res.code !== 200) {
      // 处理错误
      console.error('请求错误:', res.message)

      // 处理 401 未授权错误
      if (res.code === 401) {
        handleUnauthorized()
      }

      // 直接返回错误信息，以便在业务代码中捕获并处理
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res // 直接返回响应数据，而不是整个 response 对象
    }
  },
  (error) => {
    console.error('请求异常:', error)

    // 处理 401 未授权错误
    if (error.response && error.response.status === 401) {
      handleUnauthorized()
    }

    // 检查是否有响应数据
    if (error.response && error.response.data) {
      const { code, message } = error.response.data
      if (message) {
        return Promise.reject(new Error(message))
      }
    }

    return Promise.reject(error)
  },
)

// 处理未授权错误
function handleUnauthorized() {
  // 清除登录信息
  localStorage.removeItem('satoken')
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('role')

  // 显示提示信息
  ElMessage.error('登录已过期，请重新登录')

  // 跳转到登录页
  if (router.currentRoute.value.path !== '/login') {
    router.push('/login')
  }
}

export default service
