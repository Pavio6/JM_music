import { useToast } from 'vue-toastification'
import type { TYPE } from 'vue-toastification'
import type { ToastOptions } from 'vue-toastification/dist/types/types'

type CustomToastOptions<T> = ToastOptions & {
  type?: T
}

// 创建一个独立的toast实例
const toastIn = useToast()

// 导出toast函数
export const toast = {
  success: (message: string, options?: CustomToastOptions<TYPE.SUCCESS>) =>
    toastIn.success(message, options),
  error: (message: string, options?: CustomToastOptions<TYPE.ERROR>) =>
    toastIn.error(message, options),
  info: (message: string, options?: CustomToastOptions<TYPE.INFO>) =>
    toastIn.info(message, options),
  warning: (message: string, options?: CustomToastOptions<TYPE.WARNING>) =>
    toastIn.warning(message, options)
}
