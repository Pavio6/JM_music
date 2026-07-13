<template>
  <select
    :value="modelValue"
    @input="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)"
    :class="[
      'bg-gray-800 rounded-lg p-4 border-gray-700 w-full text-white placeholder-gray-400 focus:ring-2 focus:ring-blue-500 appearance-none ',
      showBorder ? 'border border-gray-700 border-none-sing' : 'border-none-all'
    ]"
    v-bind="$attrs"
  >
    <slot></slot>
  </select>
</template>

<script setup lang="ts">
  defineProps<{
    modelValue: string | number
    showBorder?: boolean
  }>()

  defineEmits<{
    (e: 'update:modelValue', value: string | number): void
  }>()
</script>

<style lang="scss" scoped>
  select.border-none-all,
  select.border-none-all:focus {
    outline: none;
    border: 0;
    outline-color: transparent;
    box-shadow: 0 0 0 2px rgba(0, 0, 0, 0);
  }
  select.border-none-sing,
  select.border-none-sing:focus {
    outline: none;
    outline-color: transparent;
    // box-shadow: 0 0 0 2px rgba(0, 0, 0, 0);
  }
</style>
