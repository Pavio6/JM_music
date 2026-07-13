<template>
  <div class="flex items-center justify-between w-full">
    <!-- 搜索框 -->
    <Card :size="'sm'">
      <div class="flex items-center">
        <Input
          v-model="searchText"
          icon="mdi:magnify"
          :placeholder="t('common.search')"
          name="search"
          class="max-w-xl"
          @keyup.enter="handleSearch"
          autocomplete="false"
        />
      </div>
    </Card>

    <!-- 右侧控制区 -->
    <div class="flex items-center space-x-4 ml-4">
      <!-- 私信 -->
      <Card v-if="isLogin" :size="'sm'" @click="router.push('/message')">
        <div class="flex flex-row items-center cursor-pointer">
          <Icon icon="jam:messages" class="text-size-4xl" />
        </div>
      </Card>
      <!-- 国际化 -->
      <Card :size="'sm'">
        <div class="flex flex-row items-center cursor-pointer">
          <Icon
            v-if="locale === 'zh'"
            icon="circle-flags:cn"
            class="text-size-4xl transition-all duration-200"
            @click="setLang('en')"
          />
          <Icon
            v-if="locale === 'en'"
            icon="circle-flags:lang-en-us"
            class="text-size-4xl transition-all duration-200"
            @click="setLang('zh')"
          />
        </div>
      </Card>
      <!-- 用户信息 -->
      <Card :size="'sm'">
        <div class="flex items-center space-x-3 pl-4 cursor-pointer" @click="handleUserAvatar">
          <div class="flex flex-col items-end">
            <template v-if="isLogin">
              <span class="text-sm font-medium">{{ userInfo.userName }}</span>
              <span class="text-xs text-gray-400">{{ userInfo.userEmail }}</span>
            </template>
            <template v-else>
              <span class="text-sm font-medium">{{ t('common.guest') }}</span>
              <span class="text-xs text-gray-400">{{ t('common.pleaseLogin') }}</span>
            </template>
          </div>
          <template v-if="isLogin && userInfo.userAvatar">
            <Image
              :src="userInfo.userAvatar"
              type="avatar"
              :alt="t('common.userAvatar')"
              class="w-10 h-10 rounded-full bg-gray-700"
            />
          </template>
          <template v-else>
            <Icon icon="duo-icons:user" class="text-size-4xl" />
          </template>
        </div>
      </Card>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import { Icon } from '@iconify/vue'
  import { useUserStore } from '@/stores/user'
  import { useRouter } from 'vue-router'
  import { storeToRefs } from 'pinia'
  import Input from '@/components/common/Input.vue'
  import { useI18n } from 'vue-i18n'
  import localforage from 'localforage'

  defineOptions({
    name: 'HeadBar'
  })

  const { t } = useI18n()
  const { userInfo, isLogin } = storeToRefs(useUserStore())
  const router = useRouter()
  const searchText = ref('')

  const handleUserAvatar = () => {
    router.push({ path: '/user' })
  }

  // 处理搜索功能
  const handleSearch = () => {
    if (searchText.value.trim()) {
      router.push({
        path: '/search',
        query: { keyword: searchText.value.trim() }
      })
    }
  }

  // 国际化相关
  const { locale } = useI18n({ useScope: 'global' })
  const setLang = (lang: string) => {
    locale.value = lang
    localforage.setItem('lang', lang)
  }
</script>

<style lang="scss" scoped></style>
