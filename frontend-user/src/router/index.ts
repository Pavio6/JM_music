import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      meta: {
        title: 'route.home',
        icon: 'material-symbols:home-outline-rounded',
        inSide: true
      },
      component: () => import('../views/Home.vue')
    },
    {
      path: '/playList',
      name: 'playList',
      meta: {
        title: 'route.playList',
        icon: 'solar:playlist-2-bold-duotone',
        inSide: true,
        requireLogin: true
      },
      component: () => import('../views/PlayList.vue')
    },
    {
      path: '/leaderboard',
      name: 'leaderboard',
      meta: {
        title: 'route.leaderboard',
        icon: 'iconoir:leaderboard',
        inSide: true,
        requireLogin: true
      },
      component: () => import('../views/Leaderboard.vue')
    },
    {
      path: '/favorite',
      name: 'favorite',
      meta: {
        title: 'route.favorite',
        icon: 'uil:favorite',
        inSide: true,
        requireLogin: true
      },
      component: () => import('../views/Favorite.vue')
    },
    {
      path: '/user',
      name: 'user',
      meta: {
        title: 'route.user',
        icon: 'material-symbols:person-outline',
        requireLogin: true
      },
      component: () => import('../views/User.vue')
    },
    {
      path: '/playlist/:id',
      name: 'playlist-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/PlayListDetail.vue')
    },
    {
      path: '/album/:id',
      name: 'album-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/AlbumDetail.vue')
    },
    {
      path: '/singer/:id',
      name: 'singer-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/SingerDetail.vue')
    },
    {
      path: '/song/:id',
      name: 'song-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/SongDetail.vue')
    },
    {
      path: '/search',
      name: 'search',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/Search.vue')
    },
    {
      path: '/message/:id?',
      name: 'message',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/Message.vue')
    },
    {
      path: '/feedback',
      name: 'feedback',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/Feedback.vue')
    },
    {
      path: '/follow/:type',
      name: 'follow',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/Follow.vue')
    },
    {
      path: '/follow/:type/:userId',
      name: 'user-follow',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/Follow.vue')
    },
    {
      path: '/user/:id',
      name: 'user-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/UserDetail.vue')
    },
    {
      path: '/playlist/my/:id',
      name: 'mine-playlist-detail',
      meta: {
        requireLogin: true
      },
      component: () => import('../views/MinePlayListDetail.vue')
    }
  ]
})

export default router

// 全局路由守卫，判断是否需要登录
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const { isLogin } = storeToRefs(userStore)
  if (to.meta.requireLogin && !isLogin.value) {
    // 触发登录弹窗
    userStore.toggleLoginModal(true)
    next(false)
  } else {
    next()
  }
})
