<template>
  <div class="follow-page space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-center justify-between mb-4">
        <h1 class="text-2xl font-bold">{{ title }}</h1>
      </div>

      <!-- 标签切换 -->
      <div class="tabs-container mb-6">
        <div class="flex border-b">
          <div
            v-for="tab in tabs.filter((tab) => !(type === 'fans' && tab.value === 'SINGER'))"
            :key="tab.value"
            class="tab-item px-6 py-3 cursor-pointer"
            :class="{ 'active-tab': activeTab === tab.value }"
            @click="handleTabChange(tab.value)"
          >
            {{ tab.label }}
          </div>
        </div>
      </div>

      <!-- 用户列表 -->
      <div class="user-list grid grid-cols-1 md:grid-cols-8 gap-4">
        <div
          v-for="user in userList"
          :key="user.id"
          class="user-card flex flex-col items-center space-y-3 p-4 rounded-lg transition-colors cursor-pointer"
          @click="navigateTo(user.id)"
        >
          <Image
            :src="user.cover"
            type="avatar"
            :alt="user.name"
            class="w-20 h-20 rounded-full object-cover"
          />
          <div class="user-info text-center">
            <h3 class="font-medium text-base">{{ user.name }}</h3>
          </div>
        </div>
      </div>

      <!-- 分页控制 -->
      <div class="pagination flex justify-between items-center mt-6">
        <div>
          {{
            t('common.totalMessage', {
              total: total,
              type: activeTab === 'SINGER' ? t('common.singer') : t('common.users')
            })
          }}
        </div>
        <div class="flex items-center gap-3">
          <Button rounded @click="prevPage" :disabled="pageNum <= 1">
            <Icon icon="mingcute:left-fill" />
          </Button>
          <span>{{ pageNum }}/{{ Math.ceil(total / pageSize) }}</span>
          <Button rounded @click="nextPage" :disabled="pageNum * pageSize >= total">
            <Icon icon="mingcute:right-fill" />
          </Button>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted, watch } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { storeToRefs } from 'pinia'
  import { useUserStore } from '@/stores/user'
  import { toast } from '@/utile'
  import { Card, Button } from '@/components/common'
  import {
    getMineFollowList,
    getMineFanList,
    getUserFollowList,
    getUserFanList,
    getUserInfo
  } from '@/api/modules/user/user'
  import type { FollowListQueryType, UserInfo } from '@/api/modules/user/user.d'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const route = useRoute()
  const router = useRouter()

  const back = () => {
    router.back()
  }

  const { userInfo } = storeToRefs(useUserStore())

  // 获取路由参数
  const type = computed(() => route.params.type as string)
  const userId = computed(() => Number(route.params.userId) || userInfo.value.userId)
  const isMine = computed(
    () => !route.params.userId || Number(route.params.userId) === userInfo.value.userId
  )

  // 用户名称（当查看他人关注列表时使用）
  const userName = ref('ta')
  const queryUserInfo = ref<Partial<UserInfo>>({})

  // 页面标题
  const title = computed(() => {
    if (type.value === 'fans') {
      return isMine.value
        ? t('common.myFans')
        : t('common.whoFan', {
            name: userName.value
          })
    } else {
      return isMine.value
        ? t('common.myFollow')
        : t('common.whoFollow', {
            name: userName.value
          })
    }
  })

  // 标签页配置
  const tabs: {
    label: string
    value: (typeof FollowListQueryType)[keyof typeof FollowListQueryType]
  }[] = [
    { label: t('common.users'), value: 'USER' },
    { label: t('common.singer'), value: 'SINGER' }
  ]

  // 当前激活的标签
  const activeTab = ref<(typeof FollowListQueryType)[keyof typeof FollowListQueryType]>('USER')

  // 分页参数
  const pageNum = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  // 用户列表
  const userList = ref<{ cover: string; id: number; name: string }[]>([])

  // 已关注的用户ID列表（用于前端展示关注状态）
  const followedIds = ref<number[]>([])

  // 加载数据
  const loadData = async () => {
    try {
      if (type.value === 'fans') {
        // 加载粉丝列表
        if (isMine.value) {
          const res = await getMineFanList({
            pageNum: pageNum.value,
            pageSize: pageSize.value
          })
          userList.value = res.data?.records ?? []
          total.value = res.data?.total ?? 0
        } else {
          getUserInfo(userId.value).then((res) => {
            if (res.data) {
              queryUserInfo.value = res.data
              userName.value = res.data?.userName ?? 'ta'
            }
          })
          const res = await getUserFanList({
            userId: userId.value,
            type: activeTab.value,
            pageNum: pageNum.value,
            pageSize: pageSize.value
          })
          userList.value = res.data?.records ?? []
          total.value = res.data?.total ?? 0
        }
      } else {
        // 加载关注列表
        if (isMine.value) {
          const res = await getMineFollowList({
            type: activeTab.value,
            pageNum: pageNum.value,
            pageSize: pageSize.value
          })
          userList.value = res.data?.records ?? []
          total.value = res.data?.total ?? 0
        } else {
          getUserInfo(userId.value).then((res) => {
            if (res.data) {
              queryUserInfo.value = res.data
              userName.value = res.data?.userName ?? 'ta'
            }
          })
          const res = await getUserFollowList({
            userId: userId.value,
            type: activeTab.value,
            pageNum: pageNum.value,
            pageSize: pageSize.value
          })
          userList.value = res.data?.records ?? []
          total.value = res.data?.total ?? 0
        }
      }

      followedIds.value = []
    } catch (error) {
      toast.error(t('common.getFailed'))
      console.error(error)
    }
  }

  // 切换标签
  const handleTabChange = (tab: (typeof FollowListQueryType)[keyof typeof FollowListQueryType]) => {
    activeTab.value = tab
    pageNum.value = 1 // 重置页码
    loadData()
  }

  // 分页控制
  const prevPage = () => {
    if (pageNum.value > 1) {
      pageNum.value--
      loadData()
    }
  }

  const nextPage = () => {
    if (pageNum.value * pageSize.value < total.value) {
      pageNum.value++
      loadData()
    }
  }

  const navigateTo = (userId: number) => {
    if (!userId) {
      toast.error(t('common.getFailed'))
      return
    }
    if (userId === userInfo.value.userId) {
      return
    }
    console.log('🚀 ~ activeTab.value:', activeTab.value)
    if (activeTab.value === 'USER') {
      router.push({
        path: `/user/${userId}`
      })
    }
    if (activeTab.value === 'SINGER') {
      router.push({
        path: `/singer/${userId}`
      })
    }
  }

  // 监听路由参数变化
  watch([() => route.params.type, () => route.params.userId], () => {
    pageNum.value = 1 // 重置页码
    loadData()
  })

  // 初始化
  onMounted(() => {
    loadData()
  })
</script>

<style scoped>
  .active-tab {
    color: var(--primary-color);
    border-bottom: 2px solid var(--primary-color);
    font-weight: 500;
  }

  .followed {
    background-color: #f0f0f0;
    color: #666;
  }
</style>
