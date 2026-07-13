import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import api from '../../index'
import type { PlayList } from '../music/music.d'

/**
 * 从歌单中移除歌曲
 * @param params
 * @returns
 */
export const removeSongFromPlayList = (params: {
  playlistId: number | string
  songIds?: (number | string)[]
}): Promise<Response<boolean>> => {
  const urlParams = new URLSearchParams()
  Array.isArray(params.songIds) &&
    params.songIds.forEach((id) => {
      urlParams.append('songIds', id.toString())
    })
  return api.delete(`/api/user/playlist/${params.playlistId}/removeSongs?${urlParams.toString()}`)
}

/**
 * 编辑歌单属性
 * @param playlistId
 * @param params
 * @returns
 */
export const editPlayList = (
  playlistId: number | string,
  params: {
    /** 歌单封面图 */
    playlistCover: File
    /** 歌单名称  <= 20 字符 */
    playlistName: string
    /** 歌单详情 <= 500 字符 */
    playlistBio: string
    /** 歌单标签 <= 5 items */
    tagIds: (number | string)[]
  }
): Promise<Response<boolean>> => {
  const formData = new FormData()
  params.playlistCover && formData.append('playlistCover', params.playlistCover)
  params.playlistName && formData.append('playlistName', params.playlistName)
  params.playlistBio && formData.append('playlistBio', params.playlistBio)
  params.tagIds && params.tagIds.length && formData.append('tagIds', params.tagIds.join(','))
  return api.put(`/api/user/playlist/${playlistId}/edit`, formData)
}

/**
 * 添加歌曲到歌单中
 * @param playlistId
 * @param songIds
 * @returns
 */
export const addSongToPlayList = (params: {
  playlistId: number | string
  songIds?: (number | string)[]
}): Promise<Response<boolean>> => {
  const urlParams = new URLSearchParams()
  Array.isArray(params.songIds) &&
    params.songIds.forEach((id) => {
      urlParams.append('songIds', id.toString())
    })
  return api.put(`/api/user/playlist/${params.playlistId}/addSongs?${urlParams.toString()}`)
}

/**
 * 创建歌单
 * @param params
 * @returns
 */
export const createPlayList = (params: {
  /** 歌单名称，长度不能超过20个字符 */
  playlistName: string
}): Promise<Response<boolean>> => api.post(`/api/user/playlist/create`, {}, { params })

/** 分页获取用户创建的歌单列表 */
export const getUserCreatePlayList = (
  userId: number,
  params: PageRequest
): Promise<PageResponse<PlayList>> => api.get(`/api/user/playlist/${userId}/create`, { params })

/** 获取用户个人创建的歌单列表 */
export const getMinePlayList = (params: PageRequest): Promise<PageResponse<PlayList>> =>
  api.get(`/api/user/playlist/mine`, { params })

/**删除歌单 */
export const deletePlayList = (playlistId: number | string): Promise<Response<boolean>> =>
  api.delete(`/api/user/playlist/delete/${playlistId}`)
