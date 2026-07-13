import { createI18n } from 'vue-i18n'
import en from '@/locales/en.json'
import zh from '@/locales/zh-CN.json'
import localforage from 'localforage'

// 创建 i18n 实例，默认使用中文
const i18n = createI18n({
  legacy: false,
  locale: 'zh',
  fallbackLocale: 'zh',
  messages: {
    en,
    zh
  }
})

// 从 localforage 异步加载语言设置
localforage
  .getItem('lang')
  .then((storedLocale) => {
    if (storedLocale && (storedLocale === 'en' || storedLocale === 'zh')) {
      i18n.global.locale.value = storedLocale as 'en' | 'zh'
    }
  })
  .catch((e) => {
    console.error('Failed to access localforage:', e)
  })

export default i18n
