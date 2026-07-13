import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import api from '../../index'
import type { CurrentQueue } from './queue.d'
import type { PlayMode } from '../common/common.d'

/** 切换播放队列类型 */
export const switchQueue = (params: { songIds: (string | number)[] }) => {
  const urlParams = new URLSearchParams()
  params.songIds.forEach((id) => {
    urlParams.append('songIds', id.toString())
  })
  return api.post(`/api/user/play/queue/switch?${urlParams.toString()}`, {})
}

/** 获取当前播放队列状态 */
export const getCurrentQueue = (): Promise<Response<CurrentQueue>> =>
  api.get('/api/user/play/queue/current')

/** 切换播放模式 */
export const changePlayMode = (playMode: PlayMode): Promise<Response<boolean>> =>
  api.put(
    '/api/user/play/queue/change/play-mode',
    {},
    {
      params: { playMode }
    }
  )

/** 添加歌曲到队列 */
export const addSongToQueue = (songId: number | string): Promise<Response<boolean>> =>
  api.post('/api/user/play/queue/add/' + songId)

/** 从播放队列中批量移除歌曲 */
export const batchDeleteSongFromQueue = (
  songIds: (string | number)[]
): Promise<Response<boolean>> => {
  const urlParams = new URLSearchParams()
  songIds.forEach((id) => {
    urlParams.append('songIds', id.toString())
  })
  return api.delete(`/api/user/play/queue/batchDelete?${urlParams.toString()}`, {})
}

/** 清空当前用户队列信息 */
export const clearAllQueue = (): Promise<Response<boolean>> =>
  api.delete('/api/user/play/queue/clearAll')

/** 修改用户正在播放的队列的歌曲 */
export const updateSongInQueue = (songId: number | string): Promise<Response<boolean>> =>
  api.put('/api/user/play/queue/' + songId + '/update')
