import type { PageResponse } from '@/api/api'

export type MessageRecord = {
  content: string
  createTime: string
  isRead: boolean
  messageId: number
  messageType: (typeof MessageType)[keyof typeof MessageType]
  receiverId: number
  senderId: number
  status: (typeof MessageStatus)[keyof typeof MessageStatus]
  updateTime: string
}

export const MessageType = {
  // 图片
  Image: 'IMAGE',
  // 文本
  Text: 'TEXT'
} as const

export const MessageStatus = {
  // 已发送
  0: 0,
  // 已撤回
  3: 3
}

export type MessageUser = {
  // 最后消息内容
  lastMessageContent: string
  // 最后消息时间
  lastMessageTime: string
  // 其他用户头像
  otherUserAvatar: string
  // 其他用户ID
  otherUserId: number
  // 其他用户名
  otherUserName: string
  // 未读数量
  unreadCount: number
  // 消息类型
  messageType: (typeof MessageType)[keyof typeof MessageType]
  // 在线状态
  online: boolean
}

export type WebSocketMsgRecord = {
  type: 'message' | 'recall' | 'heartbeat' | 'status' | 'recall_error'
  to?: string | number
  sender?: string | number
  content?: string
  messageType?: (typeof MessageType)[keyof typeof MessageType]
  messageId?: string | number
  userId?: number
  online?: boolean
  message?: string
}
