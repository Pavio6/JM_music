import type { LoginForm } from '@/types/auth';
import type { SingerVo } from '@/types/singer';
import type { CaptchaVo, Result, StatisticsVO } from '../types/api';
import request from '../utils/request';

export function getCaptcha() {
  return request.get<any, Result<CaptchaVo>>('/common/captcha/get');
}

export function login(data: LoginForm) {
  return request<Result<string>>({
    url: '/admin/auth/login',
    method: 'post',
    data
  });
}

export function logout() {
  return request.post<any, Result<null>>('/admin/logout');
}

export function getStatistics() {
  return request.get<any, Result<StatisticsVO>>('/admin/overview/statistics');
}

// 上传文件
export function uploadFile(file: File, fileType: string) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('fileType', fileType);
  
  return request<Result<string>>({
    url: '/common/file/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}

// 添加歌手
export function addSinger(data: Omit<SingerVo, 'singerId'>) {
  return request<Result<void>>({
    url: '/admin/singer/addOrUpdate',
    method: 'post',
    data
  });
}

// 删除文件
export function deleteFile(fileType: string, filePath: string) {
  return request<Result<boolean>>({
    url: '/common/file/delete',
    method: 'post',
    params: {
      fileType,
      filePath
    }
  });
}