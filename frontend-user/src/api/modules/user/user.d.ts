import type { PageResponse, Response } from '@/api/api.d'
import type { PlayList, Song } from '../music/music.d'

export interface UserLoginRequest {
  /**
   * 验证码code
   */
  captchaCode: string
  /**
   * 验证码key
   */
  captchaKey: string
  /**
   * 用户名
   */
  userName: string
  /**
   * 用户密码
   */
  userPass: string
  [property: string]: any
}

export type UserLoginResponse = Response<{
  token: string
  user: {
    userAvatar: string
    userId: number
    userName: string
    [property: string]: any
  }
  [property: string]: any
}>

export interface UserRegisterRequest {
  // 用户名
  userName: string
  // 密码
  userPass: string
  // 用户头像
  userAvatar: File | string
  // 邮箱
  userEmail: string
  // 简介
  userBio: string
  // 性别 0：女 1：男 2：未知
  userSex: number
  // 生日 必须为过去日期
  userBirth: string
}

export type UserRegisterResponse = Response<{
  /**
   * 用户头像url
   */
  userAvatar: string
  userId: number
  userName: string
  [property: string]: any
}>

export type UserPrivacy = {
  followersVisibility: Visibility
  followingVisibility: Visibility
  messagePermission: MessagePermission
  playlistVisibility: Visibility
  profileVisibility: Visibility
  [property: string]: any
}

export enum Visibility {
  Private = 'PRIVATE',
  Public = 'PUBLIC'
}

export enum MessagePermission {
  All = 'ALL',
  Followers = 'FOLLOWERS'
}

export type UserInfo = {
  favoritePlaylistsList: PlayList[]
  favoriteSongsList: Song[]
  followStatsVo: FollowStats
  followersVisibility: 'PRIVATE' | 'PUBLIC'
  followingVisibility: 'PRIVATE' | 'PUBLIC'
  /**
   * 登录用户是否已关注了查询的用户
   */
  isFollowed: boolean
  /**
   * 待查询用户的私信权限
   */
  messagePermission: 'ALL' | 'FOLLOWERS'
  /**
   * 待查询用户的歌单可见性
   */
  playlistVisibility: 'PRIVATE' | 'PUBLIC'
  userAvatar: string
  userBio: string
  userId: number
  userName: string
  [property: string]: any
}

export interface UserInfoMine {
  userAvatar: string
  userBio: string
  userBirth: string
  userEmail: string
  userId: number
  userName: string
  userPrivacy: UserPrivacy
  userSex: number
}

export type UpdateUserInfoRequest = {
  userName?: string
  userEmail?: string
  userBio?: string
  userAvatar?: File | string
  userBirth?: string
  // 性别 0：女 1：男 2：未知
  userSex?: number
}

export type FollowStats = {
  /**
   * 用户关注的数量
   */
  followCount: number
  /**
   * 用户的粉丝数量
   */
  followerCount: number
}

export type UpdatePrivacyRequest = {
  /**
   * 关注列表公开状态
   */
  followersVisibility?: Visibility
  /**
   * 粉丝列表权限
   */
  followingVisibility?: string
  /**
   * 私信权限（所有人或仅关注的人）
   */
  messagePermission?: MessagePermission
  /**
   * 歌单公开状态
   */
  playlistVisibility?: Visibility
  /**
   * 用户资料公开状态
   */
  profileVisibility?: Visibility
}

export type UserPageItem = {
  createTime: string
  deleteFlag: number
  lastLoginTime: null | string
  type: number
  updateTime: string
  userAvatar: null | string
  userBio: null | string
  userBirth: string
  userEmail: string
  userId: number
  userName: string
  userPass: null
  userSex: number
  userStatus: number
}

export const FollowListQueryType = {
  /** 用户 */
  USER: 'USER',
  /** 歌手 */
  SINGER: 'SINGER'
} as const
