import { createRouter, createWebHistory } from 'vue-router';

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/home/index.vue'),
    redirect: '/home/overview',
    children: [
      {
        path: 'overview',
        name: 'Overview',
        component: () => import('../views/overview/index.vue')
      },
      {
        path: 'songs',
        name: 'Songs',
        component: () => import('../views/songs/index.vue')
      },
      {
        path: 'singers',
        name: 'Singers',
        component: () => import('../views/singers/index.vue')
      },
      {
        path: 'singers/add',
        name: 'AddSinger',
        component: () => import('../views/singers/add.vue')
      },
      {
        path: 'singers/edit/:id',
        name: 'EditSinger',
        component: () => import('../views/singers/edit.vue')
      },
      {
        path: 'albums',
        name: 'Albums',
        component: () => import('../views/albums/index.vue')
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫 用于在用户导航到不同路由之前进行权限验证
// to:目标路由 from:当前路由 next:回调函数
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  if (to.path !== '/login' && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router; 