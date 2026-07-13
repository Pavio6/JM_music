import type { Album } from '@/api/modules/album/album.d'
import type { Song } from '@/api/modules/music/music.d'

export type Signer = {
  albums: Album[]
  isFollowed: boolean
  /** 粉丝数量 */
  followerCount: number
  regionName: string
  singerAvatar: string
  singerBio: string
  singerId: number
  singerName: string
  songs: (Song & { isLiked: boolean })[]
  [property: string]: any
}

export type Social = {
  /**
   * 粉丝数量
   */
  followers?: number
  /**
   * 点赞数
   */
  likes?: number
}

/**
 * 关注者类型 user/singer
 */
export const FollowType = {
  Singer: 'SINGER',
  User: 'USER'
} as const

export type Region = {
  regionId: number
  regionName: string
}
