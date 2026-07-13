export const storage = {
  local: {
    has(key: string) {
      return localStorage.getItem(key) !== null
    },
    get(key: string) {
      const item = localStorage.getItem(key)
      // 处理存储的不是 json 的情况
      if (!item) return null
      try {
        return JSON.parse(item)
      } catch (error) {
        return item
      }
    },
    set(key: string, value: any) {
      localStorage.setItem(key, JSON.stringify(value))
    },
    remove(key: string) {
      localStorage.removeItem(key)
    },
    clear() {
      localStorage.clear()
    }
  },
  session: {
    has(key: string) {
      return sessionStorage.getItem(key) !== null
    },
    get(key: string) {
      const item = sessionStorage.getItem(key)
      if (!item) return null
      try {
        return JSON.parse(item)
      } catch (error) {
        return item
      }
    },
    set(key: string, value: any) {
      sessionStorage.setItem(key, JSON.stringify(value))
    },
    remove(key: string) {
      sessionStorage.removeItem(key)
    },
    clear() {
      sessionStorage.clear()
    }
  }
}
