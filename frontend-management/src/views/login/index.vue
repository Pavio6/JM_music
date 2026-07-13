<script setup lang="ts">
  import { ref, onMounted } from 'vue';
  import { useRouter } from 'vue-router';
  import { ElMessage } from 'element-plus';
  import { getCaptcha, login } from '@/api/auth';

  const router = useRouter();

  const loginForm = ref({
    adminName: '',
    adminPass: '',
    captchaKey: '',
    captchaCode: ''
  });

  const captchaImg = ref('');

  // 获取验证码
  const refreshCaptcha = async () => {
    try {
      const res = await getCaptcha();
      if (res.state) {
        const data = res.data;
        captchaImg.value = data.image;
        loginForm.value.captchaKey = data.key;
      }
    } catch (error) {
      ElMessage.error('获取验证码失败');
    }
  };

  // 登录
  const handleLogin = async () => {
    try {
      const res = await login(loginForm.value);
      if (res.state) {
        const { token } = res.data;
        console.log('Login success, token:', token);
        localStorage.setItem('token', token);
        ElMessage.success('登录成功');
        router.push('/home');
      } else {
        ElMessage.error(res.message);
        await refreshCaptcha();
      }
    } catch (error) {
      ElMessage.error('登录失败');
      await refreshCaptcha();
    }
  };

  onMounted(() => {
    refreshCaptcha();
  });
</script>

<template>
  <div class="login-container">
    <div class="login-content">
      <div class="login-box">
        <div class="login-header">
          <div class="music-icon">
            <i class="el-icon-headset"></i>
          </div>
          <h1>JM Music</h1>
          <p class="subtitle">后台管理系统</p>
        </div>
        <el-form :model="loginForm" class="login-form">
          <el-form-item>
            <el-input v-model="loginForm.adminName" placeholder="请输入用户名" prefix-icon="User" class="custom-input" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="loginForm.adminPass" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password
              class="custom-input" />
          </el-form-item>
          <el-form-item>
            <div class="captcha-container">
              <el-input v-model="loginForm.captchaCode" placeholder="请输入验证码" class="captcha-input custom-input" />
              <img :src="captchaImg" class="captcha-img" @click="refreshCaptcha" alt="验证码" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" @click="handleLogin">
              登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="animated-bg"></div>
  </div>
</template>

<style>
  /* 添加全局样式重置 */
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  html,
  body {
    height: 100%;
    width: 100%;
    overflow: hidden;
  }
</style>

<style scoped>
  .login-container {
    height: 100vh;
    width: 100vw;
    display: flex;
    justify-content: center;
    align-items: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    overflow: hidden;
    background: linear-gradient(135deg, #1a1a1a 0%, #262626 100%);
    margin: 0;
    padding: 0;
  }

  .animated-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background:
      radial-gradient(circle at 20% 20%, rgba(76, 0, 255, 0.15) 0%, transparent 40%),
      radial-gradient(circle at 80% 80%, rgba(255, 0, 128, 0.15) 0%, transparent 40%);
    z-index: 1;
    animation: bgAnimation 15s ease infinite;
  }

  @keyframes bgAnimation {
    0% {
      transform: scale(1);
    }

    50% {
      transform: scale(1.2);
    }

    100% {
      transform: scale(1);
    }
  }

  .login-content {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100%;
    max-width: 440px;
    padding: 0 20px;
    z-index: 2;
  }

  .login-box {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 40px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }

  .login-header {
    text-align: center;
    margin-bottom: 40px;
  }

  .music-icon {
    font-size: 48px;
    color: #409EFF;
    margin-bottom: 16px;
  }

  .login-header h1 {
    font-size: 32px;
    color: #333;
    margin: 0;
    font-weight: 600;
    background: linear-gradient(45deg, #409EFF, #FF6B6B);
    background-clip: text;
    -webkit-text-fill-color: transparent;
  }

  .subtitle {
    font-size: 16px;
    color: #666;
    margin-top: 8px;
  }

  .login-form {
    margin-top: 30px;
  }

  .custom-input :deep(.el-input__wrapper) {
    border-radius: 8px;
    box-shadow: none !important;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;
    background-color: #fff;
  }

  .custom-input :deep(.el-input__wrapper:not(.is-focus):hover) {
    border-color: #409EFF;
  }

  .custom-input :deep(.el-input__wrapper.is-focus) {
    border-color: #409EFF;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1) !important;
  }

  .captcha-container {
    display: flex;
    gap: 12px;
  }

  .captcha-input {
    flex: 1;
  }

  .captcha-img {
    height: 40px;
    border-radius: 8px;
    cursor: pointer;
    transition: transform 0.3s ease;
  }

  .captcha-img:hover {
    transform: scale(1.05);
  }

  .login-button {
    width: 100%;
    height: 44px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 500;
    background: linear-gradient(45deg, #409EFF, #36D1DC);
    border: none;
    transition: all 0.3s ease;
  }

  .login-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  .el-form-item {
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    padding: 1px 15px;
  }

  :deep(.el-input__inner) {
    height: 42px;
  }
</style>