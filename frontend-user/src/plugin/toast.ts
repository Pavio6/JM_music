import type { App } from 'vue'
import Toast, { POSITION, type PluginOptions } from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import '@/assets/styles/customToast.scss'

export default function globalToast(app: App<Element>) {
  const options: PluginOptions = {
    position: POSITION.TOP_RIGHT,
    hideProgressBar: true,
    draggable: false,
    closeOnClick: false,
    pauseOnFocusLoss: true,
    pauseOnHover: true,
    draggablePercent: 0.6,
    showCloseButtonOnHover: false,
    icon: false,
    rtl: false,
    // @ts-ignore
    zIndex: 99999
  }
  app.use(Toast, options)
}
