<template>
  <div class="table-container">
    <table class="w-full">
      <thead class="text-left bg-blue/5">
        <tr>
          <th
            v-for="column in columns"
            :key="column.key"
            class="py-3 px-4 text-blue-600 hover:text-blue-500 font-semibold text-sm"
            :class="column.headerClass"
          >
            {{ column.title }}
          </th>
        </tr>
      </thead>
      <tbody>
        <!-- 如果数据为空，显示“暂无数据” -->
        <tr v-if="data.length === 0">
          <td colspan="100%" class="py-3 px-4 text-center text-gray-500">
            {{ t('common.table.noData') }}
          </td>
        </tr>

        <!-- 数据不为空时渲染数据行 -->
        <tr
          v-for="(item, index) in data"
          :key="index"
          class="border-b border-gray-100 hover:bg-blue/5 transition-colors"
        >
          <td
            v-for="column in columns"
            :key="column.key"
            class="py-3 px-4"
            :class="column.cellClass"
          >
            <slot :name="`cell-${column.key}`" :row="item" :index="index" :column="column">
              {{ item[column.key] }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  interface Column {
    key: string
    title: string
    headerClass?: string
    cellClass?: string
  }

  defineProps<{
    columns: Column[]
    data: any[]
  }>()
</script>

<style scoped>
  .table-container {
    overflow-x: auto;
  }
</style>
