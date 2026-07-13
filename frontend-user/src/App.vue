<template>
  <div class="flex h-screen bg-gray-900 text-white px-6 pt-6 pb-24">
    <!-- Sidebar Navigation -->
    <div class="w-64 bg-gray-800 p-6 flex flex-col rounded-xl">
      <SideBar />
    </div>

    <!-- Main Content Area -->
    <main class="flex-1 bg-gray-900 ml-6 overflow-auto">
      <router-view></router-view>
    </main>

    <Modal ref="ModalRef">
      <LoginForm
        @login-success="handleSuccess('login')"
        @register-success="handleSuccess('register')"
      />
    </Modal>

    <div class="fixed right-2 bottom-2 w-12 h-12">
      <MuscicControl />
    </div>
    <ModalsContainer />
    <MusicVideo v-if="showMv && currentMvId" :mvId="currentMvId" @close="closeMv" />
  </div>
</template>

<script lang="ts" setup>
  import { ref, provide, watch, onMounted, onBeforeUnmount } from 'vue'
  import LoginForm from '@/components/LoginForm.vue'
  import Modal from '@/components/common/Modal.vue'
  import SideBar from '@/components/layout/SideBar.vue'
  import { ModalsContainer } from 'vue-final-modal'
  import MuscicControl from '@/components/MusicControl.vue'
  import MusicVideo from '@/components/MusicVideo.vue'
  import { useUserStore } from '@/stores/user'
  import { useMessageStore } from '@/stores/message'
  import { useI18n } from 'vue-i18n'
  import localforage from 'localforage'

  defineOptions({
    name: 'App'
  })

  const { t, locale } = useI18n()
  // login
  const userStore = useUserStore()
  const messageStore = useMessageStore()
  const ModalRef = ref<InstanceType<typeof Modal> | null>(null)

  function handleSuccess(type: 'login' | 'register') {
    ModalRef.value && ModalRef.value.close()
    userStore.toggleLoginModal(false)
    if (type === 'login') {
      console.log('登录成功')
    } else {
      console.log('注册成功')
    }
  }

  // 监听 showLoginModal 状态，显示登录弹窗
  watch(
    () => userStore.showLoginModal,
    (val) => {
      if (val && ModalRef.value) {
        ModalRef.value.open({
          title: t('common.loginOrRegister'),
          showConfirm: true,
          showFoot: false,
          afterClose: () => {
            userStore.showLoginModal = false
          }
        })
      }
    }
  )

  // MV
  const showMv = ref(false)
  const currentMvId = ref<string | number | null>(null)
  function openMv(mvId: string | number) {
    currentMvId.value = mvId
    showMv.value = true
  }
  function closeMv() {
    showMv.value = false
    currentMvId.value = null
  }
  provide('openMv', openMv)

  // 监听页面关闭事件，发送下线消息
  const handleBeforeUnload = (event: BeforeUnloadEvent) => {
    if (userStore.isLogin) {
      try {
        messageStore.sendOfflineAndDisconnect()
      } catch (error) {
        console.log('发送下线消息失败', error)
      }
    }
  }

  onMounted(() => {
    localforage.getItem('lang').then((res) => {
      locale.value = (res as string) ?? 'zh'
      document.title = t('AppName')
    })

    // 如果用户已登录，初始化WebSocket连接
    if (userStore.isLogin) {
      userStore.refreshUserInfo()
      userStore.getUserQueue()
      messageStore.initWebSocket()
    }

    // 添加页面关闭事件监听
    window.addEventListener('beforeunload', handleBeforeUnload)
  })

  onBeforeUnmount(() => {
    // 移除页面关闭事件监听
    window.removeEventListener('beforeunload', handleBeforeUnload)

    // 如果用户已登录，尝试发送下线消息，但不抛出错误
    if (userStore.isLogin) {
      try {
        messageStore.sendOfflineAndDisconnect()
      } catch (error) {
        console.log('发送下线消息失败', error)
      }
    }
  })
</script>

<style>
  :root {
    --web-background: #0f161e;
    --primary-color: #fbfbfb;
  }

  body {
    font-family:
      -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans',
      'Helvetica Neue', sans-serif;
  }
</style>
