import type { PageRequest, PageResponse } from '@/api/api'

export type SearchResponse = {
  albums: {
    albumCover?: string
    albumId?: number
    albumName?: string
    singerName?: string
  }[]
  singers: {
    singerAvatar: string
    singerId: number
    singerName: string
  }[]
  songs: {
    singerName: string
    songCover: string
    songId: number
    songName: string
    songFilePath: string
  }[]
}

export type QueueType = 'ALBUM' | 'CUSTOM' | 'FAVORITE' | 'PLAYLIST'

export type PlayMode = 'SINGLE' | 'SEQUENCE' | 'SHUFFLE'

export enum PlayModeEnum {
  SINGLE = 'SINGLE',
  SEQUENCE = 'SEQUENCE',
  SHUFFLE = 'SHUFFLE'
}

/** 评论类型 */
export const TargetType = {
  /** 专辑 */
  Album: 'ALBUM',
  /** 歌单 */
  Playlist: 'PLAYLIST',
  /** 歌曲 */
  Song: 'SONG',
  /** MV */
  Mv: 'MV'
} as const

export type CommentRequest = PageRequest & {
  /** 目标id */
  targetId?: string
  /** 类型 */
  targetType?: (typeof TargetType)[keyof typeof TargetType]
}

export type CommentItem = {
  children?: CommentItem[]
  commentId?: number
  content?: string
  createTime?: string
  likeCount?: number
  userAvatar?: string
  userId?: number
  userName?: string
  isLike?: boolean
}

export type CommentResponse = PageResponse<CommentItem>
