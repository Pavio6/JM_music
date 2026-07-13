<template>
  <div class="inventory-container w-full">
    <div class="section-header flex justify-between items-center mb-4">
      <h2 class="text-xl font-bold text-white">{{ title }}</h2>
      <div class="flex items-center gap-2">
        <div
          v-if="showMore"
          class="text-blue-500 hover:underline ml-4 cursor-pointer"
          @click="
            () => {
              moreFnc && moreFnc()
            }
          "
        >
          {{ moreText }}
        </div>
        <div class="p-2 rounded-full hover:bg-gray-700 cursor-pointer" @click="refreshList('prev')">
          <Icon icon="mdi:chevron-left" class="text-gray-400 text-size-2xl" />
        </div>
        <div class="p-2 rounded-full hover:bg-gray-700 cursor-pointer" @click="refreshList('next')">
          <Icon icon="mdi:chevron-right" class="text-gray-400 text-size-2xl" />
        </div>
      </div>
    </div>

    <template v-if="data.length === 0">
      <div class="text-gray-400">{{ emptyText }}</div>
    </template>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
      <Card
        v-for="item in data"
        :key="item.id"
        class="cursor-pointer"
        @click="handleCardClick(item)"
      >
        <div class="relative">
          <Image
            :src="item.cover"
            :alt="item.title"
            type="album"
            class="w-full h-40 object-cover rounded-lg mb-3"
          />
          <span
            v-if="item.tag"
            class="absolute top-2 left-2 bg-blue-600 text-white px-2 py-1 rounded text-sm"
          >
            {{ item.tag }}
          </span>
        </div>
        <div class="flex flex-col">
          <p class="text-gray-400 text-sm mb-1">{{ item.subTitle }}</p>
          <h3 class="text-white font-medium mb-2">{{ item.title }}</h3>
        </div>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  defineOptions({
    name: 'Inventory'
  })

  const props = defineProps({
    searchParams: {
      type: Object,
      default: {}
    },
    title: {
      type: String,
      default: ''
    },
    showMore: {
      type: Boolean,
      default: false
    },
    moreText: {
      type: String,
      default: ''
    },
    emptyText: {
      type: String,
      default: ''
    },
    moreFnc: {
      type: Function,
      default: () => {}
    },
    data: {
      type: Array as () => ListItem[],
      default: () => []
    },
    totalPages: {
      type: Number,
      default: 0
    }
  })

  const moreText = computed(() => props.moreText || t('common.more'))
  const emptyText = computed(() => props.emptyText || t('common.noData'))

  interface ListItem {
    id: number | string
    title: string
    subTitle?: string
    cover: string
    tag?: string
    [key: string]: any
  }

  const emits = defineEmits<{
    (e: 'onItemClick', item: ListItem): void
    (e: 'onPageClick', pageNum: number): void
  }>()

  const pageNum = ref(1)
  const refreshList = (type: 'prev' | 'next') => {
    if (type === 'prev') {
      if (pageNum.value === 1) return
      pageNum.value--
      emits('onPageClick', pageNum.value)
      return
    }
    if (type === 'next') {
      if (pageNum.value >= props.totalPages) return
      pageNum.value++
      emits('onPageClick', pageNum.value)
      return
    }
  }

  const handleCardClick = (item: ListItem) => {
    emits('onItemClick', item)
  }
</script>

<style scoped></style>
