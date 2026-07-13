<template>
  <div
    :class="[
      'transition-colors duration-200 cursor-pointer items-center justify-center gap-1',
      typeClasses[type],
      rounded ? 'rounded-full px-4 p-2' : 'rounded-lg px-4 py-2',
      inline ? 'inline-flex' : 'flex',
      loading ? 'opacity-50 cursor-not-allowed' : ''
    ]"
    :disabled="loading"
  >
    <!-- Loading 状态显示 -->
    <span v-if="loading" class="flex items-center">
      <Icon icon="codex:loader" class="mr-2" />
      <slot name="loading">{{ t('common.loading') }}</slot>
    </span>
    <!-- 非 Loading 状态显示插槽内容 -->
    <template v-else>
      <slot></slot>
    </template>
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { useI18n } from 'vue-i18n'

  defineOptions({
    name: 'Button'
  })

  interface Props {
    type?: 'primary' | 'default' | 'danger' | 'warn' | 'success'
    rounded?: boolean
    inline?: boolean
    loading?: boolean
  }

  const props = withDefaults(defineProps<Props>(), {
    type: 'default',
    rounded: false,
    inline: false,
    loading: false
  })

  const typeClasses = computed(() => ({
    primary: 'bg-blue-600 hover:bg-blue-500 text-white',
    default: 'bg-gray-700 hover:bg-gray-600 text-white',
    danger: 'bg-red-600 hover:bg-red-500 text-white',
    warn: 'bg-orange-600 hover:bg-orange-500 text-white',
    success: 'bg-green-600 hover:bg-green-500 text-white'
  }))

  const { t } = useI18n()
</script>

<style lang="scss" scoped></style>
