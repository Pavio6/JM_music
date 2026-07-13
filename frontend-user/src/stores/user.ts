import { ref } from 'vue'
import { defineStore } from 'pinia'
import * as userApi from '@/api/modules/user/user'
import {
  MessagePermission,
  Visibility,
  type UpdateUserInfoRequest,
  type UserInfoMine,
  type UserLoginRequest,
  type UserRegisterRequest
} from '@/api/modules/user/user.d'
import { toast } from '@/utile'
import { useMusicStore } from './music'
import { useRouter } from 'vue-router'
import { getCurrentQueue } from '@/api/modules/queue/queue'
import { useI18n } from 'vue-i18n'
import { useMessageStore } from './message'

export const useUserStore = defineStore(
  'user',
  () => {
    const { t } = useI18n()
    const router = useRouter()
    const token = ref<string>('')
    const userInfo = ref<UserInfoMine>({
      userAvatar: '',
      userBio: '',
      userBirth: '',
      userEmail: '',
      userId: 0,
      userName: '',
      userSex: 0,
      userPrivacy: {
        followersVisibility: Visibility.Private,
        followingVisibility: Visibility.Private,
        messagePermission: MessagePermission.All,
        playlistVisibility: Visibility.Private,
        profileVisibility: Visibility.Private
      }
    })
    const isLogin = ref(false)
    const musicStore = useMusicStore()

    // 控制登录弹窗显示的状态
    const showLoginModal = ref(false)
    function toggleLoginModal(flag?: boolean) {
      showLoginModal.value = flag !== undefined ? flag : !showLoginModal.value
    }

    async function login(loginFormData: UserLoginRequest) {
      try {
        const { data } = await userApi.login(loginFormData)
        if (data && data.token && data.user) {
          token.value = data.token
          isLogin.value = true
          userInfo.value.userId = data.user.userId
          userInfo.value.userAvatar = data.user.userAvatar
          userInfo.value.userName = data.user.userName
          await refreshUserInfo()
          await getUserQueue()
          return true
        }
        return false
      } catch (error: any) {
        if (error?.message) {
          toast.error(error?.message)
        }
        return false
      }
    }

    function logout() {
      const messageStore = useMessageStore()
      userApi.logout().then(() => {
        musicStore.clearPlaylist()
        messageStore.sendOfflineAndDisconnect()

        token.value = ''
        userInfo.value = {
          userAvatar: '',
          userBio: '',
          userBirth: '',
          userEmail: '',
          userId: 0,
          userName: '',
          userSex: 0,
          userPrivacy: {
            followersVisibility: Visibility.Private,
            followingVisibility: Visibility.Private,
            messagePermission: MessagePermission.All,
            playlistVisibility: Visibility.Private,
            profileVisibility: Visibility.Private
          }
        }
        isLogin.value = false
        showLoginModal.value = false
        toast.success(t('common.logoutSuccess'))
        router.push('/')
        return true
      })
    }

    async function refreshUserInfo() {
      try {
        if (userInfo.value.userId) {
          const { data } = await userApi.getMineInfo()
          if (data) {
            userInfo.value = data
            return true
          }
        }
        return false
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return false
      }
    }

    async function getUserQueue() {
      try {
        const { data } = await getCurrentQueue()
        if (data) {
          if (data.songsInfoList.length > 0) {
            musicStore.setPlaylist(data.songsInfoList)
          }
          if (data.currentSongId && data.currentIndex >= 0) {
            musicStore.setPlayListCurrentSong(data.currentIndex, false)
          }
        }
      } catch (error) {
        console.error('获取用户队列失败:', error)
      }
    }

    async function register(registerFormData: UserRegisterRequest) {
      try {
        const { data } = await userApi.register(registerFormData)
        if (data) {
          // 注册成功
          return true
        }
        return false
      } catch (error: any | Error) {
        console.error('注册失败:', error)
        let message = error?.message ?? t('common.registerFailed')
        toast.error(message)
        return false
      }
    }

    async function updateUserInfo(updateData: UpdateUserInfoRequest) {
      try {
        const { data } = await userApi.updateUserInfo(updateData)
        if (data) {
          await refreshUserInfo()
          toast.success(t('common.userInfoUpdateSuccess'))
          return true
        } else {
          toast.error(t('common.userInfoUpdateFailed'))
          return false
        }
      } catch (error) {
        console.error('更新用户信息失败:', error)
        toast.error(t('common.userInfoUpdateFailed'))
        return false
      }
    }

    return {
      token,
      userInfo,
      isLogin,
      login,
      register,
      logout,
      updateUserInfo,
      showLoginModal,
      toggleLoginModal,
      refreshUserInfo,
      getUserQueue
    }
  },
  {
    persist: {
      key: 'user',
      storage: localStorage,
      // 过滤掉不需要持久化的数据
      omit: ['showLoginModal']
    }
  }
)
