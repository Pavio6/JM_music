export interface Result<T> {
  state: boolean;
  code: number;
  message: string;
  data: T;
}

export interface CaptchaVo {
  image: string;
  key: string;
}

export interface AdminLoginDTO {
  adminName: string;
  adminPass: string;
  captchaKey: string;
  captchaCode: string;
}

export interface AdminLoginVo {
  adminId: number;
  adminName: string;
  token: string;
}

export interface StatisticsVO {
    totalSongs: number;
    totalSingers: number;
    totalAlbums: number;
    totalUsers: number;
} 