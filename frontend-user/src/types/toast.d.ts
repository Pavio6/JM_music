import { useToast, type ToastInterface } from 'vue-toastification'

declare module 'vue' {
  interface ComponentCustomProperties {
    $toast: ToastInterface
  }
}
