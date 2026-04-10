import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig(() => {
  const backendTarget = process.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

  return {
    plugins: [react()],
    build: {
      outDir: path.resolve(__dirname, '../server/src/main/resources/web'),
      emptyOutDir: true,
    },
    server: {
      port: 5173,
      proxy: {
        '/user': {
          target: backendTarget,
          changeOrigin: true,
        },
        '/session': {
          target: backendTarget,
          changeOrigin: true,
        },
        '/game': {
          target: backendTarget,
          changeOrigin: true,
        },
        '/db': {
          target: backendTarget,
          changeOrigin: true,
        },
        '/ws': {
          target: backendTarget,
          changeOrigin: true,
          ws: true,
        },
      },
    },
  };
});
