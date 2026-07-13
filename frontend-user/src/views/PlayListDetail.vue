<template>
  <div class="playlist-detail space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 歌单封面 -->
        <div class="cover-section">
          <Image
            type="playlist"
            :src="playlistInfo?.playlistCover"
            :alt="playlistInfo?.playlistName"
            class="w-32 h-32 rounded-lg object-cover"
          />
        </div>
        <!-- 歌单信息 -->
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-2">{{ playlistInfo?.playlistName }}</h1>
          <div class="flex items-center gap-4 text-gray-600 mb-4">
            <div class="creator flex items-center gap-2">
              <Image
                type="playlist"
                :src="playlistInfo?.userAvatar"
                :alt="playlistInfo?.userName"
                class="w-6 h-6 rounded-full"
              />
              <span>{{ playlistInfo.userName }}</span>
            </div>
          </div>
          <p class="text-gray-600">{{ playlistInfo?.playlistBio || t('common.noPlaylistBio') }}</p>
          <div
            v-if="playlistInfo?.tagName && playlistInfo.tagName.length"
            class="flex flex-wrap gap-2 mt-2"
          >
            <span
              v-for="tag in playlistInfo.tagName"
              :key="tag"
              class="text-blue-600 text-xs cursor-auto"
              >#{{ tag }}</span
            >
          </div>
        </div>
        <div class="flex gap-4">
          <Button type="primary" @click="playAllSongs">
            <Icon icon="mdi:play" class="mr-1" />
            {{ t('common.playAll') }}
          </Button>
          <Button @click="toggleFavorite">
            <Icon
              :icon="playlistInfo.isCollected ? 'mdi:heart' : 'mdi:heart-outline'"
              class="mr-1"
            />
            {{ playlistInfo.isCollected ? t('common.collected') : t('common.collect') }}
          </Button>
        </div>
      </div>
    </Card>

    <!-- 歌曲列表 -->
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">{{ t('playList.songList') }}</h2>
        <div class="text-gray-600">
          {{ t('common.totalSongs', { total: playlistInfo?.songs?.length || 0 }) }}
        </div>
      </div>
      <Table :columns="columns" :data="songs">
        <template #cell-index="{ index }">
          {{ index + 1 }}
        </template>
        <template #cell-songName="{ row }">
          <div class="flex items-center gap-3">
            <Image type="song" :src="row.songCover" :alt="row.songName" class="w-10 h-10 rounded" />
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
      </Table>
    </Card>

    <!-- 评论模块 -->
    <Card size="lg">
      <Comment
        :targetId="Array.isArray(route.params.id) ? route.params.id[0] : route.params.id"
        targetType="PLAYLIST"
      />
    </Card>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import Card from '@/components/common/Card.vue'
  import Table from '@/components/common/Table.vue'
  import Comment from '@/components/common/Comment.vue'
  import { getPlayListDetail } from '@/api/modules/music/music'
  import type { PlayListDetail } from '@/api/modules/music/music.d'
  import { useMusicStore } from '@/stores/music'
  import { toast } from '@/utile'
  import { inject } from 'vue'
  import { addFavorite } from '@/api/modules/favorite/favorite'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const route = useRoute()
  const playlistInfo = ref<Partial<PlayListDetail>>({})
  const songs = ref<PlayListDetail['songs']>([])
  const musicStore = useMusicStore()

  const columns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.songName') },
    { key: 'singerName', title: t('common.singerName') },
    { key: 'albumName', title: t('common.albumName') },
    { key: 'duration', title: t('common.duration') }
  ]

  const router = useRouter()

  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }

  const back = () => {
    router.back()
  }

  const playAllSongs = () => {
    if (songs.value.length === 0) {
      toast.warning(t('common.noSongsInPlaylist'))
      return
    }
    musicStore.setPlaylist(songs.value, route.params.id as string)
    musicStore.setPlayListCurrentSong(0, true)
  }

  const toggleFavorite = async () => {
    const playlistId = route.params.id as string
    try {
      await addFavorite({
        isFavorite: !(playlistInfo.value.isCollected ?? false),
        targetId: playlistId,
        favoriteType: 'PLAYLIST'
      }).then((res) => {
        if (res.data) {
          playlistInfo.value.isCollected = !playlistInfo.value.isCollected
          toast.success(
            playlistInfo.value.isCollected
              ? t('common.collectSuccess')
              : t('common.collectCancelSuccess')
          )
        } else {
          toast.error(t('common.collectFailed'))
        }
      })
    } catch (error) {
      toast.error(t('common.collectFailed'))
    }
  }

  onMounted(async () => {
    const playlistId = route.params.id as string
    const { data } = await getPlayListDetail({ playlistId })
    playlistInfo.value = data ?? {}
    songs.value = data.songs || []
  })
</script>

<style scoped></style>
