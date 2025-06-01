import { defineConfig } from 'vite';
import { svelte } from '@sveltejs/vite-plugin-svelte';

// todo: what is vite?

export default defineConfig({
  plugins: [svelte()],
  server: {
    port: 8089,
    proxy: {
      '/supplier-facts-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/supplier-facts-service/, '/supplier-facts-service')
      },
      '/rfq-service': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/rfq-service/, '/rfq-service')
      },
    }
  }
});