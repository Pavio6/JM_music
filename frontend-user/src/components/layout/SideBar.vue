<template>
  <nav class="w-full h-full flex flex-col">
    <div class="mb-8">
      <h1 class="text-2xl font-bold">{{ t('AppName') }}</h1>
    </div>
    <div class="space-y-2">
      <div class="mb-4" v-for="route in routers" :key="route.path">
        <router-link
          :to="route.path"
          class="flex items-center p-3 rounded-lg transition duration-200 hover:bg-gray-700 text-white"
          active-class="bg-gray-700  border-blue-400 scale-[1.1]"
        >
          <div class="flex flex-row gap-2 items-center">
            <Icon v-if="route.icon" :icon="route.icon" class="text-xl" />
            <span>{{ t(route.name) }}</span>
          </div>
        </router-link>
      </div>
    </div>
    <div class="mt-auto">
      <div v-if="isLogin" class="flex flex-row gap-2 items-center">
        <Button rounded @click="userStore.logout" class="flex-grow">
          <Icon icon="line-md:logout" class="text-xl mr-1" />
          {{ t('common.logout') }}
        </Button>
        <Button rounded @click="handleFeedback">
          <Icon icon="codicon:feedback" class="text-xl mr-1" />
        </Button>
      </div>
    </div>
  </nav>
</template>

<script lang="ts" setup>
  import { useUserStore } from '@/stores/user'
  import { computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { storeToRefs } from 'pinia'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter()
  const userStore = useUserStore()
  const { isLogin } = storeToRefs(userStore)

  // 从路由配置中过滤需要在侧边栏显示的路由
  const routers = computed(() => {
    return router.options.routes
      .filter((route) => route.meta?.inSide)
      .map((route) => ({
        path: route.path,
        name: (route.meta?.title || route.name) as string,
        icon: route.meta?.icon
      }))
  })

  const handleFeedback = () => {
    router.push('/feedback')
  }
</script>

<style lang="scss" scoped>
  a {
    text-decoration: none;
  }
</style>
