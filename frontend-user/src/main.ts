import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'virtual:uno.css'
import 'normalize.css'
import './assets/styles/main.scss'
import PiniaPlugin from '@/plugin/pinia'
import ToastPlugin from '@/plugin/toast'
import ModalPlugin from '@/plugin/modal'
import I18nPlugin from '@/plugin/i18n'
import GlobalComponentsPlugin from '@/plugin/globalComponents'

// app
const app = createApp(App)
// router
app.use(router)
// store
PiniaPlugin(app)
// toast
ToastPlugin(app)
// modal
ModalPlugin(app)
// i18n
I18nPlugin(app)
// global component
GlobalComponentsPlugin(app)
// mount
app.mount('#app')
