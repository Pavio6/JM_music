import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import api from '../../index'
import type { MessageRecord, MessageUser } from './message.d'

/** 删除与指定用户的所有消息 */
export const deleteMessageWithUser = (receiverId: string | number): Promise<Response<boolean>> => {
  return api.delete(`/api/user/private-messages/${receiverId}`)
}

/** 获取与指定用户的会话消息 */
export const getMessageWithUser = (
  params: {
    /** 接收者id */
    receiverId: number | string
  } & PageRequest
): Promise<PageResponse<MessageRecord>> => {
  return api.get('/api/user/private-messages/history', { params })
}

/** 获取有私聊消息往来的用户列表 */
export const getMessageUserList = (): Promise<Response<MessageUser[]>> => {
  return api.get('/api/user/private-messages/users')
}
