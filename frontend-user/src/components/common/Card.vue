<template>
  <div
    :class="[
      'bg-gray-800 rounded-xl border-gray-700',
      noPadding ? 'p-0' : `p-${size == 'md' ? 4 : size == 'sm' ? 2 : 6}`
    ]"
  >
    <slot></slot>
  </div>
</template>

<script lang="ts" setup>
  defineOptions({
    name: 'Card'
  })
  const props = withDefaults(
    defineProps<{
      size?: 'md' | 'sm' | 'lg'
      noPadding?: boolean
    }>(),
    {
      size: 'md',
      noPadding: false
    }
  )
</script>
