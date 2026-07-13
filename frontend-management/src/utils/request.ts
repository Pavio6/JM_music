import axios from 'axios';

// 不需要token的白名单路径
const whiteList = ['/captcha/get', '/admin/login'];

// 创建一个 axios 实例，设置基础的配置项
const request = axios.create({
    baseURL: 'http://localhost:8082',  // 设置请求的基础 URL
    timeout: 5000,  // 设置请求超时时间为 5000ms（5秒）
    headers: {
        'Content-Type': 'application/json' // 请求的数据格式为json
    }
});

// 请求拦截器：每次请求发送前都会触发
request.interceptors.request.use(
    config => {
        // 确保headers对象存在
        config.headers = config.headers || {};
        
        // 如果是白名单中的路径，不需要添加token
        if (whiteList.includes(config.url || '')) {
            return config;
        }
        
        // 从 localStorage 获取 token，判断用户是否已登录
        const token = localStorage.getItem('token');
        
        if (token && config.headers) {
            config.headers.Authorization = token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器：每次请求返回时都会触发
request.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        return Promise.reject(error);
    }
);

// 导出 axios 实例，使其可以在其他模块中使用
export default request;
