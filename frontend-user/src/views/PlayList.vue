<template>
  <div class="playlist flex flex-col space-y-8">
    <!-- 标签选择器 -->
    <div class="tags-container bg-white/5 p-4 rounded-lg">
      <!-- 一级标签：标签类型选择 -->
      <div class="flex items-center flex-wrap gap-3 mb-4">
        <span class="text-white/60 w-20">{{ t('common.tagType') }}：</span>
        <div
          v-for="type in tags"
          :key="type.tagType"
          @click="
            () => {
              selectTagType(type.tagType)
            }
          "
          :class="[
            'px-3 py-1 rounded-full cursor-pointer transition-colors text-sm',
            selectedTagType === type.tagType
              ? 'bg-blue text-white'
              : 'bg-white/10 text-white/60 hover:bg-white/20'
          ]"
        >
          {{ type.tagType }}
        </div>
      </div>
      <!-- 二级标签：标签选择 -->
      <div class="flex items-center flex-wrap gap-3">
        <span class="text-white/60 w-20"></span>
        <div
          v-for="tag in currentTags"
          :key="tag.tagId"
          @click="
            () => {
              selectTag(tag)
            }
          "
          :class="[
            'px-3 py-1 rounded-full cursor-pointer transition-colors text-sm',
            currentTag?.tagId === tag.tagId
              ? 'bg-blue text-white'
              : 'bg-white/10 text-white/60 hover:bg-white/20'
          ]"
        >
          {{ tag.tagName }}
        </div>
      </div>
    </div>

    <!-- 歌单列表 -->
    <div class="grid grid-cols-5 gap-6 flex-1">
      <router-link
        v-for="playlist in playlists"
        :key="playlist.id"
        :to="`/playlist/${playlist.playlistId}`"
        class="group"
      >
        <Card>
          <div class="relative overflow-hidden rounded-lg aspect-square mb-2">
            <Image
              :src="playlist.playlistCover"
              type="playlist"
              :alt="playlist.playlistName"
              class="w-full h-full object-cover group-hover:scale-110 transition-transform duration-300"
            />
            <div
              class="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center"
            >
              <div
                class="w-12 h-12 rounded-full bg-blue/80 flex items-center justify-center cursor-pointer"
                @click.prevent="playPlaylist(playlist)"
              >
                <Icon icon="mdi:play" class="text-2xl text-white" />
              </div>
            </div>
          </div>
          <h3 class="text-white font-medium text-sm line-clamp-2">{{ playlist.playlistName }}</h3>
          <p class="text-white/60 text-xs mt-1">{{ playlist.playCount }} {{ t('common.play') }}</p>
        </Card>
      </router-link>
      <div v-if="playlists.length === 0" class="flex justify-center items-center h-full">
        <div class="text-white/60 text-lg">{{ t('common.noPlaylist') }}</div>
      </div>
    </div>

    <!-- 分页器 -->
    <div v-if="playlists.length !== 0" class="flex justify-center">
      <Pagination
        v-model:currentPage="currentPage"
        v-model:pageSize="pageSize"
        :total="total"
        @change="() => fetchPlaylists()"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted } from 'vue'
  import { Icon } from '@iconify/vue'
  import { getMusicTags, getPlaylistPage } from '@/api/modules/music/music'
  import type { Tags, Tag, PlayList } from '@/api/modules/music/music.d'
  import Pagination from '@/components/common/Pagination.vue'
  import { getPlayListDetail } from '@/api/modules/music/music'
  import { useMusicStore } from '@/stores/music'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const TagTypeConst = new Map([
    ['语种', 'LANGUAGE'],
    ['场景', 'SCENE'],
    ['主题', 'THEME'],
    ['心情', 'MOOD'],
    ['流派', 'GENRE']
  ])

  // 标签相关
  const tags = ref<Tags[]>([])
  const currentTag = ref<Tag | null>(null)
  const selectedTagType = ref<string>('')
  // 分组后的标签
  const currentTags = computed(() => {
    return tags.value.find((tag) => tag.tagType === selectedTagType.value)?.tagsBasicInfoList ?? []
  })

  // 分页相关
  const currentPage = ref(1)
  const pageSize = ref(20)
  const total = ref(0)
  const playlists = ref<PlayList[]>([])

  // 获取标签列表
  const fetchTags = async () => {
    try {
      const { data } = await getMusicTags()
      tags.value = data
    } catch (error) {
      console.error('获取标签失败：', error)
    }
  }

  // 获取歌单列表
  const fetchPlaylists = async (tagType?: string) => {
    try {
      const params = tagType
        ? {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            tagType: TagTypeConst.get(selectedTagType.value) ?? ''
          }
        : {
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            tagId: currentTag.value?.tagId
          }
      const res = await getPlaylistPage(params)
      playlists.value = res.data.records
      total.value = res.data.total
    } catch (error) {
      console.error('获取歌单列表失败：', error)
    }
  }

  // 选择标签类型
  const selectTagType = (type: string) => {
    selectedTagType.value = selectedTagType.value === type ? '' : type
    if (currentTag.value?.tagType !== type) {
      currentTag.value = null
      currentPage.value = 1
      fetchPlaylists(type)
    }
  }

  // 选择标签
  const selectTag = (tag: Tag) => {
    currentTag.value = currentTag.value?.tagId === tag.tagId ? null : tag
    currentPage.value = 1
    fetchPlaylists()
  }

  const musicStore = useMusicStore()

  // 播放歌单
  const playPlaylist = async (playlist: PlayList) => {
    try {
      if (!playlist.playlistId) {
        return
      }
      const { data } = await getPlayListDetail({ playlistId: playlist.playlistId })
      if (data.songs.length === 0) {
        return
      }
      musicStore.setPlaylist(data.songs, playlist.playlistId)
      musicStore.setPlayListCurrentSong(0, true)
    } catch (error) {
      console.error('🚀 ~ error:', error)
    }
  }

  onMounted(() => {
    fetchTags()
    fetchPlaylists()
  })
</script>

<style lang="scss" scoped></style>
