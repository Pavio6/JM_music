const opt = Object.prototype.toString

/** 是否数组 */
export function isArray(obj: any) {
  return opt.call(obj) === '[object Array]'
}

/** 是否真对象 */
export function isObject(obj: any) {
  return opt.call(obj) === '[object Object]'
}

/** 是否字符 */
export function isString(obj: any) {
  return opt.call(obj) === '[object String]'
}

/** 是否数字 */
export function isNumber(obj: any) {
  return opt.call(obj) === '[object Number]'
}

/** 是否正则 */
export function isRegExp(obj: any) {
  return opt.call(obj) === '[object RegExp]'
}

/** 是否文件 */
export function isFile(obj: any) {
  return opt.call(obj) === '[object File]'
}

/** 是否二进制文件 */
export function isBlob(obj: any) {
  return opt.call(obj) === '[object Blob]'
}

/** 是否undefined */
export function isUndefined(obj: undefined) {
  return obj === undefined
}

/** 是否null */
export function isNull(obj: null) {
  return obj === null
}

/** 是否函数 */
export function isFunction(obj: any) {
  return typeof obj === 'function'
}

/** 是否空对象 {} */
export function isEmptyObject(obj: {}) {
  return isObject(obj) && Object.keys(obj).length === 0
}

/** 是否存在 */
export function isExist(obj: number) {
  return obj || obj === 0
}

/** 是否window对象 */
export function isWindow(el: Window & typeof globalThis) {
  return el === window
}

/**
 * 是否是手机号
 * 手机号(mobile phone)中国(宽松), 只要是13,14,15,16,17,18,19开头即可
 * @param {string} value 字符串
 * @return {boolean} boolean
 */
export function isPhoneNumber(value: string) {
  if (value === '') {
    return false
  }
  return /^(?:(?:\+|00)86)?1[3-9]\d{9}$/.test(value)
}

/**
 * 是否是座机号
 * 座机(tel phone)电话(国内),如: 0341-86091234
 * @param {*} value
 * @returns {boolean} boolean
 */
export function isTelPhone(value: string) {
  if (value === '') {
    return false
  }
  return /^(?:(?:\d{3}-)?\d{8}|^(?:\d{4}-)?\d{7,8})(?:-\d+)?$/.test(value)
}

/**
 * 帐号是否合法
 * 帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线组合
 * @param {string} value 字符串
 * @return {boolean} boolean
 */
export function isAccount(value: string) {
  if (value === '') {
    return false
  }
  return /^[a-zA-Z]\w{4,15}$/.test(value)
}

/**
 * 中文姓名
 * @param {string} value 字符串
 * @return {boolean} boolean
 */
export function isChineseName(value: string) {
  if (value === '') {
    return false
  }
  return /^(?:[\u4E00-\u9FA5·]{2,16})$/.test(value)
}

/**
 * 英文姓名
 * @param {string} value 字符串
 * @return {boolean} boolean
 */
export function isEnglishName(value: string) {
  if (value === '') {
    return false
  }
  return /(^[a-zA-Z][a-zA-Z\s]{0,20}[a-zA-Z]$)/.test(value)
}

/**
 * 邮箱
 * @param {string} value
 * @return {boolean} boolean
 */
export function isEmail(value: string) {
  if (value === '') {
    return false
  }
  return /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(
    value
  )
}

/**
 * 身份证
 * @param {string} value
 * @return {boolean} boolean
 */
export function isIdCard(value: string) {
  if (value === '') {
    return false
  }
  return /^\d{6}((((((19|20)\d{2})(0[13-9]|1[012])(0[1-9]|[12]\d|30))|(((19|20)\d{2})(0[13578]|1[02])31)|((19|20)\d{2})02(0[1-9]|1\d|2[0-8])|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))0229))\d{3})|((((\d{2})(0[13-9]|1[012])(0[1-9]|[12]\d|30))|((\d{2})(0[13578]|1[02])31)|((\d{2})02(0[1-9]|1\d|2[0-8]))|(([13579][26]|[2468][048]|0[048])0229))\d{2}))(\d|X|x)$/.test(
    value
  )
}

/**
 * 密码
 * 大写字母，小写字母，数字，特殊符号 `@#$%^&*`~()-+=` 中任意3项密码
 * @param {string} value
 * @return {boolean} boolean
 */
export function isPassword(value: string) {
  if (value === '') {
    return false
  }
  const regex =
    /^(?![a-zA-Z]+$)(?![A-Z0-9]+$)(?![A-Z\W_!@#$%^&*`~()-+=]+$)(?![a-z0-9]+$)(?![a-z\W_!@#$%^&*`~()-+=]+$)(?![0-9\W_!@#$%^&*`~()-+=]+$)[a-zA-Z0-9\W_!@#$%^&*`~()-+=]/
  return regex.test(value)
}
