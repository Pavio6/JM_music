<template>
  <div class="flex items-center space-x-2">
    <!-- 左侧标签 -->
    <span v-if="showLabel && leftLabel" class="text-xs text-white/60">{{ leftLabel }}</span>

    <!-- 滑动条 -->
    <div
      class="relative cursor-pointer"
      :class="[baseClass, { 'flex-1': fullWidth }]"
      @click="handleClick"
      @mousedown="startDrag"
      ref="sliderRef"
    >
      <div
        class="absolute top-0 left-0 h-full rounded"
        :class="progressClass"
        :style="{ width: `${displayValue}%` }"
      ></div>
    </div>

    <!-- 右侧标签 -->
    <span v-if="showLabel && rightLabel" class="text-xs text-white/60">{{ rightLabel }}</span>
  </div>
</template>

<script lang="ts" setup>
  import { ref, computed, watch } from 'vue'

  interface Props {
    modelValue: number
    showLabel?: boolean
    leftLabel?: string
    rightLabel?: string
    height?: 'small' | 'large'
    fullWidth?: boolean
    progressColor?: 'white' | 'blue'
    updateOnDragEnd?: boolean
  }

  const props = withDefaults(defineProps<Props>(), {
    showLabel: false,
    leftLabel: '',
    rightLabel: '',
    height: 'small',
    fullWidth: true,
    progressColor: 'blue',
    updateOnDragEnd: true
  })

  const emit = defineEmits<{
    (e: 'update:modelValue', value: number): void
    (e: 'change', value: number): void
  }>()

  const value = computed(() => props.modelValue)
  const tempValue = ref(props.modelValue)
  const isDragging = ref(false)

  // 监听modelValue的变化
  watch(
    () => props.modelValue,
    (newValue) => {
      if (!isDragging.value) {
        tempValue.value = newValue
      }
    }
  )

  const displayValue = computed(() =>
    props.updateOnDragEnd || isDragging.value ? tempValue.value : value.value
  )

  // 基础样式类
  const baseClass = computed(() => {
    const baseHeight = props.height === 'small' ? 'h-1' : 'h-2'
    const hoverHeight = props.height === 'small' ? 'hover:h-1.5' : 'hover:h-2.5'
    return [baseHeight, `bg-white/10 rounded ${hoverHeight} transition-all duration-200`]
  })

  // 进度条样式类
  const progressClass = computed(() =>
    props.progressColor === 'white' ? 'bg-white/60' : 'bg-blue'
  )

  const sliderRef = ref<HTMLElement | null>(null)

  // 更新值的函数
  const updateValue = (percent: number) => {
    const newValue = Math.round(percent)
    tempValue.value = newValue
    if (!props.updateOnDragEnd) {
      emit('update:modelValue', newValue)
      emit('change', newValue)
    }
  }

  // 点击处理
  const handleClick = (event: MouseEvent) => {
    if (!sliderRef.value) return
    const rect = sliderRef.value.getBoundingClientRect()
    const percent = Math.max(0, Math.min(100, ((event.clientX - rect.left) / rect.width) * 100))
    updateValue(percent)
    if (props.updateOnDragEnd) {
      emit('update:modelValue', Math.round(percent))
      emit('change', Math.round(percent))
    }
  }

  // 拖拽处理
  const startDrag = (event: MouseEvent) => {
    isDragging.value = true
    const handleMouseMove = (e: MouseEvent) => {
      if (!sliderRef.value) return
      const rect = sliderRef.value.getBoundingClientRect()
      const percent = Math.max(0, Math.min(100, ((e.clientX - rect.left) / rect.width) * 100))
      updateValue(percent)
    }

    const handleMouseUp = () => {
      isDragging.value = false
      if (props.updateOnDragEnd) {
        emit('update:modelValue', tempValue.value)
        emit('change', tempValue.value)
      }
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseup', handleMouseUp)
    }

    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mouseup', handleMouseUp)
  }
</script>
