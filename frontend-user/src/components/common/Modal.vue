<script lang="ts" setup>
  import { VueFinalModal } from 'vue-final-modal'
  import { ref } from 'vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  type ModalOptions = {
    title: string
    width?: string
    showFoot?: boolean
    showClose?: boolean
    closeText?: string
    showCancel?: boolean
    cancelText?: string
    showConfirm?: boolean
    confirmText?: string
    displayDirective?: 'if' | 'show' | 'visible'
    teleportTo?: string
    modelValue: boolean
    hideOverlay?: boolean
    overlayTransition?: string
    contentTransition?: string
    clickToClose?: boolean
    escToClose?: boolean
    background?: 'interactive' | 'non-interactive'
    lockScroll?: boolean
    reserveScrollBarGap?: boolean
    swipeToClose?: 'none' | 'left' | 'right'
    closeOnClickOutside?: boolean
    afterClose?: () => void
  }

  const getInitialValues = (): ModalOptions => ({
    title: '',
    width: '30vw',
    showFoot: true,
    showClose: true,
    closeText: t('common.modal.close'),
    showCancel: true,
    showConfirm: false,
    cancelText: t('common.modal.cancel'),
    confirmText: t('common.modal.confirm'),
    teleportTo: 'body',
    modelValue: false,
    displayDirective: 'if',
    hideOverlay: false,
    overlayTransition: 'vfm-fade',
    contentTransition: 'vfm-fade',
    clickToClose: true,
    escToClose: true,
    background: 'non-interactive',
    lockScroll: true,
    reserveScrollBarGap: true,
    swipeToClose: 'none'
  })

  const options = ref(getInitialValues())

  function reset() {
    options.value = getInitialValues()
  }

  function open(modalOptions: Omit<ModalOptions, 'modelValue'>) {
    options.value.modelValue = true
    options.value = {
      ...options.value,
      ...modalOptions
    }
  }

  function close() {
    options.value.modelValue = false
    reset()
  }

  function confirm() {
    close()
  }

  defineExpose({
    reset,
    open,
    close
  })
</script>

<template>
  <Teleport to="body">
    <VueFinalModal
      v-model="options.modelValue"
      :teleport-to="options.teleportTo"
      :display-directive="options.displayDirective"
      :hide-overlay="options.hideOverlay"
      :overlay-transition="options.overlayTransition"
      :content-transition="options.contentTransition"
      :click-to-close="options.clickToClose"
      :esc-to-close="options.escToClose"
      :background="options.background"
      :lock-scroll="options.lockScroll"
      :reserve-scroll-bar-gap="options.reserveScrollBarGap"
      :swipe-to-close="options.swipeToClose"
      :zIndexFn="
        ({ index }) => {
          return 9999 + index
        }
      "
      class="flex justify-center items-center"
      @closed="
        () => {
          if (typeof options.afterClose === 'function') {
            options.afterClose()
          }
        }
      "
    >
      <Card class="bg-gray-900" :style="{ width: options.width }">
        <div class="flex flex-col w-full">
          <div class="flex flex-row justify-center border-b border-gray-700 pb-3">
            <span class="text-xl font-semibold text-white">{{ options.title }}</span>
          </div>
          <div class="flex flex-1 min-h-[100px] px-2">
            <slot />
          </div>
          <div
            v-if="options.showFoot"
            class="flex flex-row gap-3 justify-end pt-3 border-t border-gray-700"
          >
            <Button v-if="options.showConfirm" type="primary" @click="confirm">
              {{ options.confirmText }}
            </Button>
            <Button v-if="options.showCancel" @click="close"> {{ options.cancelText }} </Button>
          </div>
        </div>
      </Card>
    </VueFinalModal>
  </Teleport>
</template>

<style scoped lang="scss"></style>
