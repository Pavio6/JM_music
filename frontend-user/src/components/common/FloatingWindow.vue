<template>
  <Teleport to="body">
    <div
      v-if="show"
      class="fixed inset-0 bg-black/50 z-[9999] flex"
      :class="[position === 'right' ? 'justify-end' : 'justify-start']"
      @click.self="handleClose"
    >
      <div
        class="fixed bg-black/75 backdrop-blur-md p-6 border-white/10 rounded-lg"
        :class="[position === 'right' ? 'border-l' : 'border-r']"
        :style="{
          width: width,
          height: height,
          top: top,
          right: right,
          bottom: bottom,
          left: left
        }"
      >
        <slot></slot>
      </div>
    </div>
  </Teleport>
</template>

<script lang="ts" setup>
  interface Props {
    show: boolean
    width?: string
    height?: string
    position?: 'left' | 'right'
    top?: string
    right?: string
    bottom?: string
    left?: string
  }

  const props = withDefaults(defineProps<Props>(), {
    width: '24rem',
    height: 'auto',
    position: 'right',
    top: 'auto',
    right: 'auto',
    bottom: 'auto',
    left: 'auto'
  })
  const emit = defineEmits<{
    (e: 'update:show', value: boolean): void
  }>()

  const handleClose = () => {
    emit('update:show', false)
  }
</script>
