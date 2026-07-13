<template>
  <div class="Message">
    <Card no-padding>
      <div class="flex h-[calc(100vh-8rem)] rounded-xl overflow-hidden">
        <!-- 左侧会话列表 -->
        <div class="w-1/4 border-r-solid border-r-2 border-[#23272F] flex flex-col">
          <div class="p-4 border-b-solid border-b-2 border-[#23272F] flex items-center">
            <Image
              v-if="userInfo.userAvatar"
              :src="userInfo.userAvatar"
              type="avatar"
              :alt="t('common.myAvatar')"
              class="w-10 h-10 rounded-full object-cover mr-3"
            />
            <div class="flex-1">
              <div class="font-bold text-white">
                {{ userInfo.userName || t('common.userInfo') }}
              </div>
              <div class="text-xs text-green-500 mt-1">{{ t('common.online') }}</div>
            </div>
          </div>
          <div class="px-4 py-4 flex flex-row gap-2">
            <Input
              :placeholder="t('common.searchUserOrMessage')"
              class="w-full border-solid border-2 border-[#23272F]"
              v-model="searchText"
              type="text"
            />
            <Button type="primary" class="py-1!" @click="openAddFriendModal">
              <Icon icon="weui:add-friends-outlined" class="text-size-xl" />
            </Button>
          </div>
          <!-- 用户 -->
          <div class="flex-1 overflow-y-auto">
            <Card
              v-for="(conversation, index) in conversations.filter((i) =>
                i.otherUserName.includes(searchText)
              )"
              :key="index"
              :size="'sm'"
              class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-all duration-200 rounded-lg mb-1 hover:bg-[#23272F]"
              :class="{
                'bg-[#23272F] border-l-4 border-primary':
                  activeConversation === conversations[index]
              }"
              @click="selectConversation(index)"
            >
              <Image
                v-if="conversation.otherUserAvatar"
                :src="conversation.otherUserAvatar"
                type="avatar"
                :alt="t('common.avatar')"
                class="w-10 h-10 rounded-full object-cover border-2 border-[#23272F]"
              />
              <div
                v-else
                class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center"
              >
                <Icon icon="duo-icons:user" class="text-size-2xl text-gray-400" />
              </div>
              <div class="flex-1 min-w-0">
                <div class="font-medium text-white truncate">
                  {{ conversation.otherUserName }}
                  <span v-if="conversation.online" class="text-xs text-gray-400">
                    {{ t('common.online') }}
                  </span>
                </div>
                <div class="text-xs text-gray-400 truncate mt-1">
                  <template v-if="conversation.messageType === 'TEXT'">
                    {{ conversation.lastMessageContent }}
                  </template>
                  <template v-else-if="conversation.messageType === 'IMAGE'">
                    <span class="text-gray-400">{{ t('common.image') }}</span>
                  </template>
                </div>
              </div>
              <div
                v-if="conversation.unreadCount > 0"
                class="ml-2 w-5 h-5 rounded-full bg-primary flex items-center justify-center text-xs bg-red text-white font-bold"
              >
                {{ conversation.unreadCount }}
              </div>
            </Card>
            <div v-if="conversations.length === 0" class="p-4 text-center text-gray-400">
              {{ t('common.noConversation') }}
            </div>
          </div>
        </div>

        <!-- 右侧消息区域 -->
        <div class="w-3/4 flex flex-col">
          <!-- 消息头部 -->
          <div
            class="p-4 border-b-solid border-b-2 border-[#23272F] flex items-center justify-between"
          >
            <div class="flex items-center gap-3">
              <Image
                v-if="activeConversation && activeConversation.otherUserAvatar"
                :src="activeConversation.otherUserAvatar"
                type="avatar"
                :alt="t('common.avatar')"
                class="w-10 h-10 rounded-full object-cover"
              />
              <div
                v-else
                class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center"
              >
                <Icon icon="duo-icons:user" class="text-size-2xl text-gray-400" />
              </div>
              <div class="ml-2">
                <template v-if="activeConversation">
                  <div
                    class="font-bold text-white text-lg cursor-pointer hover:text-blue-500"
                    @click="toUserDetail(activeConversation.otherUserId)"
                  >
                    {{ activeConversation.otherUserName }}
                  </div>
                </template>
                <template v-else>
                  <div class="font-bold text-white text-lg">
                    {{ t('common.selectConversation') }}
                  </div>
                </template>
                <div
                  class="text-xs"
                  :class="
                    activeConversation && activeConversation.online
                      ? 'text-green-500'
                      : 'text-gray-400'
                  "
                >
                  {{
                    activeConversation
                      ? activeConversation.online
                        ? t('common.online')
                        : t('common.offline')
                      : ''
                  }}
                </div>
              </div>
            </div>
            <!-- <div class="flex gap-2">
              <Button @click="deleteConversation">
                <Icon icon="mdi:delete-outline" />
              </Button>
            </div> -->
          </div>

          <!-- 消息内容区域 -->
          <div class="flex-grow overflow-y-auto p-3" ref="messageContainer">
            <div v-if="activeConversation !== null">
              <div
                v-for="(message, index) in computedMessages"
                :key="index"
                class="mb-6 flex items-end"
                :class="{ 'flex-row-reverse': message.isSelf }"
              >
                <Image
                  v-if="message.userAvatar"
                  :src="message.userAvatar"
                  type="avatar"
                  :alt="t('common.avatar')"
                  class="w-9 h-9 rounded-full object-cover mx-3"
                />
                <div
                  v-else
                  class="w-9 h-9 rounded-full bg-gray-700 flex items-center justify-center mx-3"
                >
                  <Icon icon="duo-icons:user" class="text-size-xl text-gray-400" />
                </div>
                <div
                  class="flex flex-col max-w-[70%]"
                  :class="[message.isSelf ? 'items-end' : 'items-start']"
                >
                  <div class="flex flex-row items-end">
                    <template v-if="message.isSelf">
                      <Button
                        class="mr-2 text-[10px] p-1!"
                        @click="recallMessage(message)"
                        rounded
                        :loading="message.type === 'recalling'"
                      >
                        <Icon icon="lets-icons:return" class="text-size-sm" />
                      </Button>
                    </template>
                    <div
                      class="rounded-2xl px-4 py-3 shadow-md flex items-center gap-2 flex-row"
                      :class="message.isSelf ? 'bg-primary text-white' : 'bg-[#23272F] text-white'"
                    >
                      <template v-if="message.type === 'recall'">
                        <div class="text-base leading-relaxed text-gray-400">
                          {{ t('common.messageRecall') }}
                        </div>
                      </template>
                      <template v-else-if="message.messageType === 'IMAGE'">
                        <Image
                          :src="message.content"
                          :alt="t('common.imageMessage')"
                          class="max-w-xs max-h-60 rounded-lg"
                        />
                      </template>
                      <template v-else>
                        <div class="text-base leading-relaxed whitespace-pre-wrap">
                          {{ message.content }}
                        </div>
                      </template>
                    </div>
                  </div>
                  <div
                    class="text-xs mt-1 text-gray-400 opacity-70 select-none"
                    :class="[message.isSelf ? 'mr-1' : 'ml-1']"
                  >
                    {{ message.createTime }}
                  </div>
                </div>
              </div>
              <div v-if="computedMessages.length === 0" class="text-center text-gray-400 my-8">
                {{ t('common.noMessage') }}
              </div>
            </div>
            <div v-else class="flex items-center justify-center h-full text-gray-400">
              {{ t('common.selectConversation') }}
            </div>
          </div>

          <!-- 消息输入区域 -->
          <div
            class="p-4 border-t-solid border-t-2 border-[#23272F]"
            v-if="activeConversation !== null"
          >
            <div class="flex flex-col gap-1">
              <div class="flex flex-row gap-2">
                <Button @click="sendImage">
                  <Icon icon="carbon:image" />
                </Button>
              </div>
              <div class="flex flex-row gap-2">
                <TextArea
                  v-model="messageText"
                  :placeholder="t('common.inputMessage')"
                  class="flex-grow text-white rounded-lg text-base border-solid border-2 border-[#23272F]"
                  :maxlength="200"
                  style="max-height: 5rem; min-height: 2.5rem; resize: vertical"
                  @keydown.enter.exact.prevent="
                    (e: KeyboardEvent) => {
                      e.stopPropagation()
                      sendMessage(messageText, 'TEXT')
                    }
                  "
                  @keydown.shift.enter.exact.stop
                />
                <Button
                  @click="sendMessage(messageText, 'TEXT')"
                  type="primary"
                  :loading="sendLoading"
                >
                  {{ t('common.send') }}
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Card>
  </div>
  <!-- 添加好友模态框 -->
  <Modal ref="addFriendModal">
    <div class="p-4 w-full">
      <div class="flex flex-row gap-2 mb-4">
        <Input
          v-model.trim="searchUserName"
          :placeholder="t('common.inputUserNameSearch')"
          class="flex-1"
          @keyup.enter="searchUsers"
          :maxlength="20"
        />
        <Button type="primary" @click="searchUsers" :loading="searchUserLoading">
          {{ t('common.search') }}
        </Button>
      </div>

      <div class="max-h-[50vh] overflow-y-auto">
        <div v-if="searchUserList.length > 0">
          <Card
            v-for="user in searchUserList"
            :key="user.userId"
            :size="'sm'"
            class="flex items-center gap-3 px-4 py-3 cursor-pointer transition-all duration-200 rounded-lg mb-2 hover:bg-[#23272F]"
            @click="addFriend(user)"
          >
            <Image
              v-if="user.userAvatar"
              :src="user.userAvatar"
              type="avatar"
              :alt="t('common.avatar')"
              class="w-10 h-10 rounded-full object-cover border-2 border-[#23272F]"
            />
            <div v-else class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center">
              <Icon icon="duo-icons:user" class="text-size-2xl text-gray-400" />
            </div>
            <div class="flex-1 min-w-0">
              <div class="font-medium text-white truncate">{{ user.userName }}</div>
              <div class="text-xs text-gray-400 truncate" v-if="user.userBio">
                {{ user.userBio }}
              </div>
            </div>
            <Button type="primary" size="sm" class="ml-2"> {{ t('common.greet') }} </Button>
          </Card>
        </div>
        <div
          v-else-if="searchUserName && !searchUserLoading"
          class="text-center text-gray-400 py-8"
        >
          {{ t('common.noMatchUser') }}
        </div>
        <div v-else-if="!searchUserName" class="text-center text-gray-400 py-8">
          {{ t('common.inputUserNameSearch') }}
        </div>
      </div>
    </div>
  </Modal>
