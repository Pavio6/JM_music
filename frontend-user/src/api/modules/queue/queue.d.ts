export type CurrentQueue = {
  /** 当前播放队列播放的位置索引 */
  currentIndex: number
  /** 索引位置对应的歌曲id */
  currentSongId: number
  songsInfoList: SongsInfoList[]
}

export type SongsInfoList = {
  singerName: string
  songCover: string
  songId: number
  songName: string
}
