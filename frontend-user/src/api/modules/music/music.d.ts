import type { Response } from '../../api.d'

export type GetMusicTagsRequest = {}
export type Tag = {
  tagId: number
  tagName: string
  [property: string]: any
}
export type Tags = {
  tagsBasicInfoList: Tag[]
  tagType: string
  [property: string]: any
}
export type GetMusicTagsResponse = Response<Tags[]>

export const TagType = {
  Genre: 'GENRE',
  Language: 'LANGUAGE',
  Mood: 'MOOD',
  Scene: 'SCENE',
  Theme: 'THEME'
} as const

// getMusicList
export interface GetMusicListRequest {
  /**
   * 当前页码
   */
  pageNum?: number
  /**
   * 每页条数
   */
  pageSize?: number
  /**
   * 歌单标签id
   */
  tagId?: number
  [property: string]: any
}

export type PlayList = {
  playCount: number
  playlistCover: string
  playlistId: string
  playlistName: string
  [property: string]: any
}

export type GetMusicListResponse = Response<{
  current: number
  pages: number
  records: PlayList[]
  size: number
  total: number
  [property: string]: any
}>

export type GetPlayListDetailRequest = {
  playlistId: string
  [property: string]: any
}

export type Song = {
  albumId?: number
  albumName?: string
  singerId?: number
  singerName?: string
  songCover?: string
  songDuration?: string
  songFilePath?: string
  songId: number
  songName?: string
  songReleaseDate?: string
  mvId?: number | null
  isFavorite?: boolean
  [property: string]: any
}

export type PlayListDetail = {
  isCollected: boolean
  playCount: number
  playlistBio: string
  playlistCover: string
  playlistId: number
  playlistName: string
  songs: Song[]
  tagName: string[]
  userAvatar: string
  userId: number
  userName: string
  [property: string]: any
}

export type GetPlayListDetailResponse = Response<PlayListDetail>

export type GetSongAudioAndLyricsResponse = Response<{
  songFilePath: string
  songLyrics: string
}>
