<template>
  <div class="flex flex-row items-center flex-1 bg-gray-800 rounded-xl border-gray-700">
    <Icon v-if="icon" :icon="icon" class="text-gray-400 text-size-3xl ml-2" />
    <input
      :type="type"
      :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
      :placeholder="placeholder"
      :name="name"
      :disabled="disabled"
      :required="required"
      :maxlength="maxlength || undefined"
      class="w-full bg-gray-800 text-white line-height-[1.2] placeholder-gray-400 rounded-lg p-4 focus:ring-2 focus:ring-blue-500 appearance-none border-none"
      v-bind="$attrs"
    />
    <div v-if="maxlength" class="flex-shrink-0 text-right text-xs text-gray-400 px-2 select-none">
      {{ (modelValue || '').length }} / {{ maxlength }}
    </div>
  </div>
</template>

<script setup lang="ts">
  import { Icon } from '@iconify/vue'

  defineOptions({
    name: 'Input'
  })

  defineProps({
    modelValue: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'text'
    },
    placeholder: {
      type: String,
      default: ''
    },
    icon: {
      type: String,
      default: ''
    },
    name: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    required: {
      type: Boolean,
      default: false
    },
    maxlength: {
      type: Number,
      default: undefined
    }
  })

  defineEmits(['update:modelValue'])
</script>

<style lang="scss" scoped>
  input,
  input:focus {
    outline: none;
    border: 0;
    outline-color: transparent;
    box-shadow: 0 0 0 2px rgba(0, 0, 0, 0);
    background-color: rgba(0, 0, 0, 0);
  }
</style>
