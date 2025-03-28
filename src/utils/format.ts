/**
 * 日期格式化函数
 * @param date 日期字符串或Date对象
 * @param format 格式化模式，默认为 'YYYY-MM-DD'
 * @returns 格式化后的日期字符串
 */
export function formatDate(
  date: string | Date | null | undefined,
  format: string = 'YYYY-MM-DD',
): string {
  if (!date) return '-'

  const d = typeof date === 'string' ? new Date(date) : date

  // 检查日期是否有效
  if (isNaN(d.getTime())) {
    return '-'
  }

  const year = d.getFullYear()
  const month = d.getMonth() + 1
  const day = d.getDate()
  const hour = d.getHours()
  const minute = d.getMinutes()
  const second = d.getSeconds()

  // 补零函数
  const pad = (n: number): string => (n < 10 ? `0${n}` : `${n}`)

  return format
    .replace('YYYY', `${year}`)
    .replace('MM', pad(month))
    .replace('DD', pad(day))
    .replace('HH', pad(hour))
    .replace('mm', pad(minute))
    .replace('ss', pad(second))
}

/**
 * 时间段格式化函数
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @returns 格式化后的时间段字符串
 */
export function formatTimeRange(
  startTime: string | Date | null | undefined,
  endTime: string | Date | null | undefined,
): string {
  if (!startTime || !endTime) return '-'
  return `${formatDate(startTime)} 至 ${formatDate(endTime)}`
}

/**
 * 格式化数字（如服务时长）
 * @param value 数字或字符串
 * @param defaultValue 默认值，当输入为空或无效时返回
 * @returns 格式化后的数字字符串
 */
export function formatNumber(
  value: number | string | null | undefined,
  defaultValue: string = '-',
): string {
  if (value === null || value === undefined || value === '') {
    return defaultValue
  }

  const num = Number(value)
  if (isNaN(num)) {
    return defaultValue
  }

  return num.toString()
}
