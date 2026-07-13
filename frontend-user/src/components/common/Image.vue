<template>
  <img :src="imgSrc" :alt="alt" v-bind="$attrs" @error="handleError" />
</template>

<script setup lang="ts">
  import { ref, watch } from 'vue'

  defineOptions({
    name: 'Image'
  })

  const props = withDefaults(
    defineProps<{
      src?: string | null
      alt?: string
      type?: 'avatar' | 'album' | 'playlist' | 'song' | 'singer' | 'default'
    }>(),
    {
      type: 'default'
    }
  )

  // 默认图片路径
  const defaultImages: Record<string, string> = {
    default: new URL('@/assets/images/default.png', import.meta.url).href,
    avatar: new URL('@/assets/images/default-avatar.png', import.meta.url).href,
    album: new URL('@/assets/images/default-album.png', import.meta.url).href,
    playlist: new URL('@/assets/images/default-playlist.png', import.meta.url).href,
    song: new URL('@/assets/images/default-song.png', import.meta.url).href,
    singer: new URL('@/assets/images/default-singer.png', import.meta.url).href
  }

  const imgSrc = ref(props.src || '')

  watch(
    () => props.src,
    (val) => {
      imgSrc.value = val || ''
    }
  )

  function handleError() {
    imgSrc.value = defaultImages[props.type ?? 'default'] || ''
  }
</script>
