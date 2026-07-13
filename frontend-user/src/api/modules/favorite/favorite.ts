import type { PageRequest, Response, PageResponse } from '@/api/api.d'
import api from '../../index'
import { FavoriteType } from './favorite.d'
import type { PlayList, Song } from '../music/music.d'

/**
 * 获取个人歌曲收藏列表
 * @param params
 * @returns
 */
export const getFavoriteMineList = (params: PageRequest): Promise<PageResponse<Song>> => {
  return api.get('/api/user/favorite/mine', { params })
}

/**
 * 获取用户的收藏歌曲列表
 * @param userId
 * @param params
 * @returns
 */
export const getFavoriteListByUserId = (
  userId: string | number,
  params: PageRequest
): Promise<PageResponse<Song>> => {
  return api.get(`/api/user/favorite/${userId}/list`, { params })
}

/**
 * 收藏歌曲或歌单或专辑
 * @param params
 * @returns
 */
export const addFavorite = (params: {
  /** 是否收藏 */
  isFavorite: boolean
  /** 目标id */
  targetId: number | string
  /** 收藏类型 */
  favoriteType: (typeof FavoriteType)[keyof typeof FavoriteType]
}): Promise<Response<boolean>> => {
  return api.post(
    `/api/user/favorite/${params.targetId}/${params.isFavorite}`,
    {},
    {
      params: {
        favoriteType: params.favoriteType
      }
    }
  )
}
