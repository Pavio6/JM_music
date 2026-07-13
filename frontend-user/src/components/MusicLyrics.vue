<template>
  <div class="flex flex-col music-lyrics w-full h-full">
    <div class="flex-1 relative">
      <div ref="lyricsContainer" class="absolute inset-0 overflow-hidden lyrics-container">
        <div v-if="currentLyrics.length" class="space-y-4">
          <div
            v-for="(line, index) in currentLyrics"
            :key="index"
            :class="[
              'transform will-change-transform will-change-opacity lyric-line',
              currentLyricIndex === index
                ? 'text-white text-2xl font-medium glow active-lyric'
                : 'text-white/40 text-2xl blur-0.6'
            ]"
          >
            {{ line.text }}
          </div>
        </div>
        <div v-else class="text-white/40 text-center mt-10">
          {{ t('common.noLyrics') }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { computed, ref, watch } from 'vue'
  import { useMusicStore } from '@/stores/music'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const musicStore = useMusicStore()
  const lyricsContainer = ref<HTMLElement | null>(null)

  // 歌词相关
  const currentLyrics = computed(() => musicStore.currentLyrics ?? [])
  const currentLyricIndex = computed(() => musicStore.currentLyricIndex)

  // 监听当前歌词索引的变化，实现滚动效果
  watch(currentLyricIndex, (newIndex) => {
    if (newIndex === -1 || !lyricsContainer.value) return

    const lyricElements = lyricsContainer.value.getElementsByClassName('lyric-line')
    if (!lyricElements.length) return

    const currentLyric = lyricElements[newIndex] as HTMLElement
    const containerHeight = lyricsContainer.value.clientHeight
    const lyricHeight = currentLyric.clientHeight
    const scrollTop = currentLyric.offsetTop - containerHeight / 2 + lyricHeight / 2

    // 使用 requestAnimationFrame 优化滚动性能
    requestAnimationFrame(() => {
      lyricsContainer.value?.scrollTo({
        top: scrollTop,
        behavior: 'smooth'
      })
    })
  })
</script>

<style lang="scss" scoped>
  .lyrics-container {
    -webkit-overflow-scrolling: touch;
    scroll-behavior: smooth;
    padding: 0 4px;
  }

  .lyric-line {
    transition: all 0.25s cubic-bezier(0.25, 0.1, 0.25, 1);
    transform: translateZ(0); /* 启用硬件加速 */
    padding: 2px 0;
  }

  .active-lyric {
    transform: scale(1.01) translateZ(0);
  }

  .glow {
    text-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  }
</style>
