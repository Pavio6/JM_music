import type {
  GetPlayListDetailRequest,
  GetPlayListDetailResponse,
  GetMusicListResponse,
  GetMusicTagsResponse,
  GetSongAudioAndLyricsResponse,
  Song,
  TagType
} from './music.d'
import type { PageRequest, Response } from '@/api/api.d'
import api from '../../index'
import type { TargetType } from '../common/common.d'

/** 获取歌单标签列表 */
export const getMusicTags = (): Promise<GetMusicTagsResponse> => api.get('/common/tags/list')

/** 获取歌单列表(支持标签查询) */
export const getPlaylistPage = (
  params: PageRequest & {
    tagId?: number
    tagType?: string
  }
): Promise<GetMusicListResponse> => api.get('/api/user/playlist/page', { params })

/** 获取歌单详情 */
export const getPlayListDetail = (
  params: GetPlayListDetailRequest
): Promise<GetPlayListDetailResponse> => api.get(`/api/user/playlist/${params.playlistId}`)

/** 收藏歌曲或歌单或专辑 */
export const favoriteMusic = (targetId: number, isFavorite: number) =>
  api.post(`/api/user/favorite/${targetId}/${isFavorite}`)

/** 歌单播放量统计 */
export const getMusicPlayCount = (id: number) => api.get(`/api/playlists/${id}/stats`)

/** 增加播放量 */
export const updatePlayCount = (
  targetId: number | string,
  targetType: Omit<(typeof TargetType)[keyof typeof TargetType], 'ALBUM'>
) => {
  return api.post(`/api/user/song/${targetId}/playCount?targetType=${targetType}`)
}

/** 获取歌曲音频歌词文件URL */
export const getSongAudioAndLyrics = (songId: number): Promise<GetSongAudioAndLyricsResponse> =>
  api.get(`/api/user/song/${songId}/audio-and-lyrics`)

/** 根据歌手 id 查询歌曲列表
 * @deprecated
 */
export const getMusicListBySingerId = (singerId: number): Promise<Response<Song[]>> => {
  return api.get(`/api/user/singer/searchBySingerId?singerId=${singerId}`)
}

/**
 * 获取歌曲详情
 * @param id 歌曲id
 * @returns
 */
export const getSongDetail = (songId: number): Promise<Response<Song>> => {
  return api.get(`/api/user/song/${songId}/detail`)
}

/**
 * 热歌榜
 * @returns
 */
export const getHotSongList = (): Promise<Response<Song>> => {
  return api.get('/api/user/song/hot')
}

/**
 * 新歌榜
 * @returns
 */
export const getNewSongList = (): Promise<Response<Song>> => {
  return api.get('/api/user/song/new')
}

/**
 * 飙升榜
 * @returns
 */
export const getRisingSongList = (): Promise<Response<Song>> => {
  return api.get('/api/user/song/rising')
}
