<template>
  <div class="flex items-center justify-center gap-2">
    <!-- 每页显示数量选择器 -->
    <div class="w-26">
      <Select v-model="currentPageSize" @change="handlePageSizeChange" class="px-4 py-2">
        <option v-for="size in pageSizes" :key="size" :value="size">
          {{ size }}{{ t('common.pagination.pageSize/page') }}
        </option>
      </Select>
    </div>

    <!-- 页码控制 -->
    <div class="flex items-center gap-1">
      <!-- 上一页 -->
      <Button
        @click="handlePrevPage"
        :disabled="currentPageNum <= 1"
        class="disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
        :class="[
          currentPageNum <= 1
            ? 'bg-white/10 text-white/30'
            : 'bg-white/10 text-white/60 hover:bg-white/20 hover:text-white'
        ]"
      >
        {{ t('common.prevPage') }}
      </Button>

      <!-- 页码 -->
      <div class="flex items-center gap-1">
        <Button
          v-for="page in displayPages"
          :key="page"
          @click="handlePageChange(page)"
          :type="page === currentPageNum ? 'primary' : 'default'"
          class="flex-shrink-0"
        >
          {{ page }}
        </Button>
      </div>

      <!-- 下一页 -->
      <Button
        @click="handleNextPage"
        :disabled="currentPageNum >= totalPages"
        class="disabled:opacity-50 disabled:cursor-not-allowed flex-shrink-0"
        :class="[
          currentPageNum >= totalPages
            ? 'bg-white/10 text-white/30'
            : 'bg-white/10 text-white/60 hover:bg-white/20 hover:text-white'
        ]"
      >
        {{ t('common.nextPage') }}
      </Button>
    </div>

    <!-- 总数显示 -->
    <span class="text-white/60 text-sm">
      {{ t('common.totalPage', { total: total }) }}
    </span>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch } from 'vue'
  import Select from './Select.vue'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  interface Props {
    total: number
    pageSize?: number
    currentPage?: number
    pageSizes?: number[]
  }

  const props = withDefaults(defineProps<Props>(), {
    pageSize: 20,
    currentPage: 1,
    pageSizes: () => [10, 20, 30, 50]
  })

  const emit = defineEmits<{
    (e: 'update:currentPage', page: number): void
    (e: 'update:pageSize', size: number): void
    (e: 'change', page: number, size: number): void
  }>()

  const currentPageNum = ref(props.currentPage)
  const currentPageSize = ref(props.pageSize)

  // 计算总页数
  const totalPages = computed(() => Math.ceil(props.total / currentPageSize.value))

  // 计算要显示的页码
  const displayPages = computed(() => {
    const pages: number[] = []
    const maxDisplayPages = 5 // 最多显示5个页码

    if (totalPages.value <= maxDisplayPages) {
      // 如果总页数小于等于最大显示页数，显示所有页码
      for (let i = 1; i <= totalPages.value; i++) {
        pages.push(i)
      }
    } else {
      // 否则，显示当前页码周围的页码
      let start = Math.max(currentPageNum.value - 2, 1)
      let end = Math.min(start + maxDisplayPages - 1, totalPages.value)

      // 调整start，确保显示maxDisplayPages个页码
      if (end - start + 1 < maxDisplayPages) {
        start = Math.max(end - maxDisplayPages + 1, 1)
      }

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
    }

    return pages
  })

  // 监听props变化
  watch(
    () => props.currentPage,
    (newVal) => {
      currentPageNum.value = newVal
    }
  )

  watch(
    () => props.pageSize,
    (newVal) => {
      currentPageSize.value = newVal
    }
  )

  // 页码变化处理
  const handlePageChange = (page: number) => {
    currentPageNum.value = page
    emit('update:currentPage', page)
    emit('change', page, currentPageSize.value)
  }

  // 上一页
  const handlePrevPage = () => {
    if (currentPageNum.value > 1) {
      handlePageChange(currentPageNum.value - 1)
    }
  }

  // 下一页
  const handleNextPage = () => {
    if (currentPageNum.value < totalPages.value) {
      handlePageChange(currentPageNum.value + 1)
    }
  }

  // 每页显示数量变化处理
  const handlePageSizeChange = () => {
    emit('update:pageSize', currentPageSize.value)
    emit('change', 1, currentPageSize.value) // 切换每页显示数量时，重置为第一页
  }
</script>
