/**
 * 图片占位工具
 * 现在返回本地占位图，后续换成真实图片时只改这里即可
 */

export const getCoverUrl = () => '/images/cover-placeholder.svg'
export const getBannerUrl = () => '/images/banner-placeholder.svg'
export const getMascotUrl = () => '/images/mascot-placeholder.svg'
export const getQrUrl = () => '/images/qr-placeholder.svg'
export const getContentUrl = () => '/images/content-placeholder.svg'

// 批量生成封面，用于列表展示
export const getCoverUrls = (count) => {
  return Array.from({ length: count }, () => getCoverUrl())
}
