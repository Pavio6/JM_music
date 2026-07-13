/** 资源服务器地址 */
export const ASSERT_URL = import.meta.env.VITE_APP_ASSETS_URL

/**
 * 获取服务器的资源地址
 * @param assets 地址
 * @returns {string} 服务器地址
 */
export function serverAssetsURI(assets: string) {
  if (assets.startsWith('http')) {
    return assets
  }
  return `${ASSERT_URL}${assets}`
}

/** 金额千分位 */
export function formatAmount(amount: number | string) {
  return String(amount).replace(/\d{1,3}(?=(\d{3})+(\.\d*)?$)/g, '$&,')
}
