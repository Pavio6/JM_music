export interface Response<T> {
  code: number
  data: T
  message: string
  state: boolean
}

export type Response = {
  code: number
  data: Data
  message: string
  state: boolean
}

export type PageRequest = {
  /** 当前页码 */
  pageNum?: number
  /** 每页条数 */
  pageSize?: number
}

export type PageResponse<T> = Response<{
  current: number
  pages: number
  records: T[]
  size: number
  total: number
}>

export const TargetType = {
  0: '歌曲',
  1: '歌单',
  2: '专辑',
  3: '文章',
  4: '外部链接'
} as const

export type Carousel = {
  carouselId: number
  createBy: number
  createTime: string
  /** 结束时间 */
  endTime: string
  /** 外部链接URL */
  externalUrl: null | string
  imageUrl: string
  /** 排序顺序(数字越小越靠前) */
  sortOrder: number
  /** 开始时间 */
  startTime: string
  status: number
  subTitle: string
  targetId: number | null
  targetType: keyof typeof TargetType
  title: string
  updateTime: string
}
