import { defineStore, storeToRefs } from 'pinia'
import useWebSocket from 'vue-hooks-plus/es/useWebSocket'
import { computed, ref, watch, watchEffect } from 'vue'
import { useUserStore } from './user'
import { toast } from '@/utile'
import type { MessageType, WebSocketMsgRecord } from '@/api/modules/message/message.d'
import i18n from '@/plugin/i18n-instance'
import dayjs from 'dayjs'

interface MessageRecord {
  type: 'message' | 'recall' | 'recalling'
  to?: string | number
  sender?: string | number
  content: string
  messageType: (typeof MessageType)[keyof typeof MessageType]
  messageId?: string | number
  createTime?: string
  updateTime?: string
  isRead?: boolean
  status?: number
}

/**
 * 生成会话 key
 * @param userId1
 * @param userId2
 * @returns
 */
function getSessionKey(userId1: string | number, userId2: string | number) {
  // 保证 key 唯一且顺序无关
  return [userId1, userId2].sort().join('_')
}

/**
 * 生成16位唯一数字
 * @returns
 */
function getMessageId() {
  return new Date().getTime().toString() + Math.random().toString().substring(2, 5)
}

export const useMessageStore = defineStore(
  'message',
  () => {
    const userStore = useUserStore()
    const { isLogin } = storeToRefs(userStore)
    const HEARTBEAT_MSG = JSON.stringify({ type: 'heartbeat' })
    const HEARTBEAT_INTERVAL = 15000
    const MAX_RECONNECT = 1
    const reconnectCount = ref(0)
    const reconnecting = ref(false)
    let heartbeatTimer: ReturnType<typeof setInterval> | null = null
    // 消息映射：key 为会话 sessionKey，value 为消息数组
    const sessionMessages = ref<Record<string, MessageRecord[]>>({})
    // 当前激活会话 key
    const activeSessionKey = ref<string | null>(null)
    // WebSocket 连接
    const socket = ref<any>({
      latestMessage: ref(),
      sendMessage: null,
      connect: null,
      disconnect: null,
      readyState: ref(0),
      webSocketIns: null
    })

    // 初始化WebSocket连接
    const initWebSocket = () => {
      if (!isLogin.value || !userStore.token) {
        console.log('用户未登录，不初始化WebSocket连接')
        return
      }

      socket.value = useWebSocket(
        `${import.meta.env.VITE_APP_SOCKET_URL}?token=${userStore.token}`,
        {
          reconnectLimit: MAX_RECONNECT,
          onOpen: () => {
            reconnectCount.value = 0
            reconnecting.value = false
            startHeartbeat()
            console.log('WebSocket连接成功')
          },
          onMessage: (event: MessageEvent) => {
            try {
              // 预处理消息数据，处理可能存在的换行符问题
              const rawData = event.data.trim()
              let data: WebSocketMsgRecord

              try {
                data = JSON.parse(rawData) as WebSocketMsgRecord
              } catch (parseError) {
                console.warn('JSON解析失败，尝试修复消息格式:', rawData)
                // 尝试修复JSON格式问题
                const fixedData = rawData.replace(/\n/g, '\\n')
                data = JSON.parse(fixedData) as WebSocketMsgRecord
              }

              if (data.type === 'heartbeat') return
              handleWebSocketMessage(data)
            } catch (e) {
              // 非 JSON 消息
              console.error('消息处理异常', e, '原始消息:', event.data)
            }
          },
          onError: () => {
            toast.error(i18n.global.t('common.message.webSocketError'))
            // tryReconnect()
          },
          onClose: () => {
            stopHeartbeat()
            // tryReconnect()
          }
        }
      )
    }

    watch(
      () => isLogin.value,
      (newVal) => {
        if (newVal && userStore.token) {
          initWebSocket()
        }
      },
      {
        immediate: false
      }
    )

    // 发送下线消息并断开连接
    const sendOfflineAndDisconnect = () => {
      // 检查WebSocket是否已连接
      if (
        !socket.value ||
        !socket.value.webSocketIns ||
        socket.value.webSocketIns.readyState !== WebSocket.OPEN
      ) {
        console.log('WebSocket未连接，无需发送下线消息')
        return
      }

      if (socket.value.sendMessage && userStore.userInfo.userId) {
        console.log(`发送用户下线消息，userId: ${userStore.userInfo.userId}`)
        socket.value.sendMessage(JSON.stringify({ type: 'chat_inactive' }))

        // 等待消息发送后再断开连接
        setTimeout(() => {
          if (socket.value.disconnect) {
            socket.value.disconnect()
          }
        }, 300)
      }
    }

    // 心跳
    function startHeartbeat() {
      stopHeartbeat()
      heartbeatTimer = setInterval(() => {
        if (socket.value && socket.value.sendMessage) {
          socket.value.sendMessage(HEARTBEAT_MSG)
        }
      }, HEARTBEAT_INTERVAL)
    }

    // 停止心跳
    function stopHeartbeat() {
      if (heartbeatTimer) {
        clearInterval(heartbeatTimer)
        heartbeatTimer = null
      }
    }

    // 尝试重连
    function tryReconnect() {
      if (reconnectCount.value < MAX_RECONNECT && !reconnecting.value) {
        reconnecting.value = true
        reconnectCount.value++
        setTimeout(() => {
          if (socket.value && socket.value.connect) {
            socket.value.connect()
          }
        }, 2000)
      }
    }

    // 处理 WebSocket 消息
    const handleWebSocketMessage = (data: WebSocketMsgRecord) => {
      try {
        // 处理消息
        if (data.type === 'message') {
          // 计算会话 key
          const sessionKey = getSessionKey(data.sender!, data.to!)
          if (!sessionMessages.value[sessionKey]) {
            sessionMessages.value[sessionKey] = []
          }
          console.log(
            `接收到新消息: ${data.content?.substring(0, 20)}${data.content && data.content.length > 20 ? '...' : ''}`
          )
          sessionMessages.value[sessionKey].push({
            ...data,
            createTime: dayjs().format('YYYY-MM-DD HH:mm:ss')
          } as MessageRecord)
        }
        // 撤回消息逻辑：找到对应 messageId 的消息并标记或移除
        else if (data.type === 'recall') {
          // 计算会话 key
          const sessionKey = getSessionKey(data.sender!, data.to!)
          const messages = sessionMessages.value[sessionKey]
          console.log(`接收到撤回消息请求，messageId: ${data.messageId}`)
          if (messages) {
            const idx = messages.findIndex(
              (msg) => String(msg.messageId) === String(data.messageId)
            )
            if (idx !== -1) {
              // 这里选择将消息内容替换为"已撤回"
              console.log(`撤回消息成功，索引: ${idx}`)
              messages[idx].content = i18n.global.t('common.message.recall')
              messages[idx].type = 'recall'
            } else {
              console.warn(`未找到要撤回的消息，messageId: ${data.messageId}`)
            }
          } else {
            console.warn(`未找到会话: ${sessionKey}`)
          }
        }
        // 撤回消息失败
        else if (data.type === 'recall_error') {
          console.error(`撤回消息失败: ${data.message}`)
          toast.error(data.message || i18n.global.t('common.message.recallFailed'))
          // 计算会话 key
          const sessionKey = getSessionKey(data.sender!, data.to!)
          const messages = sessionMessages.value[sessionKey]
          if (messages) {
            const idx = messages.findIndex(
              (msg) => String(msg.messageId) === String(data.messageId)
            )
            if (idx !== -1) {
              messages[idx].type = 'message'
            } else {
              console.warn(`未找到要设置的消息，messageId: ${data.messageId}`)
            }
          } else {
            console.warn(`未找到会话: ${sessionKey}`)
          }
        } else {
          console.warn(`未知的消息类型: ${data.type}`)
        }
      } catch (error) {
        console.error('处理WebSocket消息时出错:', error)
      }
    }

    // 发送消息并存储到对应会话
    const sendMessage = (message: MessageRecord) => {
      try {
        // 检查WebSocket是否已连接
        if (
          !socket.value ||
          !socket.value.webSocketIns ||
          socket.value.webSocketIns.readyState !== WebSocket.OPEN
        ) {
          console.log('WebSocket未连接，无法发送消息')
          return
        }
        // 如果消息没有 messageId，则生成一个
        if (!message.messageId) {
          message.messageId = getMessageId()
        }
        // 如果消息没有 createTime，则生成一个
        if (socket.value.sendMessage) {
          socket.value.sendMessage(JSON.stringify(message))
          // 本地也存一份
          const sessionKey = getSessionKey(message.sender!, message.to!)
          if (!sessionMessages.value[sessionKey]) {
            sessionMessages.value[sessionKey] = []
          }
          sessionMessages.value[sessionKey].push(message)
        } else {
          toast.error(i18n.global.t('common.message.webSocketNotConnected'))
        }
      } catch (error) {
        toast.error(i18n.global.t('common.message.sendFailed'))
      }
    }

    // 获取某个会话的消息
    const getMessagesBySession = (userId: string | number) => {
      if (!userStore.userInfo.userId) return []
      const sessionKey = getSessionKey(userStore.userInfo.userId, userId)
      return sessionMessages.value[sessionKey] || []
    }

    // 设置当前激活会话
    const setActiveSession = (userId: string | number) => {
      if (!userStore.userInfo.userId) return
      activeSessionKey.value = getSessionKey(userStore.userInfo.userId, userId)
      if (activeSessionKey.value && socket.value && socket.value.sendMessage) {
        setTimeout(() => {
          socket.value.sendMessage(
            JSON.stringify({
              type: 'chat_active',
              otherUserId: userId
            })
          )
        }, 1000)
      }
    }

    // 获取当前激活会话的消息
    const activeMessages = computed(() => {
      if (!activeSessionKey.value) return []
      return sessionMessages.value[activeSessionKey.value] || []
    })

    // 删除某个会话的所有消息
    const deleteSessionMessages = (userId: string | number) => {
      if (!userStore.userInfo.userId) return
      const sessionKey = getSessionKey(userStore.userInfo.userId, userId)
      delete sessionMessages.value[sessionKey]
    }

    // 撤回消息
    const recallMessage = (message: MessageRecord) => {
      if (!message || !activeSessionKey.value) return

      // 检查WebSocket是否已连接
      if (
        !socket.value ||
        !socket.value.webSocketIns ||
        socket.value.webSocketIns.readyState !== WebSocket.OPEN
      ) {
        console.log('WebSocket未连接，尝试重新连接')
        return
      }

      if (socket.value.sendMessage) {
        socket.value.sendMessage(
          JSON.stringify({
            type: 'recall',
            to: message.to,
            sender: message.sender,
            messageId: message.messageId
          })
        )
        // 不直接修改消息状态，等待服务器返回撤回结果
        // 添加一个临时的撤回中状态
        const sessionKey = getSessionKey(message.sender!, message.to!)
        const messages = sessionMessages.value[sessionKey]
        if (messages) {
          const idx = messages.findIndex(
            (msg) => String(msg.messageId) === String(message.messageId)
          )
          if (idx !== -1) {
            messages[idx].type = 'recalling'
            toast.info(i18n.global.t('common.message.recalling'), {
              timeout: 2000
            })
          }
        }
      } else {
        toast.error(i18n.global.t('common.message.webSocketNotConnected'))
      }
    }

    return {
      sessionMessages,
      sendMessage,
      getMessagesBySession,
      setActiveSession,
      activeMessages,
      deleteSessionMessages,
      socket,
      reconnecting,
      recallMessage,
      initWebSocket,
      sendOfflineAndDisconnect
    }
  },
  {
    persist: {
      key: 'message',
      storage: localStorage,
      // 需要持久化的数据
      pick: ['sessionMessages', 'activeMessages']
    }
  }
)
