export interface SingerVo {
  singerId: number;
  singerName: string;
  singerNat: string;
  singerSex: number;
  singerBirth: string;
  singerDebutDate: string;
  singerAvatar: string;
}

export interface SearchForm {
  singerName: string;
  pageNum: number;
  pageSize: number;
} 