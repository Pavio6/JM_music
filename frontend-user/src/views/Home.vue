<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <div class="flex mb-6">
      <HeadBar />
    </div>
    <!-- 轮播图 -->
    <div class="flex mb-6">
      <Carousel
        :slides="slides"
        @onSlideClick="handleSlideClick"
        imgKey="imageUrl"
        titleKey="title"
        descriptionKey="subTitle"
        :showText="false"
      />
    </div>
    <!-- 热门歌手 -->
    <div class="flex mb-6">
      <Inventory
        :title="t('common.hotSinger')"
        :data="hotSinger"
        @onItemClick="
          (item) => {
            router.push({ path: `/singer/${item.id}` })
          }
        "
        :empty-text="t('common.noHotSinger')"
        :total-pages="hotSingerTotalPages"
        @onPageClick="(pageNum) => refreshList(pageNum)"
      />
    </div>
    <!-- 推荐歌单 -->
    <div class="flex">
      <Inventory
        :title="t('common.hotSongList')"
        :data="hotSongList"
        showMore
        :moreFnc="moreFnc"
        @onItemClick="
          (item) => {
            router.push({ path: `/playlist/${item.id}` })
          }
        "
        :empty-text="t('common.noHotSongList')"
        :total-pages="hotSongListTotalPages"
        @onPageClick="(pageNum) => refreshSongList(pageNum)"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import HeadBar from '@/components/HeadBar.vue'
  import Carousel from '@/components/Carousel.vue'
  import Inventory from '@/components/Inventory.vue'
  import { getSingerList } from '@/api/modules/singer/signer'
  import { getPlaylistPage } from '@/api/modules/music/music'
  import { getCarouselList } from '@/api/modules/common'
  import { toast } from '@/utile'
  import type { Carousel as CarouselType } from '@/api/api.d'
  import { sortBy } from 'lodash-es'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const router = useRouter()

  interface ListItem {
    id: number | string
    title: string
    subTitle?: string
    cover: string
    tag?: string
    [key: string]: any
  }

  const slides = ref<CarouselType[]>([])
  /**
   * 当targetType为 0/1/2 也就是歌曲/歌单/专辑时 必须有targetId，用来访问它们的详情信息
   * 当targetType为 3/4 也就是文章/外部链接时 必须要externalUrl，用来跳转到其他链接
   */
  const handleSlideClick = (item: CarouselType) => {
    if (item.targetType === 0) {
      router.push(`/song/${item.targetId}`)
    } else if (item.targetType === 1) {
      router.push(`/playlist/${item.targetId}`)
    } else if (item.targetType === 2) {
      router.push(`/album/${item.targetId}`)
    } else if ((item.targetType === 3 || item.targetType === 4) && item.externalUrl) {
      window.open(item.externalUrl, '_blank')
    } else {
      // toast.error('未知类型')
    }
  }

  // 获取轮播图
  function refreshSlides() {
    getCarouselList()
      .then((res) => {
        slides.value = sortBy(res.data, ['sortOrder'])
      })
      .catch((err) => {
        console.log(err)
        toast.error(t('common.getFailed'))
      })
  }

  // 获取热门歌手
  const hotSinger = ref<ListItem[]>([])
  const hotSingerTotalPages = ref(0)
  async function refreshList(pageNum = 1, pageSize = 5) {
    const res = await getSingerList({ pageNum, pageSize })
    hotSinger.value = res.data.records.map((item, index) => {
      return {
        id: item.singerId || '',
        title: item.singerName ?? '',
        cover: item.singerAvatar ?? ''
      }
    })
    hotSingerTotalPages.value = res.data.pages
  }

  // 获取推荐歌单
  const hotSongList = ref<ListItem[]>([])
  const hotSongListTotalPages = ref(0)
  async function refreshSongList(pageNum = 1, pageSize = 5) {
    const res = await getPlaylistPage({ pageNum, pageSize })
    hotSongList.value = res.data.records.map((item, index) => {
      return {
        id: item.playlistId || '',
        title: item.playlistName,
        cover: item.playlistCover ?? ''
      }
    })
    hotSongListTotalPages.value = res.data.pages
  }

  onMounted(() => {
    refreshList(1)
    refreshSongList(1)
    refreshSlides()
  })

  function moreFnc() {
    router.push({ path: '/playlist' })
  }
</script>

<style lang="scss" scoped></style>
