<template>
  <Teleport to="body">
    <div
      :class="[
        'music-control-bar bg-white/10 backdrop-blur-md',
        isFullscreen ? 'fullscreen-mode' : 'bar-mode'
      ]"
    >
      <!-- 全屏模式 -->
      <div
        v-if="isFullscreen"
        class="flex flex-row items-center justify-between gap-8 p-10 flex-1 bg-black/75 relative"
      >
        <!-- 退出全屏按钮 -->
        <div
          @click="toggleFullscreen"
          class="absolute top-4 right-4 text-white/80 hover:text-white transition-colors cursor-pointer"
        >
          <Icon icon="mdi:fullscreen-exit" class="text-2xl" />
        </div>
        <!-- 歌曲信息+ 控制器 -->
        <div class="flex-1 flex flex-col items-center space-y-8">
          <!-- 封面 -->
          <div
            class="w-64 h-64 rounded-lg overflow-hidden shadow-xl transition-transform hover:scale-105"
          >
            <Image
              :src="currentSong?.songCover"
              type="album"
              alt="album cover"
              class="w-full h-full object-cover"
            />
          </div>

          <!-- 歌曲名称 -->
          <div class="text-center">
            <h2 class="text-2xl font-bold text-white mb-2">
              {{ currentSong?.songName || t('common.noPlay') }}
            </h2>
            <p class="text-lg text-gray-300">{{ currentSong?.singerName || '-' }}</p>
          </div>

          <!-- 控制器 -->
          <div class="w-full space-y-6">
            <!-- 进度条 -->
            <div class="flex items-center w-120 mx-auto">
              <SliderBar
                v-model="progress"
                @change="handleProgressChange"
                height="large"
                show-label
                :left-label="formatTime(currentTime)"
                :right-label="formatTime(duration)"
                progress-color="white"
                class="flex-1"
              />
            </div>

            <!-- 播放控制按钮 -->
            <div class="flex justify-center items-center space-x-8">
              <!-- 收藏 -->
              <div class="text-white/80 hover:text-white transition-colors" @click="toggleFavorite">
                <Icon
                  :icon="currentSong?.isFavorite ? 'mdi:heart' : 'mdi:heart-outline'"
                  class="text-2xl"
                />
              </div>
              <div
                @click="playPrev"
                class="text-white/80 hover:text-white transition-colors cursor-pointer"
              >
                <Icon icon="mdi:skip-previous" class="text-4xl" />
              </div>
              <div
                @click="togglePlay"
                class="w-16 h-16 rounded-full bg-white/10 hover:bg-white/20 transition-colors flex items-center justify-center cursor-pointer"
              >
                <Icon :icon="isPlaying ? 'mdi:pause' : 'mdi:play'" class="text-white text-4xl" />
              </div>
              <div
                @click="playNext"
                class="text-white/80 hover:text-white transition-colors cursor-pointer"
              >
                <Icon icon="mdi:skip-next" class="text-4xl" />
              </div>
              <!-- 循环模式 -->
              <div
                class="text-white/80 hover:text-white transition-colors cursor-pointer"
                @click="togglePlayMode"
              >
                <Icon :icon="playModeIcon" class="text-3xl" />
              </div>
            </div>

            <!-- 音量控制 -->
            <div class="flex justify-center items-center space-x-4 ml-[-32px]">
              <div
                @click="toggleMute"
                class="text-white/80 hover:text-white transition-colors cursor-pointer"
              >
                <Icon
                  :icon="volume === 0 ? 'ph:speaker-slash' : 'ph:speaker-high'"
                  class="text-2xl"
                />
              </div>
              <SliderBar v-model="volume" class="w-32" height="large" progress-color="white" />
            </div>
          </div>
        </div>
        <!-- 歌词显示 -->
        <div class="flex-1 h-50vh min-h-lg">
          <MusicLyrics />
        </div>
        <!-- 播放列表 -->
        <div class="flex-shrink-0 h-50vh min-h-lg">
          <MusicPlayList />
        </div>
      </div>
      <!-- 音乐条模式 -->
      <div v-else class="flex items-center justify-between px-4 py-2 relative">
        <!-- 左侧：歌曲信息 -->
        <div class="flex items-center space-x-4 w-1/4">
          <Image
            type="song"
            :src="currentSong?.songCover"
            class="w-12 h-12 rounded-md object-cover"
          />
          <div class="flex flex-col">
            <span class="text-white text-sm font-medium truncate">{{
              currentSong?.songName || t('common.noPlay')
            }}</span>
            <span class="text-gray-400 text-xs truncate">{{ currentSong?.singerName || '-' }}</span>
          </div>
        </div>

        <!-- 中间：播放控制 -->
        <div class="flex flex-col items-center w-2/4">
          <div class="flex items-center space-x-6 mb-2">
            <!-- 收藏 -->
            <div class="text-white/80 hover:text-white transition-colors" @click="toggleFavorite">
              <Icon
                :icon="currentSong?.isFavorite ? 'mdi:heart' : 'mdi:heart-outline'"
                class="text-xl"
              />
            </div>
            <!-- 上一首 -->
            <div @click="playPrev" class="text-white/80 hover:text-white transition-colors">
              <Icon icon="mdi:skip-previous" class="text-3xl" />
            </div>
            <!-- 播放/暂停 -->
            <div
              @click="togglePlay"
              class="w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 transition-colors flex items-center justify-center"
            >
              <Icon :icon="isPlaying ? 'mdi:pause' : 'mdi:play'" class="text-white text-2xl" />
            </div>
            <!-- 下一首 -->
            <div @click="playNext" class="text-white/80 hover:text-white transition-colors">
              <Icon icon="mdi:skip-next" class="text-3xl" />
            </div>
            <!-- 循环模式 -->
            <div class="text-white/80 hover:text-white transition-colors" @click="togglePlayMode">
              <Icon :icon="playModeIcon" class="text-2xl" />
            </div>
          </div>
          <!-- 进度条 -->
          <div class="w-full px-4">
            <SliderBar
              v-model="progress"
              @change="handleProgressChange"
              show-label
              :left-label="formatTime(currentTime)"
              :right-label="formatTime(duration)"
              height="small"
              class="w-full"
            />
          </div>
        </div>

        <!-- 右侧 -->
        <div class="flex items-center space-x-4 w-1/4 justify-end">
          <!-- 全屏 -->
          <div @click="toggleFullscreen" class="text-white/80 hover:text-white transition-colors">
            <Icon :icon="isFullscreen ? 'mdi:fullscreen-exit' : 'mdi:fullscreen'" class="text-xl" />
          </div>
          <!-- 下载按钮 -->
          <div
            @click="downloadCurrentSong"
            class="text-white/80 hover:text-white transition-colors"
          >
            <Icon icon="mdi:download" class="text-xl" />
          </div>
          <!-- 歌单 -->
          <div
            @click="togglePlaylist"
            class="text-white/80 hover:text-white transition-colors relative"
          >
            <Icon icon="ph:playlist" class="text-xl" />
          </div>
          <FloatingWindow
            v-model:show="showPlaylist"
            width="auto"
            height="30rem"
            position="right"
            right="2rem"
            bottom="6rem"
          >
            <MusicPlayList />
          </FloatingWindow>
          <!-- 音量控制 -->
          <div class="flex items-center space-x-2">
            <div @click="toggleMute" class="text-white/80 hover:text-white transition-colors">
              <Icon :icon="volume === 0 ? 'ph:speaker-slash' : 'ph:speaker-high'" class="text-xl" />
            </div>
            <SliderBar v-model="volume" height="small" class="w-24" progress-color="white" />
          </div>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script lang="ts" setup>
  import { ref, computed, watch, onMounted } from 'vue'
  import { Icon } from '@iconify/vue'
  import { useMusicStore } from '@/stores/music'
  import SliderBar from '@/components/common/SliderBar.vue'
  import MusicPlayList from './MusicPlayList.vue'
  import FloatingWindow from './common/FloatingWindow.vue'
  import MusicLyrics from './MusicLyrics.vue'
  import { PlayModeEnum } from '@/api/modules/common/common.d'
  import { toast } from '@/utile'
  import { getSongAudioAndLyrics } from '@/api/modules/music/music'
  import { downloadSong } from '@/utile/music'
  import { addFavorite } from '@/api/modules/favorite/favorite'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const musicStore = useMusicStore()
  const isFullscreen = ref(false)
  const playMode = computed(() => musicStore.playMode)

  // 切换播放模式
  const togglePlayMode = () => {
    const modes = [PlayModeEnum.SEQUENCE, PlayModeEnum.SINGLE, PlayModeEnum.SHUFFLE]
    const currentIndex = modes.indexOf(playMode.value as PlayModeEnum)
    musicStore.setPlayMode(modes[(currentIndex + 1) % modes.length])
  }

  // 获取播放模式图标
  const playModeIcon = computed(() => {
    const icons = {
      [PlayModeEnum.SEQUENCE]: 'ph:repeat',
      [PlayModeEnum.SINGLE]: 'ph:repeat-once',
      [PlayModeEnum.SHUFFLE]: 'ph:shuffle'
    }
    return icons[playMode.value]
  })

  // 全屏模式切换
  const toggleFullscreen = () => {
    isFullscreen.value = !isFullscreen.value
  }
  // 播放控制
  const isPlaying = computed(() => musicStore.isPlaying)
  const currentSong = computed(() => musicStore.currentSong)
  const currentTime = computed(() => musicStore.currentTime)
  const duration = computed(() => musicStore.duration)
  const volume = computed({
    get: () => musicStore.volume,
    set: (value) => musicStore.setVolume(value)
  })

  // 计算进度百分比
  const progress = ref(0)

  // 监听currentTime的变化来更新进度
  watch([currentTime, duration], () => {
    if (duration.value > 0) {
      progress.value = (currentTime.value / duration.value) * 100
    } else {
      progress.value = 0
    }
  })

  // 格式化时间
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  // 收藏
  const toggleFavorite = async () => {
    try {
      const songId = currentSong.value?.songId
      const isFavorite = currentSong.value?.isFavorite
      if (!songId) {
        return
      }
      await addFavorite({
        isFavorite: !isFavorite,
        targetId: songId,
        favoriteType: 'SONG'
      }).then((res) => {
        if (res.data && currentSong.value) {
          currentSong.value.isFavorite = !currentSong.value.isFavorite
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

  // 播放控制函数
  const togglePlay = () => {
    musicStore.togglePlay()
  }

  const playNext = () => {
    musicStore.playNext()
  }

  const playPrev = () => {
    musicStore.playPrev()
  }

  // 进度条点击处理
  const handleProgressChange = (value: number) => {
    const seekTime = (value / 100) * duration.value
    musicStore.seek(seekTime)
    // 如果音乐处于暂停状态，自动播放
    if (!isPlaying.value) {
      musicStore.togglePlay()
    }
  }

  // 音量控制
  const toggleMute = () => {
    musicStore.toggleMute()
  }

  // 歌词相关
  const showPlaylist = ref(false)
  const togglePlaylist = () => {
    showPlaylist.value = !showPlaylist.value
  }

  // 歌曲下载功能
  const downloadCurrentSong = async () => {
    const song = currentSong.value
    if (!song) {
      toast.error(t('common.noDownload'))
      return
    }
    try {
      const { data } = await getSongAudioAndLyrics(song.songId)
      downloadSong(data.songFilePath, `${song.songName || '歌曲'}-${song.singerName || '未知'}.mp3`)
        .then(() => {
          toast.success(t('common.downloadSuccess'))
        })
        .catch(() => {
          toast.error(t('common.downloadFail'))
        })
    } catch (e) {
      toast.error(t('common.downloadFail'))
    }
  }
</script>

<style lang="scss" scoped>
  /* 进度条和音量条的悬停效果 */
  .h-1:hover {
    height: 0.375rem;
    transition: height 0.2s ease;
  }

  .fullscreen-mode {
    height: 100vh;
    position: fixed;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 999;
    display: flex;
    flex-direction: column;
  }

  .bar-mode {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: 50;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }

  .lyrics-container {
    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 2px;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }
    }
  }

  .playlist-container {
    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(255, 255, 255, 0.1);
      border-radius: 2px;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
      }
    }
  }
</style>
