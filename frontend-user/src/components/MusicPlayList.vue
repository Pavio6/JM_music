<template>
  <div class="w-80 flex flex-col h-full">
    <div class="flex items-center justify-between mb-4">
      <div class="flex items-center space-x-2">
        <h3 class="text-white/80 text-lg font-medium">{{ t('common.playList') }}</h3>
        <Icon
          icon="mdi:playlist-remove"
          class="text-white/60 hover:text-white cursor-pointer text-lg"
          @click="clearAll"
        />
      </div>
      <span class="text-white/60 text-sm">{{
        t('common.totalSongs', { total: playlist.length })
      }}</span>
    </div>
    <div class="flex-1 overflow-y-auto pr-1 playlist-container">
      <div
        v-for="(song, index) in playlist"
        :key="song.songId"
        @click="playSong(index)"
        :class="[
          'flex items-center p-3 rounded-lg cursor-pointer group transition-colors',
          currentSong?.songId === song.songId ? 'bg-white/10' : 'hover:bg-white/5'
        ]"
      >
        <div class="w-8 text-white/40 text-sm">{{ index + 1 }}</div>
        <div class="flex-1 min-w-0">
          <div
            :class="[
              'text-sm mb-1 truncate',
              currentSong?.songId === song.songId ? 'text-blue' : 'text-white'
            ]"
          >
            {{ song.songName }}
          </div>
          <div class="text-xs text-white/40 truncate">{{ song.singerName }}</div>
        </div>
        <div class="flex items-center space-x-3">
          <Icon
            v-if="currentSong?.songId === song.songId && isPlaying"
            icon="mdi:music-note"
            class="text-blue text-xl animate-bounce"
          />
          <Icon
            v-show="currentSong?.songId !== song.songId"
            icon="ph:play-fill"
            class="text-white/60 text-xl opacity-0 group-hover:opacity-100 transition-opacity"
          />
          <Icon
            v-show="currentSong?.songId !== song.songId"
            icon="mdi:delete"
            class="text-red-400/60 hover:text-red-400 text-lg opacity-0 group-hover:opacity-100 transition-opacity"
            @click.stop="removeMusic(song.songId)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { computed } from 'vue'
  import { Icon } from '@iconify/vue'
  import { useMusicStore } from '@/stores/music'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const musicStore = useMusicStore()

  // 播放列表相关
  const playlist = computed(() => musicStore.playlist)
  const currentSong = computed(() => musicStore.currentSong)
  const isPlaying = computed(() => musicStore.isPlaying)

  const playSong = (songIndex: number) => {
    musicStore.setPlayListCurrentSong(songIndex, true)
  }

  function clearAll() {
    musicStore.clearPlaylist()
  }

  function removeMusic(songId: number) {
    musicStore.removeFromPlaylist(songId)
  }
</script>

<style lang="scss" scoped></style>
