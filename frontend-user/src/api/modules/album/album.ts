import type { PageRequest, Response, PageResponse } from '@/api/api.d'
import api from '../../index'
import type { Album, AlbumType } from './album.d'

/** 获取专辑类型 */
export const getAlbumType = (): Promise<Response<AlbumType[]>> => api.get('/api/user/album/type')

/** 获取专辑列表 */
export const getAlbumList = (
  params: PageRequest & { typeId?: number | string }
): Promise<PageResponse<Omit<Album, 'songs'>>> => api.get('/api/user/album/list', { params })

/** 获取专辑详情 */
export const getAlbumDetail = (albumId: number | string): Promise<Response<Album>> =>
  api.get(`/api/user/album/${albumId}/detail`)
