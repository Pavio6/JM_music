<template>
  <div class="relative overflow-hidden rounded-xl w-full h-[300px]">
    <div
      class="flex"
      :class="{ 'transition-transform duration-500 ease-in-out': transitionEnabled }"
      :style="{ transform: `translateX(-${currentTranslate}%)` }"
      @transitionend="handleTransitionEnd"
    >
      <!-- 克隆最后一张 -->
      <div
        class="min-w-full h-full relative"
        v-if="slides.length"
        @click="handleSlideClick(slides[slides.length - 1])"
      >
        <Image
          :src="slides[slides.length - 1][imgKey]"
          :alt="slides[slides.length - 1][titleKey]"
          class="w-full h-full object-cover rounded-2xl"
        />
      </div>

      <!-- 真实 slides -->
      <div
        v-for="(slide, index) in slides"
        :key="index"
        class="min-w-full h-full relative cursor-pointer"
        @click="handleSlideClick(slide)"
      >
        <Image
          :src="slide[imgKey]"
          :alt="slide[titleKey]"
          class="w-full h-full object-cover rounded-2xl"
        />
        <div
          v-if="showText"
          class="absolute bottom-0 left-0 right-0 p-6 bg-gradient-to-t from-black/60 to-transparent rounded-2xl"
        >
          <h3 class="text-white text-xl font-bold">{{ slide[titleKey] }}</h3>
          <p v-if="showDescription" class="text-white/80 mt-2">{{ slide[descriptionKey] }}</p>
        </div>
      </div>

      <!-- 克隆第一张 -->
      <div
        class="min-w-full h-full relative"
        v-if="slides.length"
        @click="handleSlideClick(slides[0])"
      >
        <Image
          :src="slides[0][imgKey]"
          :alt="slides[0][titleKey]"
          class="w-full h-full object-cover rounded-2xl"
        />
      </div>
    </div>

    <!-- 导航按钮 -->
    <Button class="absolute left-2 top-1/2 -translate-y-1/2 px-2! z-10" rounded @click="prevSlide">
      <Icon icon="icon-park-outline:left" class="text-2xl" />
    </Button>
    <Button class="absolute right-2 top-1/2 -translate-y-1/2 px-2! z-10" rounded @click="nextSlide">
      <Icon icon="icon-park-outline:right" class="text-2xl" />
    </Button>

    <!-- 分页圆点 -->
    <div class="absolute bottom-4 left-0 right-0 flex justify-center gap-2">
      <span
        v-for="(_, i) in slides"
        :key="i"
        class="w-2 h-2 rounded-full transition-all cursor-pointer"
        :class="i === realIndex ? 'bg-white' : 'bg-white/50'"
        @click="goToSlide(i)"
      ></span>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
  import { Icon } from '@iconify/vue'
  import Image from './common/Image.vue'
  import Button from './common/Button.vue'

  const props = defineProps({
    slides: { type: Array, default: () => [] },
    imgKey: { type: String, default: 'image' },
    titleKey: { type: String, default: 'title' },
    descriptionKey: { type: String, default: 'description' },
    showText: { type: Boolean, default: true },
    showDescription: { type: Boolean, default: true },
    autoplay: { type: Boolean, default: true },
    delay: { type: Number, default: 5000 },
    loop: { type: Boolean, default: true }
  })

  const emit = defineEmits(['onPageClick', 'onSlideClick'])

  const totalSlides = () => props.slides.length
  const currentIndex = ref(1) // 初始指向真实第 0 项（被包在中间）
  const transitioning = ref(true)
  const transitionEnabled = ref(true)

  const currentTranslate = computed(() => currentIndex.value * 100)
  const realIndex = computed(() => {
    if (currentIndex.value === 0) return totalSlides() - 1
    if (currentIndex.value === totalSlides() + 1) return 0
    return currentIndex.value - 1
  })

  const nextSlide = () => {
    if (totalSlides() <= 1) return
    transitionEnabled.value = true
    currentIndex.value++
  }

  const prevSlide = () => {
    if (totalSlides() <= 1) return
    transitionEnabled.value = true
    currentIndex.value--
  }

  const handleTransitionEnd = () => {
    // 到克隆最后的 slide，瞬间跳转回真实第一张
    if (currentIndex.value === totalSlides() + 1) {
      transitionEnabled.value = false
      currentIndex.value = 1
    }

    // 到克隆第一张 slide，瞬间跳转到真实最后一张
    if (currentIndex.value === 0) {
      transitionEnabled.value = false
      currentIndex.value = totalSlides()
    }
  }

  const goToSlide = (i) => {
    currentIndex.value = i + 1
    emit('onPageClick', i)
  }

  const handleSlideClick = (slide) => {
    emit('onSlideClick', slide)
  }

  // 自动播放逻辑
  let timer = null
  const startAutoplay = () => {
    if (props.autoplay && totalSlides() > 1) {
      timer = setInterval(() => {
        nextSlide()
      }, props.delay)
    }
  }
  const stopAutoplay = () => {
    if (timer) clearInterval(timer)
  }
  onMounted(() => {
    startAutoplay()
  })
  onBeforeUnmount(() => {
    stopAutoplay()
  })

  watch(currentIndex, () => {
    // 下一个 tick 恢复动画
    if (!transitionEnabled.value) {
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          transitionEnabled.value = true
        })
      })
    }
  })

  // 监听 autoplay 开关
  watch(
    () => props.autoplay,
    (newVal) => {
      stopAutoplay()
      if (newVal) startAutoplay()
    }
  )
</script>

<style scoped>
  .flex {
    will-change: transform;
  }
</style>
