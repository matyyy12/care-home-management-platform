import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { configureApiAuth } from './api/request'
import { useAuthStore } from './stores/auth'

const app = createApp(App)
const pinia = createPinia()
const auth = useAuthStore(pinia)

configureApiAuth({
  getToken: () => auth.token,
  onUnauthorized: () => auth.clearSession(),
})

app.use(pinia)
app.use(router)

app.mount('#app')
