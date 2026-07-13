<template>
  <div class="common-layout">
    <el-container class="container">
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="logo">JM Music</div>
        <el-button class="logout-btn" @click="handleLogout">
          <el-icon>
            <SwitchButton />
          </el-icon>
          <span>退出登录</span>
        </el-button>
      </el-header>
      <el-container class="content">
        <!-- 侧边栏 -->
        <el-aside width="200px" class="aside">
          <el-menu router :default-active="$route.path" class="menu">
            <el-menu-item index="/home/overview">
              <el-icon>
                <Menu />
              </el-icon>
              <span>Overview</span>
            </el-menu-item>
            <el-menu-item index="/home/songs">
              <el-icon>
                <Mic />
              </el-icon>
              <span>Songs</span>
            </el-menu-item>
            <el-menu-item index="/home/singers">
              <el-icon>
                <User />
              </el-icon>
              <span>Singers</span>
            </el-menu-item>
            <el-menu-item index="/home/albums">
              <el-icon>
                <List />
              </el-icon>
              <span>Albums</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <!-- 主页面 -->
        <el-main class="main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';
  import { ElMessage } from 'element-plus';
  import { logout } from '@/api/auth';
  import {
    Menu,
    Mic,
    User,
    List,
    SwitchButton
  } from '@element-plus/icons-vue';

  const router = useRouter();

  const handleLogout = async () => {
    try {
      const res = await logout();
      if (res.state) {
        localStorage.removeItem('token');
        ElMessage.success('退出成功');
        router.push('/login');
      } else {
        ElMessage.error(res.message || '退出失败');
      }
    } catch (error) {
      ElMessage.error('退出失败');
    }
  };
</script>

<style scoped>
  .common-layout {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
  }

  .container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .content {
    flex: 1;
    display: flex;
  }

  .header {
    background-color: #1e2124;
    color: #fff;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    height: 60px;
    border-bottom: 1px solid #2d3035;
    position: relative;
    z-index: 10;
  }

  .logo {
    font-size: 24px;
    font-weight: bold;
    background: linear-gradient(45deg, #409EFF, #36D1DC);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .logout-btn {
    display: flex;
    align-items: center;
    gap: 5px;
    background: linear-gradient(45deg, #409EFF, #36D1DC);
    border: none;
    color: white;
    padding: 8px 16px;
    border-radius: 20px;
    transition: all 0.3s ease;
  }

  .logout-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(54, 209, 220, 0.3);
    background: linear-gradient(45deg, #36D1DC, #409EFF);
  }

  .logout-btn:active {
    transform: translateY(0);
  }

  .aside {
    background-color: #1e2124;
    border-right: 1px solid #2d3035;
    width: 200px;
    flex-shrink: 0;
  }

  .menu {
    border-right: none;
    background-color: #1e2124;
    height: 100%;
  }

  :deep(.el-menu) {
    border-right: none;
  }

  :deep(.el-menu-item) {
    color: #ffffff;
  }

  :deep(.el-menu-item.is-active) {
    background-color: #2d3035;
    color: #409EFF;
  }

  :deep(.el-menu-item:hover) {
    background-color: #2d3035;
  }

  .main {
    background-color: #1e2124;
    color: #ffffff;
    flex: 1;
    padding: 20px;
    overflow-y: auto;
  }

  :deep(.el-container) {
    height: 100%;
  }
</style>