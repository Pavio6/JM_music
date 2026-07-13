<template>
  <div class="user-detail space-y-8">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <!-- 个人信息卡片 -->
    <Card class="user-info-card flex items-center justify-between p-6">
      <div class="flex items-center gap-6">
        <Image
          type="avatar"
          :src="userInfo?.userAvatar"
          alt="用户头像"
          class="w-20 h-20 rounded-full object-cover"
        />
        <div>
          <div class="text-xl font-bold">
            {{ userInfo?.userName || t('common.userInfoNotVisible') }}
          </div>
          <div class="text-gray-500 mb-2">{{ userInfo?.userBio }}</div>
          <div class="flex gap-4 text-gray-400">
            <span class="cursor-pointer" @click="navigateToFollow('follow')"
              >{{ t('common.follow') }}：{{ userInfo?.followStatsVo?.followCount || 0 }}</span
            >
            <span class="cursor-pointer" @click="navigateToFollow('fans')"
              >{{ t('common.fans') }}：{{ userInfo?.followStatsVo?.followerCount || 0 }}</span
            >
          </div>
        </div>
      </div>
      <div class="flex gap-4">
        <Button v-if="userInfo && userInfo.isFollowed" @click="handleFollow">
          <Icon :icon="userInfo.isFollowed ? 'mdi:heart' : 'mdi:heart-outline'" class="mr-1" />
          {{ userInfo.isFollowed ? t('common.followed') : t('common.follow') }}
        </Button>
        <Button v-if="userInfo" @click="handleSendMsg"> {{ t('common.privateMessage') }} </Button>
      </div>
    </Card>

    <!-- 歌曲列表 -->
    <template v-if="songListVisible">
      <Card size="lg">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold">{{ t('common.favoriteSongsList') }}</h2>
          <div class="text-gray-600">
            {{
              t('common.totalSong', {
                total: SongList?.length || 0
              })
            }}
          </div>
        </div>
        <Table :columns="columns" :data="SongList">
          <template #cell-index="{ index }">
            {{ index + 1 }}
          </template>
          <template #cell-songName="{ row }">
            <div class="flex items-center gap-3">
              <Image
                type="song"
                :src="row.songCover"
                :alt="row.songName"
                class="w-10 h-10 rounded"
              />
              <router-link :to="`/song/${row.songId}`" class="text-blue-600 hover:text-blue-800">
                {{ row.songName }}
              </router-link>
              <!-- MV -->
              <template v-if="row.mvId">
                <Icon
                  icon="mingcute:video-line"
                  class="text-blue-600 cursor-pointer"
                  @click="handlePlayVideo(row.mvId)"
                />
              </template>
            </div>
          </template>
          <template #cell-singerName="{ row }">
            <router-link :to="`/singer/${row.singerId}`" class="text-blue-600 hover:text-blue-800">
              {{ row.singerName }}
            </router-link>
          </template>
          <template #cell-albumName="{ row }">
            <router-link :to="`/album/${row.albumId}`" class="text-blue-600 hover:text-blue-800">
              {{ row.albumName }}
            </router-link>
          </template>
          <template #cell-duration="{ row }">
            {{ row.songDuration }}
          </template>
          <template #cell-actions="{ row }">
            <div class="flex flex-row space-x-2">
              <Button @click="addToPlaylist(row, true)" inline>
                <Icon icon="famicons:play" />
              </Button>
              <Button @click="addToPlaylist(row, false)" inline>
                <Icon icon="mdi:playlist-plus" />
              </Button>
            </div>
          </template>
        </Table>
      </Card>
    </template>
    <template v-else>
      <Card size="lg">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold">{{ t('common.favoriteSongsList') }}</h2>
          <div class="text-gray-600">{{ t('common.playlistNotVisible') }}</div>
        </div>
      </Card>
    </template>

    <!-- 歌单列表 -->
    <template v-if="playlistVisible">
      <Card v-if="PlayList?.length" size="lg">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold">{{ t('common.favoritePlaylistsList') }}</h2>
        </div>
        <div class="grid grid-cols-5 gap-4">
          <div
            v-for="playlist in PlayList"
            :key="playlist.playlistId"
            class="hover:shadow-lg transition-shadow cursor-pointer"
            @click="navigateToPlaylist(playlist.playlistId || '')"
          >
            <Image
              type="playlist"
              :src="playlist.playlistCover"
              :alt="playlist.playlistName"
              class="w-full aspect-square object-cover"
            />
            <div class="p-4">
              <div class="font-semibold text-base mb-1">{{ playlist.playlistName }}</div>
            </div>
          </div>
        </div>
      </Card>
    </template>
    <template v-else>
      <Card size="lg">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold">{{ t('common.favoritePlaylistsList') }}</h2>
          <div class="text-gray-600">{{ t('common.playlistNotVisible') }}</div>
        </div>
      </Card>
    </template>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, inject, computed } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { Card, Button } from '@/components/common'
  import { getUserInfo } from '@/api/modules/user/user'
  import { toast } from '@/utile'
  import type { UserInfo } from '@/api/modules/user/user.d'
  import { followSingerOrUser } from '@/api/modules/singer/signer'
  import { FollowType } from '@/api/modules/singer/singer.d'
  import Table from '@/components/common/Table.vue'
  import type { Song } from '@/api/modules/music/music.d'
  import { useMusicStore } from '@/stores/music'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const route = useRoute()
  const router = useRouter()
  const back = () => {
    router.back()
  }

  const userInfo = ref<Partial<UserInfo>>({})
  const userInfoVisible = ref(true)
  const songListVisible = ref(true)
  const playlistVisible = ref(true)

  const columns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.songName') },
    { key: 'singerName', title: t('common.singerName') },
    { key: 'albumName', title: t('common.albumName') },
    { key: 'duration', title: t('common.duration') },
    { key: 'actions', title: t('common.actions') }
  ]

  const fetchUserDetail = async () => {
    try {
      const res = await getUserInfo(Number(route.params.id))
      if (!res.data) {
        userInfoVisible.value = false
        userInfo.value = {}
        return
      }
      userInfo.value = res.data
      // 歌曲可见
      songListVisible.value = res.data?.favoriteSongsList?.length > 0
      // 歌单可见
      playlistVisible.value = res.data?.playlistVisibility === 'PUBLIC'
    } catch (e) {
      userInfoVisible.value = false
      userInfo.value = {}
    }
  }

  const SongList = computed(() => {
    return userInfo.value?.favoriteSongsList || []
  })

  const PlayList = computed(() => {
    return userInfo.value?.favoritePlaylistsList || []
  })

  const handleFollow = async () => {
    const followedId = userInfo.value.userId
    if (!followedId) {
      return
    }
    const isFollow = !userInfo.value.isFollowed
    const followType = FollowType.User
    try {
      await followSingerOrUser({
        followedId,
        isFollow,
        followType
      }).then((res) => {
        if (res.data) {
          userInfo.value.isFollowed = !userInfo.value.isFollowed
          toast.success(
            userInfo.value.isFollowed ? t('common.followSuccess') : t('common.unfollowSuccess')
          )
        } else {
          toast.error(t('common.operationFailed'))
        }
      })
    } catch (error) {
      toast.error(t('common.operationFailed'))
    }
  }

  const handleSendMsg = () => {
    if (userInfo.value.messagePermission) {
      if (userInfo.value.messagePermission === 'FOLLOWERS' && !userInfo.value.isFollowed) {
        toast.info(t('common.pleaseFollowTheUser'))
      } else if (userInfo.value.messagePermission === 'ALL') {
        router.push({
          path: `/message/${userInfo.value.userId}`
        })
      }
    }
  }

  function navigateToFollow(type: 'follow' | 'fans') {
    if (!userInfo.value) {
      return
    }
    if (type === 'follow') {
      if (userInfo.value.followingVisibility === 'PRIVATE') {
        toast.info(t('common.userPrivacySetting'))
        return
      }
      if ((userInfo.value.followStatsVo?.followCount ?? 0) < 1) {
        return
      }
    }
    if (type === 'fans') {
      if (userInfo.value.followersVisibility === 'PRIVATE') {
        toast.info(t('common.userPrivacySetting'))
        return
      }
      if ((userInfo.value.followStatsVo?.followerCount ?? 0) < 1) {
        return
      }
    }
    router.push(`/follow/${type}/${userInfo.value.userId}`)
  }

  function navigateToPlaylist(playlistId?: number | string) {
    if (!playlistId) {
      return
    }
    router.push(`/playlist/${playlistId}`)
  }

  const musicStore = useMusicStore()

  const addToPlaylist = (song: Song, playNow = false) => {
    musicStore.addToPlaylist(song, playNow)
  }

  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }

  onMounted(async () => {
    await fetchUserDetail()
  })
</script>

<style scoped></style>
