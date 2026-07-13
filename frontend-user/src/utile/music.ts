// 解析lrc歌词文件
export function parseLyrics(lyrContent: string) {
  try {
    const lines = lyrContent.split('\n')
    const timeRegex = /\[(\d{2}):(\d{2})(?:\.(\d{2,3})?)?\]/
    const parsedLyrics: { time: number; text: string }[] = []

    lines.forEach((line) => {
      line = String(line)
      // 去除回车符和首尾空白
      line = line.replace('\r', '').trim()

      // 跳过空行或只包含时间标记的行
      if (!line || line.endsWith(']')) return

      // 跳过不包含实际歌词的标记行（如LRC-toomic.com）
      if (line.includes('LRC-') || line.includes('.com')) return

      const match = line.match(timeRegex)
      if (match) {
        const minutes = parseInt(match[1])
        const seconds = parseInt(match[2])
        const milliseconds = match[3] ? parseInt(match[3].padEnd(3, '0')) : 0
        const time = minutes * 60 + seconds + milliseconds / 1000
        const text = line.replace(timeRegex, '').trim()
        if (text) {
          parsedLyrics.push({ time, text })
        }
      }
    })

    return parsedLyrics.sort((a, b) => a.time - b.time)
  } catch (error) {
    return []
  }
}

export function downloadSong(Songurl: string, filename: string) {
  return new Promise<boolean>(async (resolve, reject) => {
    try {
      const response = await fetch(Songurl, { mode: 'cors' })
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = filename
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      window.URL.revokeObjectURL(url)
      resolve(true)
    } catch (e) {
      reject(e)
    }
  })
}
