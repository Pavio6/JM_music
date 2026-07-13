<template>
  <div class="album-detail space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 专辑封面 -->
        <div class="cover-section">
          <Image
            :src="albumInfo?.albumCover"
            type="album"
            :alt="albumInfo?.albumName"
            class="w-32 h-32 rounded-lg object-cover"
          />
        </div>
        <!-- 专辑信息 -->
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-2">{{ albumInfo?.albumName }}</h1>
          <div class="flex items-center gap-4 text-gray-600 mb-4">
            <div class="creator flex items-center gap-2">
              <Image
                :src="albumInfo?.singerAvatar"
                type="avatar"
                :alt="albumInfo?.singerName"
                class="w-6 h-6 rounded-full"
              />
              <span>{{ albumInfo.singerName }}</span>
            </div>
          </div>
          <p class="text-gray-600">{{ albumInfo?.albumBio || t('common.noAlbumBio') }}</p>
          <div
            v-if="albumInfo?.typeName && albumInfo.typeName.length"
            class="flex flex-wrap gap-2 mt-2"
          >
            <span class="text-blue-600 text-xs cursor-auto">#{{ albumInfo.typeName }}</span>
          </div>
        </div>
        <div class="flex gap-4">
          <Button type="primary" @click="playAllSongs">
            <Icon icon="mdi:play" class="mr-1" />
            {{ t('common.playAll') }}
          </Button>
          <Button @click="toggleFavorite">
            <Icon :icon="albumInfo?.isFavorite ? 'mdi:heart' : 'mdi:heart-outline'" class="mr-1" />
            {{ albumInfo?.isFavorite ? t('common.favorited') : t('common.notFavorite') }}
          </Button>
        </div>
      </div>
    </Card>

    <!-- 歌曲列表 -->
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">{{ t('common.songList') }}</h2>
        <div class="text-gray-600">
          {{ t('common.totalSong', { total: albumInfo?.songs?.length || 0 }) }}
        </div>
      </div>
      <Table :columns="columns" :data="songs ?? []">
        <template #cell-index="{ index }">
          {{ index + 1 }}
        </template>
        <template #cell-songName="{ row }">
          <div class="flex items-center gap-3">
            <Image :src="row.songCover" type="song" :alt="row.songName" class="w-10 h-10 rounded" />
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
        <template #cell-duration="{ row }">
          {{ row.songDuration }}
        </template>
      </Table>
    </Card>

    <!-- 评论模块 -->
    <Card size="lg">
      <Comment
        :targetId="Array.isArray(route.params.id) ? route.params.id[0] : route.params.id"
        targetType="ALBUM"
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
  import { useMusicStore } from '@/stores/music'
  import { toast } from '@/utile'
  import { inject } from 'vue'
  import { addFavorite } from '@/api/modules/favorite/favorite'
  import { getAlbumDetail } from '@/api/modules/album/album'
  import type { Album } from '@/api/modules/album/album.d'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n({ useScope: 'global' })
  const route = useRoute()
  const albumInfo = ref<Partial<Album>>({})
  const songs = ref<Album['songs']>([])
  const musicStore = useMusicStore()

  const columns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.song') },
    { key: 'singerName', title: t('common.singer') },
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
    if (!songs.value) return
    if (songs.value.length === 0) {
      toast.warning(t('common.playListNoSong'))
      return
    }
    musicStore.setPlaylist(songs.value)
    musicStore.setPlayListCurrentSong(0, true)
  }

  const toggleFavorite = async () => {
    const albumId = route.params.id as string
    if (!albumId) return
    try {
      await addFavorite({
        isFavorite: !albumInfo.value?.isFavorite,
        targetId: albumId,
        favoriteType: 'ALBUM'
      }).then((res) => {
        if (res.data) {
          albumInfo.value!.isFavorite = !albumInfo.value?.isFavorite
          toast.success(
            albumInfo.value!.isFavorite
              ? t('common.collectionSuccessful')
              : t('common.collectionCanceledSuccessfully')
          )
        } else {
          toast.error(t('common.collectionFailed'))
        }
      })
    } catch (error) {
      toast.error(t('common.collectionFailed'))
    }
  }

  onMounted(async () => {
    const albumId = route.params.id as string
    const { data } = await getAlbumDetail(albumId)
    albumInfo.value = data
    songs.value = data.songs
  })
</script>

<style scoped></style>
