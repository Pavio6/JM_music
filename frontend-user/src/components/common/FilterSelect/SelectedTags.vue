<template>
  <div class="flex flex-row flex-wrap gap-2">
    <Button v-for="tag in selectedTagObjects" :key="tag.value">
      {{ tag.label }}
      <span class="close" @click="$emit('remove', tag.value)">×</span>
    </Button>
  </div>
</template>

<script setup lang="ts">
  import { computed, defineProps } from 'vue'
  import type { TagSelectTag } from './DropdownTagSelect.vue'
  import Button from '../Button.vue'

  const props = defineProps<{
    selected: (string | number)[]
    tags: TagSelectTag[]
  }>()

  function findLabel(value: string | number, tags: TagSelectTag[]): string {
    for (const tag of tags) {
      if (tag.value === value) return tag.label
      if (tag.children) {
        const child = tag.children.find((c) => c.value === value)
        if (child) return child.label
      }
    }
    return String(value)
  }

  const selectedTagObjects = computed(() =>
    props.selected.map((val) => ({
      value: val,
      label: findLabel(val, props.tags)
    }))
  )
</script>

<style scoped>
  .close {
    margin-left: 4px;
    cursor: pointer;
    color: #888;
  }
</style>
