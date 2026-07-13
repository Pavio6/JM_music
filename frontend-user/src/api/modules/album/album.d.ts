import type { Song } from '../music/music.d'

export type AlbumType = {
  createTime: number
  deleteFlag: number
  typeId: number
  typeName: string
  updateTime: number
}

export type Album = {
  albumBio?: string
  albumCover?: string
  albumId?: number
  albumName?: string
  albumReleaseDate?: string
  createTime?: null
  singerId?: number
  singerName?: string
  singerAvatar?: string
  songs?: Song[]
  typeId?: number
  typeName?: string
  updateTime?: null
  isFavorite?: boolean
  [property: string]: any
}
