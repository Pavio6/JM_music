import type { Carousel, Response } from '../api.d'
import api from '../index'

/**
 * 获取验证码
 */
export const getCaptcha = () => {
  return api.get('/common/captcha/get', {})
}

/**
 * 获取轮播图信息
 * @returns
 */
export const getCarouselList = (): Promise<Response<Carousel[]>> => {
  return api.get('/api/user/carousel/list', {})
}
