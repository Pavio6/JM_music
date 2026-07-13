<template>
  <div class="file-input-wrapper">
    <input
      type="file"
      @change="handleFileChange"
      class="file-input"
      v-bind="$attrs"
      ref="fileInput"
    />
    <div class="custom-file-input">
      <span
        v-if="showText"
        class="file-name bg-gray-800 text-white placeholder-gray-400 rounded-lg p-4 focus:ring-2 focus:ring-blue-500 appearance-none border-none"
        >{{ fileName || placeholder }}</span
      >
      <Button type="default" @click="triggerFileInput" v-bind="buttonProps">{{
        buttonText
      }}</Button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import Button from './Button.vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const props = defineProps<{
    placeholder?: string
    buttonText?: string
    showText?: boolean
    buttonProps?: Record<string, any>
  }>()

  const emit = defineEmits<{
    (e: 'update:modelValue', file: File | null): void
    (e: 'change', event: Event): void
  }>()

  // 计算有效的占位符和按钮文本
  const placeholder = computed(() => props.placeholder || t('common.chooseFilePlaceholder'))
  const buttonText = computed(() => props.buttonText || t('common.chooseFile'))
  const showText = computed(() => props.showText ?? true)
  const buttonProps = computed(() => props.buttonProps || {})

  const fileInput = ref<HTMLInputElement | null>(null)
  const fileName = ref('')

  const handleFileChange = (event: Event) => {
    const input = event.target as HTMLInputElement
    const file = input.files?.[0] || null
    fileName.value = file?.name || ''
    emit('update:modelValue', file)
    emit('change', event)
  }

  const triggerFileInput = () => {
    fileInput.value?.click()
  }
</script>

<style lang="scss" scoped>
  .file-input-wrapper {
    width: 100%;

    .file-input {
      display: none;
    }

    .custom-file-input {
      display: flex;
      gap: 8px;
      width: 100%;

      .file-name {
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
</style>
