<template>
  <div class="leaderboard flex flex-row space-x-8">
    <Card
      v-for="(board, idx) in leaderboards"
      :key="board.keyId"
      class="leaderboard-card flex-shrink-0 flex flex-1 flex-col items-center h-full"
      @mouseenter="hoveredIdx = idx"
      @mouseleave="hoveredIdx = null"
    >
      <div class="flex flex-col items-center space-y-2 mt-8">
        <div class="flex flex-row space-x-3">
          <div class="text-2xl font-semibold text-white/80 mb-1">
            {{ board.icon }} {{ board.boardName }}
          </div>
        </div>
        <div class="w-20 h-0.5 bg-white/40"></div>
      </div>
      <ol class="w-full flex-grow-1 overflow-auto">
        <div
          v-if="board.songs.length === 0"
          class="flex justify-center items-center h-32 text-white/60"
        >
          {{ t('common.noData') }}
        </div>
        <li
          v-for="(song, i) in board.songs"
          :key="song.songId"
          class="mb-4 last:mb-0 flex flex-col"
        >
          <div class="flex flex-row space-x-2 items-center">
            <!-- 封面 -->
            <div
              class="w-14 w-14 rounded-lg overflow-hidden shadow-xl transition-transform hover:scale-105"
            >
              <Image
                :src="song?.songCover"
                type="song"
                alt="album cover"
                class="w-full h-full object-cover"
              />
            </div>
            <div class="flex flex-col space-y-1 flex-grow">
              <span
                class="text-white font-medium text-base space-x-2 cursor-pointer"
                @click="toSongDetail(song.songId!)"
              >
                <span> {{ i + 1 }} </span>
                {{ song.songName }}
              </span>
              <span
                class="text-white/80 text-sm mt-1 truncate cursor-pointer"
                @click="toSingerDetail(song.singerId!)"
              >
                {{ song.singerName }}
              </span>
            </div>
            <div class="flex pr-2 space-x-2">
              <!-- MV -->
              <template v-if="song.mvId">
                <Button>
                  <Icon
                    icon="mingcute:video-line"
                    class="text-blue-600 cursor-pointer"
                    @click="handlePlayVideo(song.mvId)"
                  />
                </Button>
              </template>
              <Button type="default" @click="musicStore.addToPlaylist(song, true)">
                <Icon icon="famicons:play" />
              </Button>
              <Button type="default" @click="musicStore.addToPlaylist(song, false)">
                <Icon icon="mdi:playlist-plus" class="" />
              </Button>
            </div>
          </div>
        </li>
      </ol>
    </Card>
  </div>
</template>

<script lang="ts" setup>
  import { inject, onMounted, ref } from 'vue'
  import { getHotSongList, getRisingSongList, getNewSongList } from '@/api/modules/music/music'
  import type { Song } from '@/api/modules/music/music.d'
  import { useMusicStore } from '@/stores/music'
  import { useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  interface Leaderboard {
    keyId: number
    boardName: string
    icon: string
    bg: string
    songs: Song[]
  }
  const router = useRouter()
  const toSongDetail = (songId: number | string) => {
    router.push(`/song/${songId}`)
  }
  const toSingerDetail = (singerId: number | string) => {
    router.push(`/singer/${singerId}`)
  }
  const musicStore = useMusicStore()
  const hoveredIdx = ref<number | null>(null)
  const leaderboards = ref<Leaderboard[]>([
    {
      keyId: 1,
      boardName: t('common.hotSongList'),
      icon: '🔥',
      bg: 'bg-gradient-to-b from-pink-400 to-pink-300',
      songs: []
    },
    {
      keyId: 2,
      boardName: t('common.risingSongList'),
      icon: '🚀',
      bg: 'bg-gradient-to-b from-pink-300 to-pink-400',
      songs: []
    },
    {
      keyId: 3,
      boardName: t('common.newSongList'),
      icon: '🎶',
      bg: 'bg-gradient-to-b from-blue-400 to-blue-300',
      songs: []
    }
  ])

  const showLength = 100

  const fetchLeaderboardData = async () => {
    getHotSongList().then((res) => {
      leaderboards.value[0].songs = res.data.slice(0, showLength)
    })
    getRisingSongList().then((res) => {
      leaderboards.value[1].songs = res.data.slice(0, showLength)
    })
    getNewSongList().then((res) => {
      leaderboards.value[2].songs = res.data.slice(0, showLength)
    })
  }

  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }

  onMounted(() => {
    fetchLeaderboardData()
  })
</script>

<style scoped>
  .leaderboard {
    height: calc(100vh - 8rem);
    overflow: hidden;
  }
</style>
