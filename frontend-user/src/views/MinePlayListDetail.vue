<template>
  <div class="playlist-detail space-y-6">
    <div class="flex">
      <Button @click="back">
        <Icon icon="mdi:arrow-left" class="text-xl" />
        {{ t('common.back') }}
      </Button>
    </div>
    <Card size="lg">
      <div class="flex items-start gap-6">
        <!-- 歌单封面 -->
        <div class="cover-section">
          <Image
            type="playlist"
            :src="playlistInfo?.playlistCover"
            :alt="playlistInfo?.playlistName"
            class="w-32 h-32 rounded-lg object-cover"
          />
          <template v-if="isEdit">
            <FileInput @change="handleAvatarUpload" accept="image/*" :show-Text="false" />
          </template>
        </div>
        <!-- 歌单信息 -->
        <div class="flex-1 min-w-50">
          <template v-if="isEdit">
            <Input
              v-model="playlistInfo.playlistName"
              :placeholder="t('common.inputPlaylistName')"
              :maxlength="20"
              class="text-2xl font-bold px-0! pb-1! pt-2!"
            />
          </template>
          <template v-else>
            <h1 class="text-2xl font-bold mb-2">{{ playlistInfo?.playlistName }}</h1>
          </template>
          <div class="flex items-center gap-4 text-gray-600 mb-4">
            <div class="creator flex items-center gap-2">
              <Image
                type="playlist"
                :src="playlistInfo?.userAvatar"
                :alt="playlistInfo?.userName"
                class="w-6 h-6 rounded-full"
              />
              <span>{{ playlistInfo.userName }}</span>
            </div>
          </div>
          <template v-if="isEdit">
            <TextArea
              v-model="playlistInfo.playlistBio"
              :placeholder="t('common.inputPlaylistBio')"
              :maxlength="200"
              class="px-0! py-1!"
            />
          </template>
          <template v-else>
            <p class="text-gray-600">
              {{ playlistInfo?.playlistBio || t('common.noPlaylistBio') }}
            </p>
          </template>
          <!-- 歌单标签编辑 -->
          <template v-if="isEdit">
            <FilterSelect v-model="playlistInfo.tagIds!" :tags="tags" :maxlength="3" />
          </template>
          <template v-else>
            <div
              v-if="playlistInfo?.tagName && playlistInfo.tagName.length"
              class="flex flex-wrap gap-2 mt-2"
            >
              <span
                v-for="tag in playlistInfo.tagName"
                :key="tag"
                class="text-blue-600 text-xs cursor-auto"
                >#{{ tag }}</span
              >
            </div>
          </template>
        </div>
        <div class="flex gap-4 flex-wrap min-w-40">
          <template v-if="isEdit">
            <Button type="default" @click="handleCancel">
              <Icon icon="solar:undo-left-broken" class="mr-1" />
              {{ t('common.cancel') }}
            </Button>
            <Button @click="openModal('delete')" type="danger">
              <Icon icon="mdi:delete" />
              {{ t('common.deletePlaylist') }}
            </Button>
            <Button @click="updateMinePlaylist" type="success" :loading="upLoading">
              <Icon icon="mingcute:refresh-4-ai-line" class="mr-1" />
              {{ t('common.updateInfo') }}
            </Button>
          </template>
          <template v-else>
            <Button type="primary" @click="playAllSongs">
              <Icon icon="mdi:play" class="mr-1" />
              {{ t('common.playAll') }}
            </Button>
            <Button @click="handleEdit">
              <Icon icon="material-symbols-light:edit-document-outline-rounded" class="mr-1" />
              {{ t('common.editInfo') }}
            </Button>
          </template>
        </div>
      </div>
    </Card>

    <!-- 歌曲列表 -->
    <Card size="lg">
      <div class="flex justify-between items-center mb-4">
        <div class="flex flex-row gap-2 items-center">
          <h2 class="text-xl font-bold">{{ t('common.songList') }}</h2>
          <template v-if="isEdit">
            <Button @click="openModal('add')">
              <Icon icon="mdi:add" />
            </Button>
          </template>
        </div>
        <div class="text-gray-600">共 {{ playlistInfo?.songs?.length || 0 }} 首</div>
      </div>
      <Table :columns="columns" :data="playlistInfo?.songs || []">
        <template #cell-index="{ index }">
          {{ index + 1 }}
        </template>
        <template #cell-songName="{ row }">
          <div class="flex items-center gap-3">
            <Image type="song" :src="row.songCover" :alt="row.songName" class="w-10 h-10 rounded" />
            <router-link :to="`/song/${row.songId}`" class="text-blue-600 hover:text-blue-800">
              {{ row.songName }}
            </router-link>
            <!-- MV -->
            <template v-if="row.mvId">
              <Icon
                icon="mingcute:video-line"
                class="text-blue-600 cursor-pointer"
                @click="handlePlayVideo(row.mvId)"
              />
            </template>
          </div>
        </template>
        <template #cell-singerName="{ row }">
          <router-link :to="`/singer/${row.singerId}`" class="text-blue-600 hover:text-blue-800">
            {{ row.singerName }}
          </router-link>
        </template>
        <template #cell-albumName="{ row }">
          <router-link :to="`/album/${row.albumId}`" class="text-blue-600 hover:text-blue-800">
            {{ row.albumName }}
          </router-link>
        </template>
        <template #cell-duration="{ row }">
          {{ row.songDuration }}
        </template>
        <template #cell-actions="{ row }">
          <div class="flex flex-row space-x-2">
            <template v-if="isEdit">
              <Button @click="handleDelete(row)" type="danger">
                <Icon icon="ri:delete-bin-line" />
              </Button>
            </template>
            <template v-else>
              <Button @click="addToPlaylist(row, true)" inline>
                <Icon icon="famicons:play" />
              </Button>
              <Button @click="addToPlaylist(row, false)" inline>
                <Icon icon="mdi:playlist-plus" />
              </Button>
            </template>
          </div>
        </template>
      </Table>
    </Card>

    <!-- 评论模块 -->
    <template v-if="!isEdit">
      <Card size="lg">
        <Comment
          :targetId="Array.isArray(route.params.id) ? route.params.id[0] : route.params.id"
          targetType="PLAYLIST"
        />
      </Card>
    </template>

    <!-- 编辑歌单 -->
    <template v-if="isEdit">
      <Modal ref="ModalRef">
        <template v-if="modalType === 'delete'">
          <div class="text-center w-full">
            <p class="mb-4 text-lg font-bold text-white">{{ t('common.confirmDeletePlaylist') }}</p>
            <div class="flex justify-center gap-4">
              <Button type="default" @click="closeModal" :loading="modalLoading">
                {{ t('common.cancel') }}
              </Button>
              <Button type="danger" @click="deleteMinePlaylist" :loading="modalLoading">{{
                t('common.delete')
              }}</Button>
            </div>
          </div>
        </template>
        <template v-if="modalType === 'add'">
          <div class="w-full flex flex-col gap-4">
            <div class="flex flex-row gap-2 mb-2">
              <Input
                v-model.trim="searchSongName"
                :placeholder="t('common.inputSongNameSearch')"
                class="flex-1"
                @keyup.enter="fetchSongList"
                :maxlength="20"
              />
              <Button type="primary" @click="fetchSongList" :loading="searchLoading">
                {{ t('common.search') }}
              </Button>
            </div>
            <Table :columns="searchSongColumns" :data="searchSongList">
              <template #cell-index="{ index }">
                <span class="text-blue-600">
                  {{ index + 1 }}
                </span>
              </template>
              <template #cell-songName="{ row }">
                <div class="flex items-center gap-3">
                  <Image
                    type="song"
                    :src="row.songCover"
                    :alt="row.songName"
                    class="w-10 h-10 rounded"
                  />
                  <span class="text-blue-600 hover:text-blue-800">
                    {{ row.songName }}
                  </span>
                </div>
              </template>
              <template #cell-singerName="{ row }">
                <span class="text-blue-600 hover:text-blue-800">
                  {{ row.singerName }}
                </span>
              </template>
              <template #cell-albumName="{ row }">
                <span class="text-blue-600 hover:text-blue-800">
                  {{ row.albumName }}
                </span>
              </template>
              <template #cell-duration="{ row }">
                {{ row.songDuration }}
              </template>
              <template #cell-actions="{ row }">
                <Button inline @click="toggleSongInPlaylist(row)">
                  <span v-if="isSongInPlaylist(row)">{{ t('common.remove') }}</span>
                  <span v-else>{{ t('common.add') }}</span>
                </Button>
              </template>
            </Table>
          </div>
        </template>
      </Modal>
    </template>
  </div>
