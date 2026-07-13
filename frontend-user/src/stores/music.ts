import type { Song } from '@/api/modules/music/music.d'
import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue'
import { useUserStore } from '@/stores/user'
import { Howl } from 'howler'
import { cloneDeep } from 'lodash-es'
import { getSongAudioAndLyrics, getSongDetail, updatePlayCount } from '@/api/modules/music/music'
import { toast } from '@/utile'
import { PlayModeEnum, type PlayMode, type QueueType } from '@/api/modules/common/common.d'
import { parseLyrics } from '@/utile/music'
import {
  addSongToQueue,
  batchDeleteSongFromQueue,
  changePlayMode,
  clearAllQueue,
  switchQueue,
  updateSongInQueue
} from '@/api/modules/queue/queue'
import { useI18n } from 'vue-i18n'

export type Middleware = {
  type: 'sync' | 'async'
  name: string
  handler: (song: Song) => boolean | Promise<boolean> | void
}

function updateCurrentSong(song: Song) {
  if (!song.songId) return
  setTimeout(() => {
    updateSongInQueue(song.songId).catch((error) => {
      console.error('修改播放歌曲失败：', error)
    })
  }, 5000)
}

function increasePlayCount(song: Song) {
  if (!song.songId) return
  setTimeout(() => {
    updatePlayCount(song.songId, 'SONG')
  }, 1000)
}

