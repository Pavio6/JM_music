import api from '../../index'
import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import type { FollowType, Region, Signer } from './singer.d'

/** 获取歌手详情 */
export const getSingerDetail = (id: number): Promise<Response<Signer>> => {
  return api.get(`/api/user/singer/${id}`)
}

/** 获取歌手的粉丝数 */
export const getSingerFans = (
  singerId: number
): Promise<
  Response<{
    singerFollowerCount: number
  }>
> => {
  return api.get(`/api/user/follow/singer/stats?singerId=${singerId}`)
}

/**
 * 关注/取关歌手/用户
 * 用户关注或取关指定歌手或用户
 * @param followedId 歌手/用户id
 * @param isFollow 是否关注
 * @param followType - 关注类型
 * @returns
 */
export const followSingerOrUser = (params: {
  followedId: number | string
  isFollow: boolean
  followType: (typeof FollowType)[keyof typeof FollowType]
}): Promise<Response<boolean>> => {
  return api.post(
    `/api/user/follow/${params.followedId}/${params.isFollow}?followType=${params.followType}`
  )
}

/** 获取歌手地域列表 */
export const getSingerRegionList = (): Promise<Response<Region>> => {
  return api.get('/api/user/region/list')
}

/** 获取歌手列表 */
export const getSingerList = (
  params: PageRequest & {
    /** 区域id */
    regionID?: number
  }
): Promise<
  PageResponse<{
    singerAvatar?: null
    singerId?: number
    singerName?: string
  }>
> => {
  return api.get('/api/user/singer/page', { params })
}
