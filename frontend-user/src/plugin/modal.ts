import type { App } from 'vue'
import { createVfm } from 'vue-final-modal'
import 'vue-final-modal/style.css'

export default function globalModal(app: App<Element>) {
  const FinalModal = createVfm()
  app.use(FinalModal)
}
