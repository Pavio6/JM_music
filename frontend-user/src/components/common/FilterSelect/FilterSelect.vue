<template>
  <div class="filter-select" @click.stop>
    <div class="flex flex-row items-center gap-2 flex-wrap">
      <Button class="flex-shrink-0" @click="toggleDropdown" inline>
        {{ t('common.filter.select') }}
        <span :class="['arrow', { open: isOpen }]">▼</span>
      </Button>
      <SelectedTags :selected="selectedTags" :tags="tags" @remove="removeTag" />
    </div>
    <div v-if="isOpen" class="dropdown-content">
      <DropdownTagSelect
        :tags="tags"
        :modelValue="selectedTags"
        @update:modelValue="handleSelect"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, onBeforeUnmount } from 'vue'
  import DropdownTagSelect from './DropdownTagSelect.vue'
  import type { TagSelectTag } from './DropdownTagSelect.vue'
  import SelectedTags from './SelectedTags.vue'
  import Button from '../Button.vue'
  import { toast } from '@/utile'
  import { useI18n } from 'vue-i18n'

  const props = defineProps<{
    tags: TagSelectTag[]
    maxlength?: number
  }>()

  const { t } = useI18n()
  const emit = defineEmits(['change'])
  const selectedTags = defineModel<(string | number)[]>('modelValue', {
    default: () => []
  })

  const isOpen = ref(false)

  function toggleDropdown(e: Event) {
    e.stopPropagation()
    isOpen.value = !isOpen.value
  }

  function handleClickOutside() {
    isOpen.value = false
  }

  onMounted(() => {
    document.addEventListener('click', handleClickOutside)
  })
  onBeforeUnmount(() => {
    document.removeEventListener('click', handleClickOutside)
  })

  function removeTag(val: string | number) {
    selectedTags.value = selectedTags.value.filter((v) => v !== val)
  }

  function handleSelect(val: (string | number)[]) {
    if (props.maxlength && val.length > props.maxlength) {
      toast.warning(t('common.maxLength', { maxlength: props.maxlength }))
      return
    }
    selectedTags.value = val
  }
</script>

<style scoped>
  .filter-select {
    position: relative;
    display: inline-block;
  }

  .arrow {
    font-size: 12px;
    transition: transform 0.2s;
  }
  .arrow.open {
    transform: rotate(180deg);
  }
  .dropdown-content {
    position: absolute;
    left: 0;
    top: 100%;
    z-index: 10;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    margin-top: 4px;
  }
</style>
