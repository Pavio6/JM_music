import api from '../../index'
import type { PageRequest, PageResponse, Response } from '@/api/api.d'
import type { MvDetail } from './musicVideo.d'

/** 获取MV详细信息 */
export const getMvDetail = (mvId: number | string): Promise<Response<MvDetail>> => {
  return api.get(`/api/user/mv/detail/${mvId}`)
}
