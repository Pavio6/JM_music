<template>
  <div class="singer-detail space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 歌手头像 -->
        <div class="avatar-section">
          <Image
            type="singer"
            :src="singerInfo?.singerAvatar"
            :alt="singerInfo?.singerName"
            class="w-32 h-32 rounded-full object-cover"
          />
        </div>
        <!-- 歌手信息 -->
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-2">{{ singerInfo?.singerName }}</h1>
          <div class="flex items-center gap-4 text-gray-600 mb-4">
            <div class="stat-item">
              <span class="font-medium">{{ t('common.region') }}：</span>
              <span>{{ singerInfo?.regionName || t('common.unknown') }}</span>
            </div>
            <div class="stat-item">
              <span class="font-medium">{{ t('common.followerCount') }}：</span>
              <span>{{ singerInfo?.followerCount || 0 }}</span>
            </div>
          </div>
          <p class="text-gray-600">{{ singerInfo?.singerBio || t('common.noIntroduction') }}</p>
        </div>
        <div class="flex gap-4">
          <Button type="primary" @click="playAllSongsInSinger">
            <Icon icon="mdi:play" class="mr-1" />
            {{ t('common.playAll') }}
          </Button>
          <Button @click="toggleFavoriteSinger">
            <Icon :icon="singerInfo?.isFollowed ? 'mdi:heart' : 'mdi:heart-outline'" class="mr-1" />
            {{ singerInfo?.isFollowed ? t('common.followed') : t('common.follow') }}
          </Button>
        </div>
      </div>
    </Card>

    <!-- 热门歌曲 -->
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">{{ t('common.hotSongs') }}</h2>
      </div>
      <Table :columns="songColumns" :data="hotSongs">
        <template #cell-index="{ index }">
          {{ index + 1 }}
        </template>
        <template #cell-songName="{ row }">
          <div class="flex items-center gap-3">
            <Image type="song" :src="row.songCover" :alt="row.songName" class="w-10 h-10 rounded" />
            <span>{{ row.songName }}</span>
            <template v-if="row.mvId">
              <Icon
                icon="mingcute:video-line"
                class="text-blue-600 cursor-pointer"
                @click="handlePlayVideo(row.mvId)"
              />
            </template>
          </div>
        </template>
        <template #cell-duration="{ row }">
          {{ row.songDuration ?? 0 }}
        </template>
        <template #cell-actions="{ row }">
          <div class="flex flex-row space-x-2">
            <Button @click="addToPlaylist(row, true)" inline>
              <Icon icon="famicons:play" />
            </Button>
            <Button @click="addToPlaylist(row, false)" inline>
              <Icon icon="mdi:playlist-plus" />
            </Button>
            <Button @click="toggleFavorite(row)">
              <Icon :icon="row.isLiked ? 'mdi:heart' : 'mdi:heart-outline'" />
            </Button>
          </div>
        </template>
      </Table>
    </Card>

    <!-- 专辑列表 -->
    <Card v-if="singerInfo?.albums?.length" size="lg">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">{{ t('common.albumList') }}</h2>
      </div>
      <div class="grid grid-cols-5 gap-4">
        <div
          v-for="album in singerInfo?.albums"
          :key="album.albumId"
          class="album-card hover:shadow-lg transition-shadow cursor-pointer"
          @click="navigateToPlaylist(album)"
        >
          <Image
            type="album"
            :src="album.albumCover"
            :alt="album.albumName"
            class="w-full aspect-square object-cover rounded-lg"
          />
          <div class="font-semibold text-base mb-1">{{ album.albumName }}</div>
        </div>
      </div>
    </Card>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, inject } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import Table from '@/components/common/Table.vue'
  import { followSingerOrUser, getSingerDetail } from '@/api/modules/singer/signer'
  import type { Signer } from '@/api/modules/singer/singer.d'
  import type { Song } from '@/api/modules/music/music.d'
  import { toast } from '@/utile'
  import { useMusicStore } from '@/stores/music'
  import type { Album } from '@/api/modules/album/album.d'
  import { useI18n } from 'vue-i18n'
  import { addFavorite } from '@/api/modules/favorite/favorite'

  const { t } = useI18n()

  const route = useRoute()
  const router = useRouter()
  const back = () => {
    router.back()
  }
  const singerInfo = ref<Signer | null>(null)
  const hotSongs = ref<Song[]>([])

  const songColumns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.songName') },
    { key: 'albumName', title: t('common.albumName') },
    { key: 'duration', title: t('common.duration') },
    { key: 'actions', title: t('common.actions') }
  ]

  const musicStore = useMusicStore()

  const playAllSongsInSinger = () => {
    if (!hotSongs.value.length) {
      toast.warning(t('common.noSongs'))
      return
    }
    musicStore.setPlaylist(hotSongs.value)
    musicStore.setPlayListCurrentSong(0, true)
  }

  const toggleFavoriteSinger = async () => {
    try {
      if (!singerInfo.value?.singerId) {
        return
      }
      await followSingerOrUser({
        followedId: singerInfo.value?.singerId,
        isFollow: !singerInfo.value.isFollowed,
        followType: 'SINGER'
      }).then((res) => {
        if (res.data) {
          singerInfo.value!.isFollowed = !singerInfo.value?.isFollowed
          toast.success(
            singerInfo.value!.isFollowed
              ? t('common.followSuccess')
              : t('common.followCancelSuccess')
          )
        }
      })
    } catch (error) {
      toast.error(t('common.operationFailed'))
    }
  }

  onMounted(async () => {
    const singerId = route.params.id as string
    const { data } = await getSingerDetail(Number(singerId))
    singerInfo.value = data

    hotSongs.value = data.songs
  })

  const addToPlaylist = (song: Song, playNow = false) => {
    musicStore.addToPlaylist(song, playNow)
  }

  const toggleFavorite = async (song: Song) => {
    if (!song.songId) {
      return
    }
    try {
      const isFavorite = song.isLiked
      await addFavorite({
        isFavorite: !isFavorite,
        targetId: song.songId,
        favoriteType: 'SONG'
      }).then((res) => {
        if (res.data) {
          song.isLiked = !isFavorite
          toast.success(
            isFavorite ? t('common.cancelFavoriteSuccess') : t('common.favoriteSuccess')
          )
        } else {
          toast.error(t('common.favoriteFail'))
        }
      })
    } catch (error) {
      toast.error(t('common.favoriteFail'))
    }
  }

  const navigateToPlaylist = (album: Album) => {
    if (album.albumId) {
      router.push({ path: `/album/${album.albumId}` })
    }
  }

  // MV播放
  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }
</script>

<style scoped></style>
