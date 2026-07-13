import type {
  UserRegisterRequest,
  UserLoginRequest,
  UserLoginResponse,
  UserRegisterResponse,
  UserInfo,
  UpdatePrivacyRequest,
  UpdateUserInfoRequest,
  UserPageItem,
  FollowListQueryType,
  UserInfoMine,
  FollowStats
} from './user.d'
import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import api from '../../index'
import { PlayListType } from '../playList/playList.d'
import { omit } from 'lodash-es'
import type { Song } from '../music/music.d'

/** 登录 */
export const login = (data: UserLoginRequest): Promise<UserLoginResponse> =>
  api.post('/api/user/login', data, {})

/** 用户注册 */
export const register = (data: UserRegisterRequest): Promise<UserRegisterResponse> => {
  const formdata = new FormData()
  formdata.append('userName', data.userName)
  formdata.append('userPass', data.userPass)
  data.userAvatar && formdata.append('userAvatar', data.userAvatar)
  formdata.append('userEmail', data.userEmail)
  data.userBio && formdata.append('userBio', data.userBio)
  data.userSex && formdata.append('userSex', data.userSex + '')
  formdata.append('userBirth', data.userBirth)
  return api.post('/api/user/register', formdata, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/** 登出 */
export const logout = (): Promise<Response<boolean>> => api.post('/api/user/logout')

/** 获取用户信息 */
export const getUserInfo = (userId: number): Promise<Response<UserInfo>> =>
  api.get(`/api/user/${userId}/info`)

/** 更新用户个人资料 */
export const updateUserInfo = (data: UpdateUserInfoRequest): Promise<Response<boolean>> => {
  const formdata = new FormData()
  for (const key in data) {
    if (Object.prototype.hasOwnProperty.call(data, key)) {
      const element = data[key as keyof typeof data]
      // 根据元素类型分别处理
      if (element !== undefined) {
        if (typeof element === 'string' || typeof element === 'number') {
          formdata.append(key, element.toString())
        } else if (element instanceof File) {
          formdata.append(key, element)
        }
      }
    }
  }
  return api.put('/api/user/edit', formdata, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/** 获取用户个人信息 */
export const getMineInfo = (): Promise<Response<UserInfoMine>> => api.get('/api/user/mine')

/** 获取用户的关注和粉丝数 */
export const getFollowStats = (userId: number): Promise<Response<FollowStats>> =>
  api.get('/api/user/follow/stats', { params: { userId } })

/** 获取用户当天的听歌时长 */
export const getListeningDailyDuration = (): Promise<Response<number>> =>
  api.get('/api/user/listening/daily-duration')

/** 更新隐私设置 */
export const updatePrivacy = (data: UpdatePrivacyRequest): Promise<Response<boolean>> =>
  api.put('/api/user/privacy/update/', data)

/** 获取用户歌单收藏列表 */
export const getUserPlaylistCollect = (
  params: {
    userId: number
    type?: (typeof PlayListType)[keyof typeof PlayListType]
  } & PageRequest
): Promise<
  PageResponse<{
    id: number | string
    cover: string
    name: string
  }>
> => {
  return api.get(`/api/user/playlist/${params.userId}/collect`, {
    params: {
      type: params.type,
      pageNum: params.pageNum,
      pageSize: params.pageSize
    }
  })
}

/** 获取用户列表 */
export const getUserList = (
  params: {
    userName?: string
  } & PageRequest
): Promise<PageResponse<UserPageItem>> => {
  return api.get('/api/user/list', {
    params
  })
}

/** 获取用户个人的关注列表 */
export const getMineFollowList = (
  params: {
    type: (typeof FollowListQueryType)[keyof typeof FollowListQueryType]
  } & PageRequest
): Promise<PageResponse<{ cover: string; id: number; name: string }>> =>
  api.get('/api/user/mine/follow/list', { params })

/** 获取用户个人的粉丝列表 */
export const getMineFanList = (
  params: PageRequest
): Promise<PageResponse<{ cover: string; id: number; name: string }>> =>
  api.get('/api/user/mine/fan/list', { params })

/** 获取用户的关注列表 */
export const getUserFollowList = (
  params: {
    userId: number
    type: (typeof FollowListQueryType)[keyof typeof FollowListQueryType]
  } & PageRequest
): Promise<PageResponse<{ cover: string; id: number; name: string }>> =>
  api.get(`/api/user/follow/list/${params.userId}`, { params: omit(params, ['userId']) })

/** 获取用户的粉丝列表 */
export const getUserFanList = (
  params: {
    userId: number
    type: (typeof FollowListQueryType)[keyof typeof FollowListQueryType]
  } & PageRequest
): Promise<PageResponse<{ cover: string; id: number; name: string }>> =>
  api.get(`/api/user/follow/fan/list/${params.userId}`, { params: omit(params, ['userId']) })

/** 获取歌曲列表 */
export const getSongList = (params: { songName: string }): Promise<Response<Song[]>> =>
  api.get('/api/user/song/list', { params })
