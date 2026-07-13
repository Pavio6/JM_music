import type { PageRequest, Response, PageResponse } from '@/api/api.d'
import api from '../../index'
import type { FeecbackRequest, FeedbackResponse, FeedbackDetail } from './feedback.d'

/** 添加反馈信息 */
export const addFeedback = (data: FeecbackRequest): Promise<Response<boolean>> =>
  api.post('/api/user/feedback/add', data)

/** 编辑反馈信息 */
export const editFeedback = (
  feedbackId: number,
  data: FeecbackRequest
): Promise<Response<boolean>> => api.put(`/api/user/feedback/edit/${feedbackId}`, data)

/** 查看用户个人反馈列表 */
export const getFeedbackList = (): Promise<Response<FeedbackResponse[]>> =>
  api.get('/api/user/feedback/list', {})

/** 查看反馈信息详情 */
export const getFeedbackDetail = (feedbackId: number): Promise<Response<FeedbackDetail>> =>
  api.get(`/api/user/feedback/${feedbackId}`)

/** 删除反馈信息 */
export const deleteFeedback = (feedbackId: number): Promise<Response<boolean>> =>
  api.delete(`/api/user/feedback/delete/${feedbackId}`)
