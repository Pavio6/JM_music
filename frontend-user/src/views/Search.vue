<template>
  <div class="container mx-auto py-6 px-4">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <div class="mb-8">
      <h1 class="text-3xl font-bold mb-2">{{ t('common.searchResult') }}: {{ keyword }}</h1>
      <p class="text-gray-500" v-if="isLoading">
        {{ t('common.searching') }}
      </p>
      <p class="text-gray-500" v-else-if="noResults">
        {{ t('common.noResultsFound') }}
      </p>
    </div>

    <!-- 歌曲结果 -->
    <div v-if="searchResult.songs && searchResult.songs.length > 0" class="mb-10">
      <h2 class="text-2xl font-bold mb-4 flex items-center">
        <Icon icon="material-symbols:music-note" class="mr-2 text-primary" />
        {{ t('common.song') }}
      </h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <Card
          v-for="song in searchResult.songs"
          :key="song.songId"
          class="hover:shadow-lg transition-shadow duration-300"
        >
          <div class="flex items-center p-3 cursor-pointer" @click="navigateToSong(song.songId)">
            <div class="w-16 h-16 mr-4 rounded-md overflow-hidden bg-gray-200 flex-shrink-0">
              <Image
                v-if="song.songCover"
                :src="song.songCover"
                type="song"
                :alt="t('common.songCover')"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-gray-700">
                <Icon icon="material-symbols:music-note" class="text-3xl text-gray-400" />
              </div>
            </div>
            <div class="flex-grow">
              <h3 class="font-medium text-lg truncate">{{ song.songName }}</h3>
              <p class="text-gray-500 text-sm truncate">{{ song.singerName }}</p>
            </div>
            <div class="flex-shrink-0 ml-2">
              <Icon
                icon="material-symbols:play-circle-outline"
                class="text-3xl text-primary hover:text-primary-dark"
                @click.stop="playSong(song)"
              />
            </div>
          </div>
        </Card>
      </div>
    </div>

    <!-- 歌手结果 -->
    <div v-if="searchResult.singers && searchResult.singers.length > 0" class="mb-10">
      <h2 class="text-2xl font-bold mb-4 flex items-center">
        <Icon icon="material-symbols:person" class="mr-2 text-primary" />
        {{ t('common.singer') }}
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        <Card
          v-for="singer in searchResult.singers"
          :key="singer.singerId"
          class="hover:shadow-lg transition-shadow duration-300"
          no-padding
        >
          <div class="p-4 text-center cursor-pointer" @click="navigateToSinger(singer.singerId)">
            <div class="w-24 h-24 mx-auto rounded-full overflow-hidden bg-gray-200 mb-3">
              <Image
                v-if="singer.singerAvatar"
                :src="singer.singerAvatar"
                type="signer"
                :alt="t('common.singerAvatar')"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-gray-700">
                <Icon icon="material-symbols:person" class="text-4xl text-gray-400" />
              </div>
            </div>
            <h3 class="font-medium text-lg truncate">{{ singer.singerName }}</h3>
          </div>
        </Card>
      </div>
    </div>

    <!-- 专辑结果 -->
    <div v-if="searchResult.albums && searchResult.albums.length > 0" class="mb-10">
      <h2 class="text-2xl font-bold mb-4 flex items-center">
        <Icon icon="material-symbols:album" class="mr-2 text-primary" />
        {{ t('common.album') }}
      </h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        <Card
          v-for="album in searchResult.albums"
          :key="album.albumId"
          class="hover:shadow-lg transition-shadow duration-300"
          no-padding
        >
          <div class="p-4 cursor-pointer" @click="album.albumId && navigateToAlbum(album.albumId)">
            <div class="w-full aspect-square rounded-md overflow-hidden bg-gray-200 mb-3">
              <Image
                v-if="album.albumCover"
                :src="album.albumCover"
                type="album"
                :alt="t('common.albumCover')"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center bg-gray-700">
                <Icon icon="material-symbols:album" class="text-4xl text-gray-400" />
              </div>
            </div>
            <h3 class="font-medium text-lg truncate">{{ album.albumName }}</h3>
            <p class="text-gray-500 text-sm truncate">{{ album.singerName }}</p>
          </div>
        </Card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { Icon } from '@iconify/vue'
  import { search } from '@/api/modules/common/common'
  import type { SearchResponse } from '@/api/modules/common/common.d'
  import Card from '@/components/common/Card.vue'
  import { useMusicStore } from '@/stores/music'
  import type { Song } from '@/api/modules/music/music.d'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const route = useRoute()
  const router = useRouter()

  const back = () => {
    router.back()
  }

  const keyword = ref('')
  const isLoading = ref(false)
  const searchResult = ref<SearchResponse>({
    songs: [],
    singers: [],
    albums: []
  })

  const noResults = computed(() => {
    return (
      (!searchResult.value.songs || searchResult.value.songs.length === 0) &&
      (!searchResult.value.singers || searchResult.value.singers.length === 0) &&
      (!searchResult.value.albums || searchResult.value.albums.length === 0)
    )
  })

  onMounted(async () => {
    // 从路由参数获取搜索关键词
    keyword.value = (route.query.keyword as string) || ''
    if (keyword.value) {
      await fetchSearchResults()
    }
  })

  async function fetchSearchResults() {
    if (!keyword.value.trim()) return

    isLoading.value = true
    try {
      const response = await search({
        keyword: keyword.value,
        limit: 20 // 每类最多显示20条结果
      })

      if (response.state) {
        searchResult.value = response.data
      } else {
        console.error('搜索失败:', response.message)
      }
    } catch (error) {
      console.error('搜索请求出错:', error)
    } finally {
      isLoading.value = false
    }
  }

  function navigateToSong(id: number) {
    router.push(`/song/${id}`)
  }

  const musicStore = useMusicStore()
  function playSong(song: Song) {
    // 播放歌曲的逻辑
    musicStore.addToPlaylist(song, true)
  }

  function navigateToSinger(id: number) {
    router.push(`/singer/${id}`)
  }

  function navigateToAlbum(id: number) {
    router.push(`/album/${id}`)
  }
</script>
