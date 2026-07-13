/**
 * 反馈类型
 */
export const FeedbackType = {
  Bug: 'BUG',
  Complaint: 'COMPLAINT',
  Feature: 'FEATURE',
  Other: 'OTHER',
  Suggestion: 'SUGGESTION'
} as const

export const FeedbackStatus = {
  // 待处理
  PENDING: 'PENDING',
  // 处理中
  PROCESSING: 'PROCESSING',
  // 已解决
  RESOLVED: 'RESOLVED',
  // 已拒绝
  REJECTED: 'REJECTED'
} as const

export type FeecbackRequest = {
  /** 反馈内容 */
  feedbackContent: string
  /** 反馈标题 */
  feedbackTitle: string
  /** 反馈类型 */
  feedbackType: (typeof FeedbackType)[keyof typeof FeedbackType]
}

export type FeedbackResponse = {
  /** 回复内容 */
  adminReply: null | string
  createTime: string
  feedbackId: number
  /** 反馈标题 */
  feedbackTitle: string
  feedbackType: string
  /** 回复时间 */
  replyTime: null | string
  /** 处理状态 */
  status: (typeof FeedbackStatus)[keyof typeof FeedbackStatus]
}

export type FeedbackDetail = {
  adminName: string
  adminReply: string
  createTime: string
  feedbackContent: string
  feedbackId: number
  feedbackTitle: string
  feedbackType: string
  replyTime: string
  status: (typeof FeedbackStatus)[keyof typeof FeedbackStatus]
  userName: string
}