export const useMusicStore = defineStore('music', () => {
  const { t } = useI18n()

  const middlewares = ref<Middleware[]>([
    {
      type: 'async',
      name: 'updateCurrentSong',
      handler: updateCurrentSong
    },
    {
      type: 'async',
      name: 'increasePlayCount',
      handler: increasePlayCount
    }
  ])

  function useMiddleware(middleware: Middleware) {
    middlewares.value.push(middleware)
  }

  const currentSong = ref<Song | null>(null)
  const autoPlayed = ref(false)
  const playlist = ref<Song[]>([])
  const isPlaying = ref(false)
  const currentTime = ref(0)
  const duration = ref(0)
  const volume = ref(100)
  const previousVolume = ref(100)
  const playMode = ref<PlayMode>(PlayModeEnum.SEQUENCE) // 默认列表循环
  const currentLyrics = ref<{ time: number; text: string }[]>([]) // 当前歌词数组
  const currentLyricIndex = ref(-1) // 当前歌词索引
  const userStore = useUserStore()
  let sound: Howl | null = null

  // 清理音频实例
  function clearSound() {
    if (sound) {
      sound.stop()
      sound.unload()
      sound = null
    }
    // 重置歌词状态
    currentLyrics.value = []
    currentLyricIndex.value = -1
  }

  // 当前歌曲索引
  const currentIndex = computed(() => {
    if (!currentSong.value) return -1
    return playlist.value.findIndex((song) => song.songId === currentSong.value?.songId)
  })

  // 设置当前歌曲
  async function setPlayListCurrentSong(index: number, played: boolean = true) {
    if (index < 0 || index >= playlist.value.length) return

    const targetSong = playlist.value[index]

    // 执行同步中间件校验
    const syncResults = await Promise.all(
      middlewares.value.filter((m) => m.type === 'sync').map((m) => m.handler(targetSong))
    )

    if (syncResults.some((result) => result !== true)) {
      return
    }

    // 触发异步中间件
    middlewares.value.filter((m) => m.type === 'async').forEach((m) => m.handler(targetSong))

    clearSound()

    autoPlayed.value = played

    try {
      const res = await getSongDetail(targetSong.songId)
      currentSong.value = cloneDeep(res.data)
    } catch (error) {
      toast.error(t('common.music.getSongDetailError'))
    }
  }

  // 设置播放列表
  function setPlaylist(songs: Song[], playlistId?: number | string) {
    playlist.value = songs
    if (userStore.isLogin) {
      switchQueue({ songIds: songs.map((song) => song.songId) }).catch((error) => {
        console.error('切换播放队列失败：', error)
      })
    }
    if (playlistId) {
      updatePlayCount(playlistId, 'PLAYLIST')
    }
  }

  // 添加歌曲到播放列表
  function addToPlaylist(song: Song, played: boolean = true) {
    const inPlaylist = playlist.value.some((item) => item.songId === song.songId)
    if (inPlaylist) {
      if (played) {
        const index = playlist.value.findIndex((item) => item.songId === song.songId)
        setPlayListCurrentSong(index, played)
      }
    } else {
      // 添加到正在播放歌曲的下方
      const currentSongIndex = playlist.value.findIndex(
        (item) => item.songId === currentSong.value?.songId
      )
      if (currentSongIndex !== -1) {
        playlist.value.splice(currentSongIndex + 1, 0, song)
      } else {
        playlist.value.unshift(song)
      }
      if (played) {
        setPlayListCurrentSong(currentSongIndex + 1, played)
      }
      if (userStore.isLogin) {
        addSongToQueue(song.songId).catch((error) => {
          console.error('添加歌曲到播放队列失败：', error)
        })
      }
    }
  }

  // 清空播放列表
  function clearPlaylist() {
    playlist.value = []
    currentSong.value = null
    isPlaying.value = false
    currentTime.value = 0
    duration.value = 0
    currentLyrics.value = []
    currentLyricIndex.value = -1

    if (userStore.isLogin) {
      clearAllQueue().catch((error) => {
        console.error('清空播放队列失败：', error)
      })
    }
  }

  // 从播放列表移除歌曲
  function removeFromPlaylist(songId: number) {
    const index = playlist.value.findIndex((song) => song.songId === songId)
    if (index > -1) {
      playlist.value.splice(index, 1)
      if (userStore.isLogin) {
        batchDeleteSongFromQueue([songId]).catch((error) => {
          console.error('移除歌曲从播放队列失败：', error)
        })
      }
    }
  }

  // 获取下一首歌曲
  function getNextSong(): Song | null {
    if (playlist.value.length === 0) return null

    switch (playMode.value) {
      case PlayModeEnum.SINGLE:
        return currentSong.value // 单曲循环返回当前歌曲
      case PlayModeEnum.SHUFFLE:
        const randomIndex = Math.floor(Math.random() * playlist.value.length)
        return playlist.value[randomIndex]
      case PlayModeEnum.SEQUENCE:
      default:
        if (currentIndex.value === playlist.value.length - 1) {
          return playlist.value[0] // 列表循环，最后一首歌返回第一首
        }
        return playlist.value[currentIndex.value + 1]
    }
  }

  // 获取上一首歌曲
  function getPrevSong(): Song | null {
    if (playlist.value.length === 0) return null

    switch (playMode.value) {
      case PlayModeEnum.SINGLE:
        return currentSong.value
      case PlayModeEnum.SHUFFLE:
        const randomIndex = Math.floor(Math.random() * playlist.value.length)
        return playlist.value[randomIndex]
      case PlayModeEnum.SEQUENCE:
      default:
        if (currentIndex.value === 0) {
          return playlist.value[playlist.value.length - 1] // 列表循环，第一首歌返回最后一首
        }
        return playlist.value[currentIndex.value - 1]
    }
  }

  // 设置播放模式
  function setPlayMode(mode: PlayMode) {
    playMode.value = mode
    if (userStore.isLogin) {
      changePlayMode(mode).catch((error) => {
        console.error('切换播放模式失败：', error)
      })
    }
  }

  // 播放下一首
  function playNext() {
    const nextSong = getNextSong()
    const nextSongIndex = playlist.value.findIndex((song) => song.songId === nextSong?.songId)
    if (nextSong && nextSongIndex > -1) {
      setPlayListCurrentSong(nextSongIndex, true)
    }
  }

  // 播放上一首
  function playPrev() {
    const prevSong = getPrevSong()
    const prevSongIndex = playlist.value.findIndex((song) => song.songId === prevSong?.songId)
    if (prevSong && prevSongIndex > -1) {
      setPlayListCurrentSong(prevSongIndex, true)
    }
  }

  // 播放
  function play() {
    if (sound) {
      sound.play()
      isPlaying.value = true
      autoPlayed.value = false
    }
  }

  // 暂停
  function pause() {
    if (sound) {
      sound.pause()
      isPlaying.value = false
    }
  }

  // 重放
  function replay() {
    if (sound) {
      seek(0)
    }
  }

  // 切换播放/暂停
  function togglePlay(trigger?: boolean) {
    if (sound) {
      if (trigger !== undefined) {
        trigger ? play() : pause()
      } else {
        isPlaying.value ? pause() : play()
      }
    }
  }

  // 更新进度
  function updateProgress() {
    if (sound && isPlaying.value) {
      currentTime.value = sound.seek() as number
      requestAnimationFrame(updateProgress)
    }
  }

  // 设置播放进度
  function seek(time: number) {
    if (sound) {
      sound.seek(time)
      currentTime.value = time
    }
  }

  // 设置音量
  function setVolume(value: number) {
    volume.value = value
    if (sound) {
      sound.volume(value / 100)
    }
  }

  // 切换静音
  function toggleMute() {
    if (volume.value > 0) {
      previousVolume.value = volume.value
      setVolume(0)
    } else {
      setVolume(previousVolume.value)
    }
  }

  // 监听当前歌曲变化
  watch(currentSong, async (newSong) => {
    clearSound()
    if (newSong?.songId) {
      try {
        const { data } = await getSongAudioAndLyrics(newSong.songId)
        newSong.songFilePath = data.songFilePath
        if (data.songLyrics) {
          loadLyrics(data.songLyrics)
        } else {
          currentLyrics.value = []
          currentLyricIndex.value = -1
        }
      } catch (error) {
        console.error('获取歌曲音频和歌词失败：', error)
        toast.error(t('common.music.getSongAudioAndLyricsError'))
        return
      }
      sound = new Howl({
        src: [newSong.songFilePath],
        html5: true,
        volume: volume.value / 100,
        onload: () => {
          duration.value = sound?.duration() || 0
        },
        onplay: () => {
          isPlaying.value = true
          requestAnimationFrame(updateProgress)
        },
        onpause: () => {
          isPlaying.value = false
        },
        onend: () => {
          playNext()
        },
        onstop: () => {
          isPlaying.value = false
        }
      })
      if (autoPlayed.value) {
        // 等待sound实例初始化完成后再播放
        setTimeout(() => {
          play()
        }, 100)
      }
    }
  })

  // 更新当前播放时间
  function updateCurrentTime(time: number) {
    currentTime.value = time
  }

  // 加载歌词
  async function loadLyrics(lyricsUrl: string) {
    try {
      const response = await fetch(lyricsUrl)
      const buffer = await response.arrayBuffer()

      // 尝试多种编码格式，按照常见程度排序
      const encodings = ['utf-8', 'gbk', 'big5', 'shift-jis', 'euc-kr', 'iso-8859-1']
      let lyrContent = ''
      let successfulEncoding = ''

      // 依次尝试不同编码
      for (const encoding of encodings) {
        try {
          const decoder = new TextDecoder(encoding)
          const decodedText = decoder.decode(buffer)

          // 检查解码结果是否有明显乱码
          if (!decodedText.includes('�') && !/[\uFFFD\uD800-\uDBFF]/.test(decodedText)) {
            lyrContent = decodedText
            successfulEncoding = encoding
            break
          }

          // 如果是最后一种编码方式，即使有乱码也使用
          if (encoding === encodings[encodings.length - 1]) {
            lyrContent = decodedText
            successfulEncoding = encoding
          }
        } catch (e) {
          console.warn(`使用 ${encoding} 解码失败:`, e)
          // 继续尝试下一种编码
        }
      }
      const lyrics = parseLyrics(lyrContent)
      currentLyrics.value = lyrics
      currentLyricIndex.value = -1
    } catch (error) {
      console.error('获取歌词文件失败：', error)
      currentLyrics.value = []
      currentLyricIndex.value = -1
    }
  }

  // 更新当前歌词索引
  function updateLyricIndex(time: number) {
    // 让歌词提前 0.5 秒滚动
    const adjustedTime = time + 0.5
    const index = currentLyrics.value.findIndex((lyric, i) => {
      const nextLyric = currentLyrics.value[i + 1]
      return adjustedTime >= lyric.time && (!nextLyric || adjustedTime < nextLyric.time)
    })
    if (index !== -1) {
      currentLyricIndex.value = index
    }
  }

  // 监听播放时间更新
  watch(currentTime, (newTime) => {
    updateLyricIndex(newTime)
  })

  return {
    currentSong,
    playlist,
    isPlaying,
    currentTime,
    duration,
    volume,
    currentIndex,
    setPlayListCurrentSong,
    setPlaylist,
    addToPlaylist,
    clearPlaylist,
    removeFromPlaylist,
    playNext,
    playPrev,
    togglePlay,
    updateCurrentTime,
    playMode,
    setPlayMode,
    seek,
    setVolume,
    toggleMute,
    currentLyrics,
    currentLyricIndex,
    useMiddleware
  }
})
