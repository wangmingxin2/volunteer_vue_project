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
 * 格式化数字
 * @param value 需要格式化的数字
 * @param defaultValue 默认值，当输入为空或无效时返回
 * @param useLocale 是否使用本地化格式（千分位），默认为false
 * @returns 格式化后的数字字符串
 */
export function formatNumber(
  value: number | string | null | undefined,
  defaultValue: string = '-',
  useLocale: boolean = false,
): string {
  if (value === null || value === undefined || value === '') {
    return defaultValue
  }

  const num = Number(value)
  if (isNaN(num)) {
    return defaultValue
  }

  // 如果需要千分位格式化
  if (useLocale) {
    return num.toLocaleString('zh-CN')
  }

  return num.toString()
}

// 添加获取当前北京时间的函数
/**
 * 获取当前北京时间的格式字符串(不带时区信息，适配Java LocalDateTime)
 * 用于解决提交时间早8小时的问题
 * @returns 格式化后的北京时间字符串
 */
export function getCurrentBeijingTime(): string {
  const now = new Date()

  // 获取本地时区偏移分钟数
  const localOffset = now.getTimezoneOffset()

  // 北京时区为+8小时，即480分钟，加上本地时区偏移
  const beijingOffset = 480 + localOffset

  // 按北京时间调整当前时间
  const beijingTime = new Date(now.getTime() + beijingOffset * 60 * 1000)

  // 格式化为 "yyyy-MM-ddTHH:mm:ss" 格式，不添加时区信息
  // 这种格式可以被Java LocalDateTime直接解析
  const year = beijingTime.getFullYear()
  const month = String(beijingTime.getMonth() + 1).padStart(2, '0')
  const day = String(beijingTime.getDate()).padStart(2, '0')
  const hours = String(beijingTime.getHours()).padStart(2, '0')
  const minutes = String(beijingTime.getMinutes()).padStart(2, '0')
  const seconds = String(beijingTime.getSeconds()).padStart(2, '0')

  return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`
}

/**
 * 格式化文件大小
 * @param bytes - 文件大小（字节）
 * @returns 格式化后的字符串
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
