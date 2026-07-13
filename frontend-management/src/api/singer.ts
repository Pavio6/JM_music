import type { Result } from "@/types/api.ts";
import type { SearchForm, SingerVo } from '@/types/singer';
import request from '@/utils/request';

export interface PageResult<T> {
  records: T[];
  total: number;
  size: number;
  current: number;
  pages: number;
}

// 添加或更新歌手
export function addSinger(data: Partial<SingerVo>) {
  return request<Result<void>>({
    url: '/admin/singer',
    method: data.singerId ? 'put' : 'post',
    data
  });
}

export function getSingerList(params: SearchForm) {
  return request<Result<PageResult<SingerVo>>>({
    url: '/admin/singer/page',
    method: 'get',
    params
  });
}

// 获取歌手详情
export function getSingerDetail(id: string) {
  return request<Result<SingerVo>>({
    url: `/admin/singer/${id}`,
    method: 'get'
  });
} 