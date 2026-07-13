<template>
  <div class="bg-gray-800 rounded-xl border-gray-700">
    <div class="flex flex-row items-center flex-1">
      <textarea
        :type="type"
        :value="modelValue"
        @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)"
        :placeholder="placeholder"
        :name="name"
        :disabled="disabled"
        :required="required"
        :maxlength="maxlength > 0 ? maxlength : undefined"
        class="w-full bg-gray-800 text-white placeholder-gray-400 rounded-lg p-4 focus:ring-2 focus:ring-blue-500 appearance-none border-none"
        :style="{ maxHeight: maxHeight }"
        v-bind="$attrs"
      />
    </div>
    <div v-if="maxlength > 0" class="text-right text-xs text-gray-400 select-none px-2">
      {{ (modelValue || '').length }} / {{ maxlength }}
    </div>
  </div>
</template>

<script setup lang="ts">
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
    maxHeight: {
      type: String,
      default: '100px'
    },
    maxlength: {
      type: Number,
      default: 0
    }
  })

  defineEmits(['update:modelValue'])
</script>

<style lang="scss" scoped>
  textarea,
  textarea:focus {
    outline: none;
    border: 0;
    outline-color: transparent;
    box-shadow: 0 0 0 2px rgba(0, 0, 0, 0);
    background-color: rgba(0, 0, 0, 0);
  }
</style>
