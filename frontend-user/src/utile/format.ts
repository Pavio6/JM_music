/**
 * 格式化日期
 * @param date 日期字符串或时间戳
 * @returns {string} 格式化后的日期字符串 (YYYY-MM-DD)
 */
export function formatDate(date: string | number | Date): string {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 格式化时长（秒转换为分:秒）
 * @param duration 时长（秒）
 * @returns {string} 格式化后的时长字符串 (MM:SS)
 */
export function formatDuration(duration: string | number): string {
  if (!duration) return '00:00'
  duration = Number(duration)
  const minutes = Math.floor(duration / 60)
  const seconds = Math.floor(duration % 60)
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}
