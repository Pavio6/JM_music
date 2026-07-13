<template>
  <div class="login-form">
    <div class="form-header">
      <div :class="['tab-item', { active: isLoginMode }]" @click="switchMode(true)">
        {{ t('common.login') }}
      </div>
      <div :class="['tab-item', { active: !isLoginMode }]" @click="switchMode(false)">
        {{ t('common.register') }}
      </div>
    </div>

    <form @submit.prevent="() => {}" class="form-content">
      <!-- 登录表单 -->
      <template v-if="isLoginMode">
        <div class="form-item">
          <Input
            v-model="loginForm.userName"
            type="text"
            :placeholder="t('common.userName')"
            required
          />
        </div>
        <div class="form-item">
          <Input
            v-model="loginForm.userPass"
            type="password"
            :placeholder="t('common.userPass')"
            required
          />
        </div>
        <div class="form-item captcha">
          <Input
            v-model="loginForm.captchaCode"
            type="text"
            :placeholder="t('common.captchaCode')"
            required
          />
          <!-- 验证码图片组件 -->
          <div class="captcha-img" @click="refreshCaptcha">
            <img :src="captchaUrl" :alt="t('common.captchaCode')" />
          </div>
        </div>
      </template>

      <!-- 注册表单 -->
      <template v-else>
        <div class="form-item">
          <Input
            v-model="registerForm.userName"
            type="text"
            :placeholder="t('common.userName')"
            required
          />
        </div>
        <div class="form-item">
          <Input
            v-model="registerForm.userPass"
            type="password"
            :placeholder="t('common.userPass')"
            required
          />
        </div>
        <div class="form-item">
          <Input
            v-model="registerForm.userEmail"
            type="email"
            :placeholder="t('common.userEmail')"
            required
          />
        </div>
        <div class="form-item">
          <TextArea v-model="registerForm.userBio" :placeholder="t('common.userBio')" rows="3" />
        </div>
        <div class="form-item">
          <Select v-model="registerForm.userSex" required>
            <option value="" disabled>{{ t('common.selectSex') }}</option>
            <option :value="0">{{ t('common.girl') }}</option>
            <option :value="1">{{ t('common.boy') }}</option>
          </Select>
        </div>
        <div class="form-item">
          <Input
            v-model="registerForm.userBirth"
            type="date"
            :placeholder="t('common.userBirth')"
            required
            :max="today"
          />
        </div>
        <div class="form-item">
          <FileInput
            @change="handleAvatarUpload"
            accept="image/*"
            :placeholder="t('common.uploadAvatar')"
          />
          <div v-if="avatarPreview" class="avatar-preview">
            <img :src="avatarPreview" :alt="t('common.avatarPreview')" />
          </div>
        </div>
      </template>

      <Button type="primary" class="submit-btn" @click="handleSubmit">
        {{ isLoginMode ? t('common.login') : t('common.register') }}
      </Button>
    </form>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { getCaptcha } from '@/api/modules/common'
  import { useUserStore } from '@/stores/user'
  import { toast } from '@/utile'
  import type { UserRegisterRequest } from '@/api/modules/user/user.d'
  import Input from '@/components/common/Input.vue'
  import TextArea from '@/components/common/TextArea.vue'
  import Select from '@/components/common/Select.vue'
  import FileInput from '@/components/common/FileInput.vue'
  import Button from '@/components/common/Button.vue'
  import dayjs from 'dayjs'
  import { useI18n } from 'vue-i18n'

  const { t } = useI18n()

  const emit = defineEmits(['login-success', 'register-success'])

  const userStore = useUserStore()
  const isLoginMode = ref(true)
  const captchaUrl = ref('')
  const avatarPreview = ref('')

  // 登录表单数据
  const loginForm = ref({
    userName: '',
    userPass: '',
    captchaCode: '',
    captchaKey: ''
  })

  // 注册表单数据
  const registerForm = ref<UserRegisterRequest>({
    userName: '',
    userPass: '',
    userEmail: '',
    userBio: '',
    userSex: 0,
    userBirth: '',
    userAvatar: ''
  })

  // 计算今天的日期作为生日选择的最大值
  const today = computed(() => {
    const date = dayjs().subtract(1, 'day').format('YYYY-MM-DD')
    return date
  })

  // 切换登录/注册模式
  const switchMode = (mode: boolean) => {
    isLoginMode.value = mode
  }

  // 刷新验证码
  const refreshCaptcha = async () => {
    try {
      const { data } = await getCaptcha()
      captchaUrl.value = data?.image || ''
      loginForm.value.captchaKey = data?.key || ''
    } catch (error) {
      console.error('获取验证码失败:', error)
      // toast.error('获取验证码失败，请重试')
    }
  }

  // 处理头像上传
  const handleAvatarUpload = (event: Event) => {
    const input = event.target as HTMLInputElement
    if (input.files && input.files[0]) {
      registerForm.value.userAvatar = input.files[0]
      const reader = new FileReader()
      reader.onload = (e) => {
        avatarPreview.value = e.target?.result as string
      }
      reader.readAsDataURL(input.files[0])
    }
  }

  const loginFormValid: {
    name: string
    validate: (value: any) => string
  }[] = [
    {
      name: 'userName',
      validate(value: string) {
        return !value ? t('common.userNameEmpty') : ''
      }
    },
    {
      name: 'userPass',
      validate(value: string) {
        return !value ? t('common.userPassEmpty') : ''
      }
    },
    {
      name: 'captchaCode',
      validate(value: string) {
        return !value ? t('common.captchaCodeEmpty') : ''
      }
    }
  ]

  const registerFormValid: {
    name: string
    validate: (value: any) => string
  }[] = [
    {
      name: 'userName',
      validate(value: string) {
        if (!value) return t('common.userNameEmpty')
        return /^[A-Za-z][A-Za-z0-9_]{2,19}$/.test(value) ? '' : t('common.userNameLength')
      }
    },
    {
      name: 'userPass',
      validate(value: string) {
        if (!value) return t('common.userPassEmpty')
        return /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{8,}$/.test(value)
          ? ''
          : t('common.userPassLength')
      }
    },
    {
      name: 'userEmail',
      validate(value: string) {
        if (!value) return t('common.userEmailEmpty')
        return /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(value)
          ? ''
          : t('common.userEmailInvalid')
      }
    },
    {
      name: 'userBio',
      validate(value: string) {
        // if (!value) return '个人简介不能为空'
        return value.length <= 500 ? '' : t('common.userBioLength')
      }
    },
    {
      name: 'userSex',
      validate(value: number) {
        return value !== null && value !== undefined ? '' : t('common.selectSex')
      }
    },
    {
      name: 'userBirth',
      validate(value: string) {
        if (!value) return t('common.userBirthEmpty')
        return dayjs(value).isBefore(dayjs()) ? '' : t('common.userBirthInvalid')
      }
    }
    // {
    //   name: 'userAvatar',
    //   validate(value: File) {
    //     return value ? '' : '请上传头像'
    //   }
    // }
  ]

  // 表单提交
  const handleSubmit = async () => {
    try {
      if (isLoginMode.value) {
        // 登录表单验证
        for (const field of loginFormValid) {
          const errorMessage = field.validate(
            loginForm.value[field.name as keyof typeof loginForm.value]
          )
          if (errorMessage) {
            toast.error(errorMessage)
            return
          }
        }
        // 登录请求
        const response = await userStore.login(loginForm.value)
        if (response) {
          toast.success(t('common.loginSuccess'))
          emit('login-success')
        } else {
          refreshCaptcha() // 登录失败时刷新验证码
        }
      } else {
        // 注册表单验证
        for (const field of registerFormValid) {
          const errorMessage = field.validate(
            registerForm.value[field.name as keyof typeof registerForm.value]
          )
          if (errorMessage) {
            toast.error(errorMessage)
            return
          }
        }

        // 注册请求
        const response = await userStore.register(registerForm.value)
        if (response) {
          toast.success(t('common.registerSuccess'))
          switchMode(true) // 切换到登录模式
        }
      }
    } catch (error) {
      console.error('表单提交失败:', error)
    }
  }

  // 组件挂载时获取验证码
  refreshCaptcha()
</script>

<style lang="scss" scoped>
  .login-form {
    width: 100%;
    max-width: 400px;
    margin: 0 auto;
    padding: 20px;

    .form-header {
      display: flex;
      margin-bottom: 20px;

      .tab-item {
        flex: 1;
        text-align: center;
        padding: 10px;
        cursor: pointer;
        color: #666;

        &.active {
          color: #1890ff;
          border-bottom: 2px solid #1890ff;
        }
      }
    }

    .form-content {
      .form-item {
        margin-bottom: 16px;

        &.captcha {
          display: flex;
          gap: 12px;

          input {
            flex: 1;
          }

          .captcha-img {
            width: 100px;
            height: 38px;
            cursor: pointer;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }
          }
        }
      }

      .avatar-preview {
        margin-top: 8px;
        width: 100px;
        height: 100px;
        border-radius: 50%;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .submit-btn {
        width: 100%;
        padding: 10px;
        background-color: #1890ff;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background-color: #40a9ff;
        }

        &:active {
          background-color: #096dd9;
        }
      }
    }
  }
</style>
