import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'

import App from './App.vue'
import routes from './router'

// Create app
const app = createApp(App)

// Create Pinia store
const pinia = createPinia()
app.use(pinia)

// Create router
const router = createRouter({
  history: createWebHistory(),
  routes
})

// Add router to app
app.use(router)

app.mount('#app')
