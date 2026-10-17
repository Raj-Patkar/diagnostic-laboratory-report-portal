
import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

const apiProxy = {
  target: 'http://127.0.0.1:8086',
  changeOrigin: true,
  headers: {
    Origin: 'http://localhost:5173',
  },
}

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': apiProxy,
    },
  },
  preview: {
    proxy: {
      '/api': apiProxy,
    },
  },
})
