<template>
  <div class="favorite space-y-8">
    <div class="bg-white/5 rounded-xl p-6 shadow mb-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-bold">{{ t('common.favorite.song') }}</h2>
      </div>
      <Table :columns="songColumns" :data="favoriteSongs" class="mb-4">
        <template #cell-index="{ index }">
          <span>{{ (favoriteSongsPageNum - 1) * favoriteSongsPageSize + index + 1 }}</span>
        </template>
        <template #cell-songName="{ row }">
          <div class="flex items-center gap-3">
            <Image :src="row.songCover" type="song" :alt="row.songName" class="w-10 h-10 rounded" />
            <router-link :to="`/song/${row.songId}`" class="text-blue-600 hover:text-blue-800">
              {{ row.songName }}
            </router-link>
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
          {{ row.albumName }}
        </template>
        <template #cell-duration="{ row }">
          {{ row.songDuration }}
        </template>
        <template #cell-action="{ row }">
          <div class="flex flex-row space-x-2">
            <Button @click="addToPlaylist(row, true)" inline>
              <Icon icon="famicons:play" />
            </Button>
            <Button @click="addToPlaylist(row, false)" inline>
              <Icon icon="mdi:playlist-plus" />
            </Button>
            <Button type="danger" size="sm" @click="cancelFavoriteSong(row.songId)" inline>
              <Icon icon="mdi:heart-off" />
            </Button>
          </div>
        </template>
      </Table>
      <div class="flex justify-end">
        <Pagination
          :total="favoriteSongsTotal"
          v-model:currentPage="favoriteSongsPageNum"
          v-model:pageSize="favoriteSongsPageSize"
          @change="refreshFavoriteSongs"
        />
      </div>
    </div>
    <div class="mb-6">
      <div class="flex border-b border-gray-200 dark:border-gray-700">
        <Button
          :type="activeTab === 'playlist' ? 'primary' : 'default'"
          @click="handleChangeTab('playlist')"
          class="rounded-rt-none rounded-rb-none"
        >
          {{ t('common.favorite.playlist') }}
        </Button>
        <Button
          :type="activeTab === 'album' ? 'primary' : 'default'"
          @click="handleChangeTab('album')"
          class="rounded-lt-none rounded-lb-none"
        >
          {{ t('common.favorite.album') }}
        </Button>
      </div>
    </div>
    <div v-if="activeTab === 'playlist'">
      <div class="flex mb-6">
        <template v-if="favoritePlaylistsTotal > 0">
          <div class="flex flex-row items-center gap-3">
            <div>
              {{ t('common.favorite.totalPlaylist', { total: favoritePlaylistsTotal }) }}
            </div>
            <Button rounded @click="prevPage(activeTab)">
              <Icon icon="mingcute:left-fill" />
            </Button>
            <Button rounded @click="nextPage(activeTab)">
              <Icon icon="mingcute:right-fill" />
            </Button>
          </div>
        </template>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
        <template v-if="favoritePlaylists.length > 0">
          <Card
            v-for="playlist in favoritePlaylists"
            :key="playlist.id"
            class="overflow-hidden cursor-pointer"
            no-padding
            @click="handlePageto('playlist', playlist.id)"
          >
            <Image
              :src="playlist.cover"
              type="playlist"
              :alt="playlist.name"
              class="w-full aspect-[4/3] object-cover"
            />
            <div class="p-4">
              <div class="font-semibold text-base mb-1">{{ playlist.name }}</div>
            </div>
          </Card>
        </template>
        <template v-else>
          <div class="text-center text-white/60 py-8">
            <Icon icon="mdi:playlist-music" class="text-5xl" />
            <div class="mt-4">{{ t('common.favorite.noPlaylist') }}</div>
          </div>
        </template>
      </div>
    </div>
    <div v-else-if="activeTab === 'album'">
      <div class="flex mb-6">
        <template v-if="favoriteAlbumsTotal > 0">
          <div class="flex flex-row items-center gap-3">
            <div>
              {{ t('common.favorite.totalAlbum', { total: favoriteAlbumsTotal }) }}
            </div>
            <Button rounded @click="prevPage(activeTab)">
              <Icon icon="mingcute:left-fill" />
            </Button>
            <Button rounded @click="nextPage(activeTab)">
              <Icon icon="mingcute:right-fill" />
            </Button>
          </div>
        </template>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-5 gap-6">
        <template v-if="favoriteAlbums.length > 0">
          <Card
            v-for="album in favoriteAlbums"
            :key="album.id"
            class="overflow-hidden cursor-pointer"
            no-padding
            @click="handlePageto('album', album.id)"
          >
            <Image
              :src="album.cover"
              type="album"
              :alt="album.name"
              class="w-full aspect-square object-cover"
            />
            <div class="p-4">
              <div class="font-semibold text-base mb-1">{{ album.name }}</div>
            </div>
          </Card>
        </template>
        <template v-else>
          <div class="text-center text-white/60 py-8">
            <Icon icon="mdi:album" class="text-5xl" />
            <div class="mt-4">{{ t('common.favorite.noAlbum') }}</div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { onMounted, ref, computed, inject } from 'vue'
  import Button from '@/components/common/Button.vue'
  import Card from '@/components/common/Card.vue'
  import Pagination from '@/components/common/Pagination.vue'
  import Table from '@/components/common/Table.vue'
  import { addFavorite, getFavoriteMineList } from '@/api/modules/favorite/favorite'
  import type { Song } from '@/api/modules/music/music.d'
  import { useUserStore } from '@/stores/user'
  import { useMusicStore } from '@/stores/music'
  import { getUserPlaylistCollect } from '@/api/modules/user/user'
  import { toast } from '@/utile'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  interface PlayList {
    id: number | string
    cover: string
    name: string
  }

  const { t } = useI18n()
  const userStore = useUserStore()
  const musicStore = useMusicStore()

  // 顶部歌单卡片区数据
  const activeTab = ref('playlist')
  // 收藏的歌曲和歌单数据
  const favoriteSongs = ref<Song[]>([])
  const favoriteSongsTotal = ref(0)
  const favoriteSongsPageNum = ref(1)
  const favoriteSongsPageSize = ref(10)
  const favoritePlaylists = ref<PlayList[]>([])
  const favoritePlaylistsTotal = ref(0)
  const favoritePlaylistsPageNum = ref(1)
  const favoritePlaylistsPageSize = ref(4)
  const favoriteAlbums = ref<PlayList[]>([])
  const favoriteAlbumsTotal = ref(0)
  const favoriteAlbumsPageNum = ref(1)
  const favoriteAlbumsPageSize = ref(4)

  const songColumns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: '歌曲' },
    { key: 'singerName', title: '歌手' },
    { key: 'albumName', title: '专辑' },
    { key: 'duration', title: '时长' },
    { key: 'action', title: '' }
  ]

  const handleChangeTab = (tab: string) => {
    activeTab.value = tab
    if (tab === 'playlist') {
      favoritePlaylistsPageNum.value = 1
      refreshFavoritePlaylists()
    } else if (tab === 'album') {
      favoriteAlbumsPageNum.value = 1
      refreshFavoriteAlbums()
    }
  }

  const refreshFavoriteSongs = () => {
    getFavoriteMineList({
      pageNum: favoriteSongsPageNum.value,
      pageSize: favoriteSongsPageSize.value
    }).then((res) => {
      favoriteSongs.value = res.data.records
      favoriteSongsTotal.value = res.data.total
    })
  }

  const prevPage = (type: string) => {
    if (type === 'playlist') {
      if (favoritePlaylistsPageNum.value === 1) return
      favoritePlaylistsPageNum.value--
      refreshFavoritePlaylists()
    } else if (type === 'album') {
      if (favoriteAlbumsPageNum.value === 1) return
      favoriteAlbumsPageNum.value--
      refreshFavoriteAlbums()
    }
  }

  const nextPage = (type: string) => {
    if (type === 'playlist') {
      if (
        favoritePlaylistsPageNum.value * favoritePlaylistsPageSize.value >=
        favoritePlaylistsTotal.value
      ) {
        return
      }
      favoritePlaylistsPageNum.value++
      refreshFavoritePlaylists()
    } else if (type === 'album') {
      if (favoriteAlbumsPageNum.value * favoriteAlbumsPageSize.value >= favoriteAlbumsTotal.value) {
        return
      }
      favoriteAlbumsPageNum.value++
      refreshFavoriteAlbums()
    }
  }

  const refreshFavoritePlaylists = () => {
    getUserPlaylistCollect({
      userId: userStore.userInfo.userId,
      type: 'PLAYLIST',
      pageNum: favoritePlaylistsPageNum.value,
      pageSize: favoritePlaylistsPageSize.value
    })
      .then((res) => {
        favoritePlaylists.value = res.data.records.map((album) => {
          return {
            id: album.id,
            cover: album.cover,
            name: album.name
          }
        })
        favoritePlaylistsTotal.value = res.data.total
      })
      .catch((err) => {
        console.log('🚀 ~ err:', err)
        toast.error('获取歌单失败')
      })
  }

  const refreshFavoriteAlbums = () => {
    getUserPlaylistCollect({
      userId: userStore.userInfo.userId,
      type: 'ALBUM',
      pageNum: favoriteAlbumsPageNum.value,
      pageSize: favoriteAlbumsPageSize.value
    })
      .then((res) => {
        favoriteAlbums.value = res.data.records
        favoriteAlbumsTotal.value = res.data.total
      })
      .catch((err) => {
        console.log('🚀 ~ err:', err)
        toast.error('获取专辑失败')
      })
  }

  onMounted(() => {
    refreshFavoriteSongs()
    refreshFavoritePlaylists()
    refreshFavoriteAlbums()
  })

  // 取消收藏歌曲
  function cancelFavoriteSong(songId: number) {
    addFavorite({
      isFavorite: false,
      targetId: songId,
      favoriteType: 'SONG'
    }).then((res) => {
      if (res.data) toast.success('取消收藏成功')
      favoriteSongs.value = favoriteSongs.value.filter((song) => song.songId !== songId)
    })
  }

  // 播放列表
  const addToPlaylist = (song: Song, playNow = false) => {
    musicStore.addToPlaylist(song, playNow)
  }

  // MV播放
  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }

  const router = useRouter()
  const handlePageto = (type: string, id: string | number) => {
    if (type === 'playlist') {
      router.push(`/playlist/${id}`)
    } else if (type === 'album') {
      router.push(`/album/${id}`)
    }
  }
</script>

<style lang="scss" scoped>
  .favorite {
    min-height: 80vh;
  }
</style>
