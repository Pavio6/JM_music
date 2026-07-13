import type { App } from 'vue'
import i18n from './i18n-instance'

export default function globalModal(app: App<Element>) {
  app.use(i18n)
}
