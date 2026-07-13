<template>
  <div class="user-page space-y-6">
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 用户头像 -->
        <div class="avatar-section">
          <Image
            type="avatar"
            :src="userInfo.userAvatar"
            :alt="userInfo.userName"
            class="w-32 h-32 rounded-full object-cover"
          />
        </div>
        <!-- 用户基本信息 -->
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-2">{{ userInfo.userName }}</h1>
          <p class="text-gray-600 mb-4">{{ userInfo.userBio || t('common.noUserBio') }}</p>
          <div class="user-stats flex gap-6 text-gray-700">
            <div class="stat-item cursor-pointer" @click="navigateToFollow('follow')">
              <div class="font-medium">{{ t('common.follow') }}</div>
              <div class="text-xl">{{ followStats.followCount || 0 }}</div>
            </div>
            <div class="stat-item cursor-pointer" @click="navigateToFollow('fans')">
              <div class="font-medium">{{ t('common.fans') }}</div>
              <div class="text-xl">{{ followStats.followerCount || 0 }}</div>
            </div>
          </div>
        </div>
        <!-- 编辑按钮 -->
        <Button @click="openEditModal">{{ t('common.edit') }}</Button>
      </div>
    </Card>

    <div class="flex flex-row gap-6">
      <Card size="lg" class="flex-1">
        <h2 class="text-xl font-bold mb-4">{{ t('common.favoriteSingers') }}</h2>
        <div class="flex mb-6">
          <template v-if="favoriteSingersTotal > 0">
            <div class="flex flex-row items-center gap-3">
              <div>
                {{
                  t('common.totalSinger', {
                    total: favoriteSingersTotal
                  })
                }}
              </div>
              <Button rounded @click="prevFavoriteSingerPage">
                <Icon icon="mingcute:left-fill" />
              </Button>
              <Button rounded @click="nextFavoriteSingerPage">
                <Icon icon="mingcute:right-fill" />
              </Button>
            </div>
          </template>
        </div>
        <div class="grid grid-cols-4 gap-4">
          <template v-if="favoriteSingers.length">
            <div
              v-for="singer in favoriteSingers"
              :key="singer.id"
              class="cursor-pointer transition-shadow space-y-2"
              @click="navigateToSinger(singer.id)"
            >
              <Image
                type="singer"
                :src="singer.cover"
                :alt="singer.name"
                class="w-full aspect-square object-cover rounded-lg"
              />
              <h3 class="font-medium truncate">{{ singer.name }}</h3>
            </div>
          </template>
          <template v-else>
            <div style="text-align: center; color: #999; padding: 40px 0; font-size: 16px">
              {{ t('common.noFavoriteSingers') }}
            </div>
          </template>
        </div>
      </Card>
      <!-- 隐私设置 -->
      <Card size="lg" class="flex-shrink-0">
        <Privacy />
      </Card>
    </div>

    <!-- 收藏的歌单 -->
    <Card size="lg">
      <div class="flex flex-row justify-between">
        <h2 class="text-xl font-bold mb-4">{{ t('common.myPlaylists') }}</h2>
        <div class="flex">
          <Button type="primary" @click="openCreatePlaylistModal">
            <Icon icon="mdi:play" class="mr-1" />
            {{ t('common.createPlaylist') }}
          </Button>
        </div>
      </div>
      <div class="flex mb-6">
        <template v-if="myPlaylistsTotal > 0">
          <div class="flex flex-row items-center gap-3">
            <div>
              {{
                t('common.totalPlaylist', {
                  total: myPlaylistsTotal
                })
              }}
            </div>
            <Button rounded @click="prevMyPage">
              <Icon icon="mingcute:left-fill" />
            </Button>
            <Button rounded @click="nextMyPage">
              <Icon icon="mingcute:right-fill" />
            </Button>
          </div>
        </template>
      </div>
      <!-- 个人歌单 -->
      <div class="grid grid-cols-1 md:grid-cols-5 gap-6">
        <template v-if="myPlaylistsTotal > 0">
          <Card
            v-for="playlist in myPlaylists"
            :key="playlist.id"
            class="cursor-pointer transition-shadow space-y-2"
            no-padding
            @click="navigateToMyPlaylist(playlist.id)"
          >
            <Image
              type="playlist"
              :src="playlist.cover"
              :alt="playlist.name"
              class="w-full aspect-square object-cover rounded-lg"
            />
            <h3 class="font-medium truncate">{{ playlist.name }}</h3>
          </Card>
        </template>
        <template v-else>
          <div style="text-align: center; color: #999; padding: 40px 0; font-size: 16px">
            {{ t('common.noMyPlaylists') }}
          </div>
        </template>
      </div>
    </Card>

    <!-- 编辑资料弹窗 -->
    <Modal ref="userInfoModalRef">
      <Info @close="closeEditModal" />
    </Modal>

    <!-- 自建弹窗 -->
    <Modal ref="createPlaylistModalRef">
      <form @submit.prevent="handleCreatePlaylist" class="space-y-4 p-4 w-full">
        <div>
          <Input
            v-model="newPlaylist.name"
            :placeholder="t('common.playlistName')"
            required
            :maxlength="20"
          />
        </div>
        <div class="flex justify-end gap-2 mt-4">
          <Button type="default" @click="closeCreatePlaylistModal">{{ t('common.cancel') }}</Button>
          <Button
            type="primary"
            native-type="submit"
            @click="handleCreatePlaylist"
            :loading="loading"
            >{{ t('common.create') }}</Button
          >
        </div>
      </form>
    </Modal>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { storeToRefs } from 'pinia'
  import { useUserStore } from '@/stores/user'
  import { toast } from '@/utile'
  import { getFollowStats, getMineFollowList } from '@/api/modules/user/user'
  import Privacy from '@/components/user/Privacy.vue'
  import Info from '@/components/user/Info.vue'
  import type { Modal } from '@/components/common'
  import { createPlayList, getMinePlayList } from '@/api/modules/playList/playList'
  import type { FollowStats } from '@/api/modules/user/user.d'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  interface PlayList {
    id: number | string
    cover: string
    name: string
  }

  const { userInfo } = storeToRefs(useUserStore())
  const router = useRouter()

  // 编辑弹窗控制
  const userInfoModalRef = ref<InstanceType<typeof Modal> | null>(null)

  // 用户统计数据
  const followStats = ref<FollowStats>({
    followerCount: 0,
    followCount: 0
  })

  // 关注的歌手
  const favoriteSingers = ref<{ cover: string; id: number; name: string }[]>([])
  const favoriteSingersTotal = ref(0)
  const favoriteSingersPageNum = ref(1)
  const favoriteSingersPageSize = ref(4)
  function fetchFavoriteSingers() {
    getMineFollowList({
      type: 'SINGER',
      pageNum: favoriteSingersPageNum.value,
      pageSize: favoriteSingersPageSize.value
    }).then((res) => {
      favoriteSingers.value = res.data.records
      favoriteSingersTotal.value = res.data.total
    })
  }

  function prevFavoriteSingerPage() {
    if (favoriteSingersPageNum.value === 1) return
    favoriteSingersPageNum.value--
    fetchFavoriteSingers()
  }

  function nextFavoriteSingerPage() {
    if (
      favoriteSingersPageNum.value * favoriteSingersPageSize.value >=
      favoriteSingersTotal.value
    ) {
      return
    }
    favoriteSingersPageNum.value++
    fetchFavoriteSingers()
  }

  function openEditModal() {
    userInfoModalRef.value?.open({
      title: t('common.editUserInfo'),
      showFoot: false
    })
  }

  function closeEditModal(formData: any) {
    userInfoModalRef.value?.close()
  }

  function navigateToMyPlaylist(playlistId: number | string) {
    router.push({ path: `/playlist/my/${playlistId}` })
  }

  function navigateToSinger(singerId: number | string) {
    router.push({ path: `/singer/${singerId}` })
  }

  function navigateToFollow(type: 'follow' | 'fans') {
    if (type === 'follow' && followStats.value.followCount < 1) {
      return
    }
    if (type === 'fans' && followStats.value.followerCount < 1) {
      return
    }
    router.push(`/follow/${type}`)
  }

  async function fetchUserStats() {
    try {
      getFollowStats(userInfo.value.userId).then((res) => {
        followStats.value = res.data
      })
    } catch (error) {
      console.error('获取用户统计数据失败:', error)
    }
  }

  const userStore = useUserStore()

  // #region 获取自建歌单
  const myPlaylists = ref<PlayList[]>([])
  const myPlaylistsTotal = ref(0)
  const myPlaylistsPageNum = ref(1)
  const myPlaylistsPageSize = ref(4)

  const prevMyPage = () => {
    if (myPlaylistsPageNum.value === 1) return
    myPlaylistsPageNum.value--
    refreshMyPlaylists()
  }

  const nextMyPage = () => {
    if (myPlaylistsPageNum.value * myPlaylistsPageSize.value >= myPlaylistsTotal.value) {
      return
    }
    myPlaylistsPageNum.value++
    refreshMyPlaylists()
  }

  const refreshMyPlaylists = () => {
    getMinePlayList({
      pageNum: myPlaylistsPageNum.value,
      pageSize: myPlaylistsPageSize.value
    })
      .then((res) => {
        myPlaylists.value = res.data.records.map((playlist) => {
          return {
            id: playlist.playlistId,
            cover: playlist.playlistCover,
            name: playlist.playlistName
          }
        })
        myPlaylistsTotal.value = res.data.total
      })
      .catch((err) => {
        console.log('🚀 ~ err:', err)
        toast.error(t('common.getMyPlaylistsFailed'))
      })
  }
  // #endregion

  // 创建歌单弹窗逻辑
  const createPlaylistModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const newPlaylist = ref({ name: '', desc: '' })
  const loading = ref(false)
  function openCreatePlaylistModal() {
    createPlaylistModalRef.value?.open({
      title: t('common.createFavoritePlaylist'),
      width: '40vw',
      showConfirm: false,
      showFoot: false
    })
  }

  function closeCreatePlaylistModal() {
    createPlaylistModalRef.value?.close()
    newPlaylist.value = { name: '', desc: '' }
  }

  function handleCreatePlaylist() {
    if (!newPlaylist.value.name) return
    loading.value = true
    createPlayList({
      playlistName: newPlaylist.value.name
    })
      .then((res) => {
        if (res.data) {
          refreshMyPlaylists()
          closeCreatePlaylistModal()
          toast.success(t('common.createSuccess'))
        } else {
          toast.error(t('common.createFailed'))
        }
      })
      .finally(() => {
        loading.value = false
      })
  }

  onMounted(() => {
    fetchUserStats()
    refreshMyPlaylists()
    fetchFavoriteSingers()
  })
</script>

<style lang="scss" scoped></style>
