import type { App } from 'vue'
import { Icon } from '@iconify/vue'
import * as Common from '@/components/common'

export default function globalComponents(app: App<Element>) {
  app.component('Icon', Icon)
  Object.keys(Common).forEach((key) => {
    app.component(key, Common[key as keyof typeof Common])
  })
}
