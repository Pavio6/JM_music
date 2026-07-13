<template>
  <div class="dropdown-tag-select">
    <Card class="dropdown" noPadding>
      <div
        v-for="item in tags"
        :key="item.value"
        class="dropdown-item"
        @mouseenter="hovered = item.value"
        @mouseleave="hovered = null"
      >
        <div :class="['tagLi', isLevel1Active(item)]" @click="onLevel1Click(item)">
          {{ item.label }}
        </div>
        <Card v-if="item.children && hovered === item.value" class="dropdown-children" noPadding>
          <div
            v-for="child in item.children"
            :key="child.value"
            :class="['tagLi', modelValue.includes(child.value) ? 'active' : '']"
            @click.stop="onLevel2Click(item, child)"
          >
            {{ child.label }}
          </div>
        </Card>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
  import { ref, defineProps, defineEmits } from 'vue'
  import Card from '../Card.vue'

  export interface TagSelectTag {
    label: string
    value: string | number
    children?: TagSelectTag[]
  }

  const props = defineProps<{
    tags: TagSelectTag[]
    modelValue: (string | number)[]
  }>()

  const emit = defineEmits(['update:modelValue'])

  const hovered = ref<string | number | null>(null)

  function isLevel1Active(item: TagSelectTag) {
    if (item.children) {
      return item.children.some((child) => props.modelValue.includes(child.value)) ? 'active' : ''
    } else {
      return props.modelValue.includes(item.value) ? 'active' : ''
    }
  }

  function onLevel1Click(item: TagSelectTag) {
    if (item.children) return
    let newValue
    if (props.modelValue.includes(item.value)) {
      newValue = props.modelValue.filter((v) => v !== item.value)
    } else {
      newValue = [...props.modelValue, item.value]
    }
    emit('update:modelValue', newValue)
  }

  function onLevel2Click(parent: TagSelectTag, child: TagSelectTag) {
    let newValue
    if (props.modelValue.includes(child.value)) {
      newValue = props.modelValue.filter((v) => v !== child.value)
    } else {
      newValue = [...props.modelValue, child.value]
    }
    emit('update:modelValue', newValue)
  }
</script>

<style scoped>
  .dropdown-tag-select {
    position: relative;
    display: inline-block;
  }
  .dropdown {
    padding: 8px;
    min-width: 120px;
  }
  .dropdown-item {
    position: relative;
  }
  .tagLi {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 16px;
    cursor: pointer;
    transition: all 0.2s;
    user-select: none;
    margin-bottom: 4px;
    &.active {
      background: #2563eb;
      color: #fff;
    }
  }
  .tag {
    display: inline-block;
    padding: 4px 12px;
    border-radius: 16px;
    background: #f3f4f6;
    color: #333;
    cursor: pointer;
    margin-right: 8px;
    transition: all 0.2s;
    user-select: none;
  }
  .tag.active {
    background: #2563eb;
    color: #fff;
  }
  .dropdown-children {
    position: absolute;
    left: 100%;
    top: 0;
    padding: 8px;
    min-width: 120px;
    z-index: 10;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }
</style>
