import type {
  CommentItem,
  CommentRequest,
  CommentResponse,
  PlayMode,
  QueueType,
  SearchResponse,
  TargetType
} from './common.d'
import api from '@/api/'
import type { PageResponse, Response } from '@/api/api'

/** 搜索接口 */
export const search = (params: {
  /** 搜索关键词 */
  keyword: string
  /** 每页条数 */
  limit?: number
}): Promise<Response<SearchResponse>> => {
  return api.get('/api/user/search', {
    params
  })
}

/** 记录播放时长 */
export const recordListening = (params: {
  /** 歌曲ID */
  songId: number | string
  /** 播放时长 */
  playDuration: number | string
}): Promise<Response<boolean>> => {
  return api.post('/api/user/listening/record', {}, { params })
}

/**
 * 获取当前播放队列状态
 * 获取用户歌单
 */
export const getPlayQueue = (): Promise<
  Response<{
    /** 当前播放队列播放的位置索引 */
    currentIndex: number
    /** 索引位置对应的歌曲id */
    currentSongId: number
    playMode: string
    songsInfoList: {
      singerName: string
      songCover: string
      songId: number
      songName: string
    }[]
  }>
> => {
  return api.get('/api/user/play/queue/current')
}

/**
 * 获取评论列表
 * @param params
 * @returns
 */
export const getCommentList = (params: CommentRequest): Promise<CommentResponse> => {
  return api.get('/api/user/comment/', { params })
}

/**
 * 添加评论
 * @param params
 * @returns
 */
export const addComment = (
  params: CommentRequest & {
    /** 评论内容 */
    content: string
    /** 父评论id */
    parentCommentId?: string
  }
): Promise<Response<boolean>> => {
  return api.post('/api/user/comment/', params)
}

/** 点赞/取消点赞评论 */
export const likeComment = (commentId: number, isLike: boolean): Promise<Response<boolean>> => {
  return api.put(`/api/user/comment/like/${commentId}/${isLike}`)
}

/** 上传图片 */
export const uploadImage = (params: {
  /** 图片文件 */
  file: File
  /** 目标类型 */
  uploadFileType: 'CHAT_IMAGE'
}): Promise<Response<string>> => {
  const formData = new FormData()
  formData.append('file', params.file)
  formData.append('uploadFileType', params.uploadFileType)
  return api.post('/common/file/upload/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
