import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 5431, // 前端开发服务器端口
    proxy: {
      // 将 /card 请求转发到后端服务器
      '/card': {
        target: 'http://localhost:3306', // 后端服务器地址
        changeOrigin: true, // 允许跨域
        rewrite: (path) => path.replace(/^\/card/, '') // 可选：去掉 /card 前缀
      }
    }
  }
})