</template>

<script setup lang="ts">
  import { ref, onMounted, nextTick, watch, computed } from 'vue'
  import { Icon } from '@iconify/vue'
  import { Card, Input, Button, Modal, TextArea } from '@/components/common'
  import { useUserStore } from '@/stores/user'
  import { useMessageStore } from '@/stores/message'
  import { storeToRefs } from 'pinia'
  import type { MessageUser } from '@/api/modules/message/message.d'
  import {
    deleteMessageWithUser,
    getMessageUserList,
    getMessageWithUser
  } from '@/api/modules/message/message'
  import { getUserList } from '@/api/modules/user/user'
  import dayjs from 'dayjs'
  import { useFilePicker } from '@/hook/useFilePicker'
  import { uploadImage } from '@/api/modules/common/common'
  import { onBeforeRouteLeave, useRoute, useRouter } from 'vue-router'
  import { useI18n } from 'vue-i18n'

  defineOptions({
    name: 'Message'
  })

  const { t } = useI18n()
  const route = useRoute()
  const router = useRouter()
  const userStore = useUserStore()
  const { isLogin, userInfo } = storeToRefs(userStore)
  const messageStore = useMessageStore()
  const { activeMessages } = storeToRefs(messageStore)

  const conversations = ref<MessageUser[]>([])
  const activeConversation = ref<MessageUser | null>(null)
  const searchText = ref('')
  const messageText = ref('')
  const sendLoading = ref(false)
  const { pickFiles } = useFilePicker({ accept: 'image/*', multiple: false })
  const messageContainer = ref<HTMLElement | null>(null)
  const addFriendModal = ref<InstanceType<typeof Modal> | null>(null)
  const searchUserName = ref('')
  const searchUserList = ref<any[]>([])
  const searchUserLoading = ref(false)

  const refershConversations = () => {
    getMessageUserList().then((res) => {
      conversations.value = res.data.map((item) => ({
        ...item,
        unreadCount:
          activeConversation.value?.otherUserId === item.otherUserId ? 0 : item.unreadCount
      }))
    })
  }

  const pageNum = ref(1)
  const pageSize = ref(20)
  const total = ref(0)
  const loadingHistory = ref(false)

  const loadMessages = async (reset = false) => {
    if (!activeConversation.value) return
    if (loadingHistory.value) return
    loadingHistory.value = true
    try {
      const res = await getMessageWithUser({
        receiverId: activeConversation.value.otherUserId,
        pageNum: pageNum.value,
        pageSize: pageSize.value
      })
      if (res.data && Array.isArray(res.data.records)) {
        const sessionKey = [userInfo.value.userId, activeConversation.value.otherUserId]
          .sort()
          .join('_')
        const records = res.data.records.reverse().map((item) => ({
          ...item,
          sender: item.senderId,
          to: item.receiverId,
          type: 'message' as const
        }))
        if (reset) {
          messageStore.sessionMessages[sessionKey] = records
        } else {
          messageStore.sessionMessages[sessionKey] = [
            ...records,
            ...(messageStore.sessionMessages[sessionKey] || [])
          ]
        }
        total.value = res.data.total || 0
      }
    } finally {
      loadingHistory.value = false
    }
  }

  const selectConversation = async (index: number) => {
    activeConversation.value = conversations.value[index]
    if (activeConversation.value) {
      messageStore.setActiveSession(activeConversation.value.otherUserId)
      try {
        setTimeout(() => {
          conversations.value[index].unreadCount = 0
        }, 1000)
      } catch (e) {
        console.log('发送聊天激活消息失败', e)
      }
      pageNum.value = 1
      await loadMessages(true)
      scrollToBottom()
    }
  }

  const deleteConversation = () => {
    if (activeConversation.value) {
      deleteMessageWithUser(activeConversation.value.otherUserId).then((res) => {
        if (res.data && activeConversation.value?.otherUserId) {
          messageStore.deleteSessionMessages(activeConversation.value.otherUserId)
          activeConversation.value = null
          refershConversations()
        }
      })
    }
  }

  const sendMessage = (content: string, type: 'TEXT' | 'IMAGE' = 'TEXT') => {
    if (content.trim() === '' || !activeConversation.value) return
    sendLoading.value = true
    const createTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    messageStore.sendMessage({
      isRead: true,
      content: content,
      messageType: type,
      to: activeConversation.value.otherUserId,
      sender: userInfo.value.userId,
      status: 0,
      type: 'message',
      createTime: createTime,
      updateTime: createTime
    })
    setTimeout(() => {
      sendLoading.value = false
      messageText.value = ''
      scrollToBottom()
    }, 500)
  }

  const sendImage = () => {
    pickFiles().then((file) => {
      if (file) {
        uploadImage({
          file: file as File,
          uploadFileType: 'CHAT_IMAGE'
        }).then((res) => {
          if (res.data) {
            sendMessage(res.data, 'IMAGE')
          }
        })
      }
    })
  }

  const scrollToBottom = async () => {
    await nextTick()
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  }

  watch(
    () => activeMessages.value,
    () => {
      scrollToBottom()
    },
    { deep: true }
  )

  onBeforeRouteLeave(() => {
    if (messageStore.socket?.sendMessage) {
      messageStore.socket.sendMessage(JSON.stringify({ type: 'chat_inactive' }))
    }
  })

  onMounted(() => {
    if (isLogin.value) {
      refershConversations()
    }
  })

  const computedMessages = computed(() => {
    if (!activeConversation.value) return []
    return activeMessages.value.map((item) => ({
      ...item,
      isSelf: item.sender === userInfo.value.userId,
      userAvatar:
        item.sender === userInfo.value.userId
          ? userInfo.value.userAvatar
          : activeConversation.value?.otherUserAvatar
    }))
  })

  const recallMessage = (message: any) => {
    messageStore.recallMessage(message)
  }

  const openAddFriendModal = () => {
    if (addFriendModal.value) {
      addFriendModal.value.open({
        title: t('common.findUser'),
        width: '500px',
        showCancel: false,
        showConfirm: false,
        closeText: t('common.close')
      })
      searchUserName.value = ''
      searchUserList.value = []
    }
  }

  const searchUsers = async () => {
    if (!searchUserName.value.trim()) return
    searchUserLoading.value = true
    try {
      const res = await getUserList({
        userName: searchUserName.value,
        pageNum: 1,
        pageSize: 10
      })
      if (res.data && Array.isArray(res.data.records)) {
        searchUserList.value = res.data.records.filter(
          (user) =>
            user.userId !== userInfo.value.userId &&
            !conversations.value.some((conv) => conv.otherUserId === user.userId)
        )
      }
    } catch (error) {
      console.error('搜索用户失败', error)
    } finally {
      searchUserLoading.value = false
    }
  }

  const addFriend = async (user: any) => {
    if (!user || !user.userId) return
    const createTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
    messageStore.sendMessage({
      isRead: true,
      content: t('common.greetMessage'),
      messageType: 'TEXT',
      to: user.userId,
      sender: userInfo.value.userId,
      status: 0,
      type: 'message',
      createTime: createTime,
      updateTime: createTime
    })
    if (addFriendModal.value) {
      addFriendModal.value.close()
    }
    await refershConversations()
    const newConversationIndex = conversations.value.findIndex(
      (conv) => conv.otherUserId === user.userId
    )
    if (newConversationIndex !== -1) {
      selectConversation(newConversationIndex)
    }
  }

  const toUserDetail = (userId: number) => {
    if (!userId) return
    router.push({
      path: `/user/${userId}`
    })
  }

  onMounted(() => {
    if (route.params.id as string) {
      addFriend(Number(route.params.id))
    }
  })

  // 监听消息池变化，自动刷新会话列表（保证新会话及时出现）
  watch(
    () => messageStore.sessionMessages,
    () => {
      refershConversations()
    },
    { deep: true }
  )
</script>
