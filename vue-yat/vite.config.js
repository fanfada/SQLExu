import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://60.204.236.211:8081',  // 目标服务器地址
        changeOrigin: true,  // 修改请求头中的 Origin，避免跨域问题
        // rewrite: (path) => path.replace(/^\/api/, ''),  // 可选：重写路径，将 /api 替换为空
        secure: false,  // 如果目标服务器是 https，设置为 true
      },
    },
  },
})
