<template>
  <Teleport to="body">
    <Transition name="fade">
      <div class="bg-white/10 backdrop-blur-md fullscreen-mode">
        <div
          class="flex flex-row items-center justify-between gap-8 p-10 flex-1 bg-black/75 relative"
        >
          <!-- 退出全屏按钮 -->
          <div
            class="absolute top-4 right-4 text-white/80 hover:text-white transition-colors cursor-pointer z-10"
          >
            <Icon icon="mdi:fullscreen-exit" class="text-2xl" @click="exitFullscreen" />
          </div>
          <!-- 左侧视频播放区 -->
          <div class="flex-1 flex flex-col justify-center items-center">
            <div ref="videoContainer" class="w-full h-[60vh] flex items-center justify-center">
              <video
                ref="videoRef"
                class="w-full h-full bg-black pointer-events-auto"
                controls
              ></video>
            </div>
            <div class="flex space-x-2 mt-4">
              <Button
                v-for="res in resolutions"
                :key="res.value"
                :type="currentResolution === res.value ? 'primary' : 'default'"
                @click="changeResolution(res.value)"
              >
                {{ res.label }}
              </Button>
            </div>
            <!-- 信息区... -->
            <div class="mt-6 w-full text-white/80 text-left space-y-2" v-if="mvDetail">
              <div>
                <span class="font-semibold">歌手：</span>{{ mvDetail.singerName || '未知' }}
              </div>
              <div>
                <span class="font-semibold">发行日期：</span>
                {{ mvDetail.mvReleaseDate || '未知' }}
              </div>
              <div><span class="font-semibold">简介：</span>{{ mvDetail.mvBio || '暂无简介' }}</div>
            </div>
          </div>
          <!-- 右侧评论区 -->
          <div
            v-if="props.mvId"
            class="w-[40vw] min-w-[450px] h-full border-l border-white/10 p-10 flex flex-col"
          >
            <Card>
              <Comment :targetId="String(props.mvId)" targetType="MV" class="flex-1" />
            </Card>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script lang="ts" setup>
  import { ref, onMounted, onUnmounted, watch, defineProps } from 'vue'
  import { Icon } from '@iconify/vue'
  import { getMvDetail } from '@/api/modules/musicVideo/musicVideo'
  import type { MvDetail } from '@/api/modules/musicVideo/musicVideo.d'
  import { useMusicStore } from '@/stores/music'
  import { updatePlayCount } from '@/api/modules/music/music'

  const props = defineProps<{ mvId: string | number }>()
  const emit = defineEmits(['close'])
  const mvId = props.mvId
  const musicStore = useMusicStore()
  const videoRef = ref<HTMLVideoElement | null>(null)
  const videoContainer = ref<HTMLElement | null>(null)
  const mvDetail = ref<MvDetail | null>(null)
  const resolutions = ref([
    { label: '1080P', value: 1080 },
    { label: '720P', value: 720 },
    { label: '480P', value: 480 }
  ])
  const currentResolution = ref(1080)

  const getVideoUrl = (detail: MvDetail, res: number) => {
    if (res === 1080 && detail.mvFilePath1080p) {
      return detail.mvFilePath1080p
    } else if (res === 720 && detail.mvFilePath720p) {
      return detail.mvFilePath720p
    } else if (res === 480 && detail.mvFilePath480p) {
      return detail.mvFilePath480p
    } else {
      return ''
    }
  }

  const loadMvDetail = async () => {
    if (!mvId) {
      return
    }
    const res = await getMvDetail(mvId)
    mvDetail.value = res.data
    const { mvFilePath1080p, mvFilePath480p, mvFilePath720p } = res.data
    if (mvDetail.value) {
      // 默认选择最高分辨率
      currentResolution.value = mvFilePath1080p
        ? 1080
        : mvFilePath720p
          ? 720
          : mvFilePath480p
            ? 480
            : 0
      setVideoSrc()
      updatePlayCount(mvId, 'MV')
    }
  }

  const setVideoSrc = () => {
    if (!mvDetail.value || !videoRef.value) return
    const url = getVideoUrl(mvDetail.value, currentResolution.value)
    const video = videoRef.value
    if (video) {
      videoRef.value.src = url
      video.load()
      video.oncanplay = () => {
        video.muted = false
        video.volume = 1.0
        video
          .play()
          .then(() => {
            console.log('播放成功')
            // 暂停音乐
            if (musicStore.isPlaying) {
              musicStore.togglePlay()
            }
          })
          .catch((err) => {
            console.warn('播放失败:', err)
          })
      }
    }
  }

  const changeResolution = (res: number) => {
    if (currentResolution.value !== res) {
      currentResolution.value = res
      setVideoSrc()
    }
  }

  const exitFullscreen = () => {
    if (videoRef.value) {
      videoRef.value.pause()
      videoRef.value.src = ''
    }
    emit('close')
  }

  onMounted(() => {
    loadMvDetail()
  })

  onUnmounted(() => {
    if (videoRef.value) {
      videoRef.value.pause()
      videoRef.value.src = ''
    }
  })

  watch(currentResolution, () => {
    setVideoSrc()
  })
</script>

<style lang="scss" scoped>
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
</style>
