import axios from 'axios'
import { useUserStore } from '@/stores/user'
import { toast } from '@/utile'
import i18n from '@/plugin/i18n-instance.ts'

const api = axios.create({
  baseURL:
    import.meta.env.DEV && import.meta.env.VITE_APP_OPEN_MOCK === 'true'
      ? import.meta.env.VITE_APP_MOCK_URL
      : import.meta.env.VITE_APP_API_BASEURL,
  timeout: 1000 * 60,
  responseType: 'json'
})

api.interceptors.request.use((request) => {
  // 全局拦截请求发送前提交的参数
  const userStore = useUserStore()

  // 设置请求头
  if (request.headers) {
    if (userStore.isLogin) {
      request.headers.token = userStore.token
    }
  }
  // 是否将 POST 请求参数进行字符串化处理
  if (request.method === 'post') {
    // request.data = qs.stringify(request.data, {
    //   arrayFormat: 'brackets',
    // })
  }
  return request
})

api.interceptors.response.use(
  (response) => {
    if (response.status === 200) {
      if (response.data.state !== true) {
        console.error(response.data.message)
        return Promise.reject(response.data)
      }
    } else {
      // useUserStore().logout()
    }
    return Promise.resolve(response.data)
  },
  (error) => {
    let message = error.message

    if (message === 'Network Error') {
      message = i18n.global.t('common.api.networkError')
    } else if (message.includes('timeout')) {
      message = i18n.global.t('common.api.timeout')
    } else if (message.includes('Request failed with status code')) {
      const code = message.substr(message.length - 3)
      message = i18n.global.t('common.api.requestFailed', { code })
    }

    toast.error(message)
    console.error(message)
    return Promise.reject(error)
  }
)

export default api