</template>

<script setup lang="ts">
  import { ref, onMounted, inject } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import Table from '@/components/common/Table.vue'
  import Comment from '@/components/common/Comment.vue'
  import { getMusicTags, getPlayListDetail } from '@/api/modules/music/music'
  import type { PlayListDetail, Song } from '@/api/modules/music/music.d'
  import { useMusicStore } from '@/stores/music'
  import { toast } from '@/utile'
  import { Button, FileInput, Modal, TextArea, Card, Input } from '@/components/common'
  import { uploadImage } from '@/api/modules/common/common'
  import { add, cloneDeep, findKey } from 'lodash-es'
  import {
    addSongToPlayList,
    deletePlayList,
    editPlayList,
    removeSongFromPlayList
  } from '@/api/modules/playList/playList'
  import FilterSelect from '@/components/common/FilterSelect/FilterSelect.vue'
  import type { TagSelectTag } from '@/components/common/FilterSelect/DropdownTagSelect.vue'
  import { getSongList } from '@/api/modules/user/user'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()
  const route = useRoute()
  const playlistInfo = ref<Partial<PlayListDetail>>({})
  const musicStore = useMusicStore()
  const isEdit = ref(false)
  const upLoading = ref(false)
  const ModalRef = ref<InstanceType<typeof Modal> | null>(null)
  const modalType = ref<'add' | 'delete'>('add')
  const modalLoading = ref(false)

  const columns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.songName') },
    { key: 'singerName', title: t('common.singerName') },
    { key: 'albumName', title: t('common.albumName') },
    { key: 'duration', title: t('common.duration') },
    { key: 'actions', title: t('common.actions') }
  ]

  const router = useRouter()

  const openMv = inject('openMv') as (mvId: string | number) => void
  const handlePlayVideo = (musicVideo: string | number) => {
    if (openMv) {
      openMv(musicVideo)
    }
  }

  const back = () => {
    router.back()
  }

  const playAllSongs = () => {
    if (playlistInfo.value.songs?.length === 0) {
      toast.warning(t('common.noSongsInPlaylist'))
      return
    }
    musicStore.setPlaylist(playlistInfo.value.songs!, playlistInfo.value.playlistId)
    musicStore.setPlayListCurrentSong(0, true)
  }

  const addToPlaylist = (song: Song, playNow = false) => {
    musicStore.addToPlaylist(song, playNow)
  }

  const playlistInfoRaw = ref<Partial<PlayListDetail> | null>(null)

  const handleEdit = () => {
    playlistInfoRaw.value = cloneDeep(playlistInfo.value)
    refreshTags()
    isEdit.value = true
  }

  const handleDelete = (song: Song) => {
    toggleSongInPlaylist(song)
  }

  const handleCancel = () => {
    playlistInfo.value = playlistInfoRaw.value ?? {}
    isEdit.value = false
  }

  // 获取标签列表
  const tags = ref<TagSelectTag[]>([])
  const refreshTags = async () => {
    tags.value = []
    const { data } = await getMusicTags()
    tags.value = data.map((tag) => ({
      label: tag.tagType,
      value: tag.tagType,
      children: tag.tagsBasicInfoList?.map((child) => ({
        label: child.tagName,
        value: child.tagId
      }))
    }))
  }

  // 处理头像上传
  const actionsFile = ref<File | null>(null)
  const handleAvatarUpload = (event: Event) => {
    const input = event.target as HTMLInputElement
    if (input.files && input.files[0]) {
      actionsFile.value = input.files?.[0]
      uploadImage({
        file: input.files[0] as File,
        uploadFileType: 'CHAT_IMAGE'
      }).then((res) => {
        if (res.data) {
          playlistInfo.value!.playlistCover = res.data
        }
      })
    }
  }

  const updateMinePlaylist = async () => {
    const playlistId = route.params.id as string
    if (!playlistInfo.value) {
      return
    }
    try {
      upLoading.value = true
      const formBody: any = {
        playlistName: playlistInfo.value.playlistName,
        playlistBio: playlistInfo.value.playlistBio,
        tagIds: playlistInfo.value.tagIds
      }
      if (actionsFile.value) {
        formBody.playlistCover = actionsFile.value
      }
      await editPlayList(playlistId, formBody).then((res) => {
        if (res.data) {
          isEdit.value = false
          actionsFile.value = null
          playlistInfoRaw.value = null
          toast.success(t('common.updateSuccess'))
        } else {
          throw new Error(res.message)
        }
      })
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message || t('common.updateFailed'))
      }
    } finally {
      upLoading.value = false
    }
  }

  const openModal = (type: 'add' | 'delete') => {
    if (!ModalRef.value) {
      return
    }
    modalType.value = type
    if (type === 'add') {
      ModalRef.value.open({
        title: t('common.addSong'),
        showConfirm: false,
        showCancel: false,
        width: '800px'
      })
    } else if (type === 'delete') {
      ModalRef.value.open({
        title: t('common.deletePrompt'),
        showConfirm: false,
        showCancel: false
      })
    }
  }

  const closeModal = () => {
    if (!ModalRef.value) {
      return
    }
    ModalRef.value.close()
  }

  const deleteMinePlaylist = async () => {
    const playlistId = route.params.id as string
    modalLoading.value = true
    try {
      await deletePlayList(playlistId).then((res) => {
        if (res.data) {
          toast.success(t('common.deleteSuccess'))
          router.push('/user')
        } else {
          throw new Error(res.message)
        }
      })
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message)
      }
    } finally {
      setTimeout(() => {
        modalLoading.value = false
      }, 200)
    }
  }

  const searchSongName = ref('')
  const searchSongList = ref<Song[]>([])
  const searchSongColumns = [
    { key: 'index', title: '#' },
    { key: 'songName', title: t('common.songName') },
    { key: 'singerName', title: t('common.singerName') },
    { key: 'albumName', title: t('common.albumName') },
    { key: 'actions', title: t('common.actions') }
  ]
  const searchLoading = ref(false)

  const fetchSongList = async () => {
    if (!searchSongName.value) {
      searchSongList.value = []
      return
    }
    try {
      searchLoading.value = true
      const { data } = await getSongList({ songName: searchSongName.value })
      if (!data) {
        throw new Error(t('common.getSongListFailed'))
      } else if (data.length === 0) {
        toast.error(t('common.noRelatedSongs'))
      }
      searchSongList.value = data || []
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message)
      }
    } finally {
      setTimeout(() => {
        searchLoading.value = false
      }, 200)
    }
  }

  function isSongInPlaylist(song: Song) {
    return playlistInfo.value.songs?.some((item) => item.songId === song.songId)
  }

  function toggleSongInPlaylist(song: Song) {
    if (!playlistInfo.value.songs) playlistInfo.value.songs = []
    const idx = playlistInfo.value.songs.findIndex((item) => item.songId === song.songId)
    if (idx > -1) {
      // 存在则删除
      removeSongFromPlayList({
        playlistId: playlistInfo.value.playlistId!,
        songIds: [song.songId]
      }).then((res) => {
        if (res.data) {
          playlistInfo.value.songs!.splice(idx, 1)
          toast.success(t('common.removeFromPlaylistSuccess'))
        } else {
          toast.error(t('common.operationFailed'))
        }
      })
    } else {
      // 不存在则添加
      addSongToPlayList({
        playlistId: playlistInfo.value.playlistId!,
        songIds: [song.songId]
      }).then((res) => {
        if (res.data) {
          playlistInfo.value.songs!.push(song)
          toast.success(t('common.addToPlaylistSuccess'))
        } else {
          toast.error(t('common.operationFailed'))
        }
      })
    }
  }

  onMounted(async () => {
    const playlistId = route.params.id as string
    const { data } = await getPlayListDetail({ playlistId })
    playlistInfo.value = data ?? {}
  })
</script>

<style scoped></style>
