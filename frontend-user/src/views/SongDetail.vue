<template>
  <div class="song-detail space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 歌曲封面 -->
        <div class="cover-section">
          <Image
            type="song"
            :src="songInfo?.songCover"
            :alt="songInfo?.songName"
            class="w-32 h-32 rounded-lg object-cover"
          />
        </div>
        <!-- 歌曲信息 -->
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-2">{{ songInfo?.songName }}</h1>
          <div class="flex items-center gap-4 text-gray-600 mb-4">
            <div class="artist flex items-center gap-2">
              <router-link
                :to="`/singer/${songInfo?.singerId}`"
                class="text-blue-600 hover:text-blue-800"
              >
                {{ songInfo?.singerName }}
              </router-link>
            </div>
            <div class="album">{{ t('common.albumName') }}：{{ songInfo?.albumName }}</div>
            <div class="duration">{{ t('common.duration') }}：{{ songInfo?.songDuration }}</div>
          </div>
          <div class="flex gap-4">
            <Button type="primary" @click="handlePlay">
              <Icon icon="mdi:play" class="mr-1" />
              {{ t('common.play') }}
            </Button>
            <Button type="default" @click="handleAddToPlaylist">
              <Icon icon="mdi:playlist-plus" class="mr-1" />
              {{ t('common.addToPlaylist') }}
            </Button>
            <Button
              type="default"
              @click="handleToggleFavorite"
              :class="{ 'text-red-500': songInfo?.isFavorite }"
            >
              <Icon :icon="songInfo?.isFavorite ? 'mdi:heart' : 'mdi:heart-outline'" />
            </Button>
          </div>
        </div>
      </div>
    </Card>

    <!-- 歌词 -->
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-xl font-bold">{{ t('common.lyrics') }}</h2>
      </div>
      <div class="lyrics-container max-h-96 overflow-y-auto">
        <p v-if="!lyrics?.length" class="text-gray-500 text-center py-4">
          {{ t('common.noLyrics') }}
        </p>
        <div v-for="(line, index) in lyrics" :key="index" class="py-2">
          {{ line.text }}
        </div>
      </div>
    </Card>

    <!-- 评论模块 -->
    <Card size="lg">
      <Comment
        :targetId="Array.isArray(route.params.id) ? route.params.id[0] : route.params.id"
        targetType="SONG"
      />
    </Card>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import { Icon } from '@iconify/vue'
  import Card from '@/components/common/Card.vue'
  import Button from '@/components/common/Button.vue'
  import { useMusicStore } from '@/stores/music'
  import { getSongAudioAndLyrics, getSongDetail, favoriteMusic } from '@/api/modules/music/music'
  import type { Song } from '@/api/modules/music/music.d'
  import { parseLyrics } from '@/utile/music'
  import { useI18n } from 'vue-i18n'
  import { toast } from '@/utile'
  import { addFavorite } from '@/api/modules/favorite/favorite'

  const { t } = useI18n()

  const route = useRoute()
  const router = useRouter()
  const back = () => {
    router.back()
  }
  const musicStore = useMusicStore()
  const songInfo = ref<Song | null>(null)
  const lyrics = ref<{ time: number; text: string }[]>([])

  // 播放歌曲
  function handlePlay() {
    if (songInfo.value) {
      musicStore.addToPlaylist(songInfo.value, true)
    }
  }

  // 添加到播放列表
  function handleAddToPlaylist() {
    if (songInfo.value) {
      musicStore.addToPlaylist(songInfo.value, false)
    }
  }

  // 切换收藏状态
  async function handleToggleFavorite() {
    try {
      if (!songInfo.value?.songId) {
        return
      }
      await addFavorite({
        isFavorite: !songInfo.value.isFavorite,
        targetId: songInfo.value.songId,
        favoriteType: 'SONG'
      }).then((res) => {
        if (res.data) {
          songInfo.value!.isFavorite = !songInfo.value!.isFavorite
          toast.success(
            songInfo.value!.isFavorite ? t('common.followSuccess') : t('common.followCancelSuccess')
          )
        }
      })
    } catch (error) {
      toast.error(t('common.operationFailed'))
    }
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
        }
      }
      lyrics.value = parseLyrics(lyrContent)
    } catch (error) {
      console.error('获取歌词文件失败：', error)
    }
  }

  onMounted(async () => {
    const songId = route.params.id
    const { data } = await getSongDetail(Number(songId))
    songInfo.value = data
    if (!data) return
    const { data: audioAndLyrics } = await getSongAudioAndLyrics(data.songId)
    if (audioAndLyrics.songLyrics) {
      loadLyrics(audioAndLyrics.songLyrics)
    }
  })
</script>

<style scoped></style>
