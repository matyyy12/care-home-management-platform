<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AlertMessage from '@/components/ui/AlertMessage.vue'
import AppButton from '@/components/ui/AppButton.vue'
import AppInput from '@/components/ui/AppInput.vue'
import FormField from '@/components/ui/FormField.vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  email: 'manager@seniorhouse.local',
  password: 'Password123!',
})
const error = ref('')
const loading = ref(false)

const demoAccounts = [
  'director@seniorhouse.local',
  'manager@seniorhouse.local',
  'employee@seniorhouse.local',
  'medical@seniorhouse.local',
  'admin@seniorhouse.local',
]

async function submit() {
  error.value = ''
  loading.value = true

  try {
    await auth.login(form)
    await router.push(route.query.redirect || { name: 'dashboard' })
  } catch (err) {
    error.value = err?.message || 'Nie udalo sie zalogowac.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="flex min-h-screen items-center justify-center bg-slate-100 px-4 py-10">
    <section class="w-full max-w-md rounded-2xl bg-white p-8 shadow-sm">
      <p class="text-xs font-semibold uppercase tracking-wide text-emerald-700">Senior House</p>
      <h1 class="mt-2 text-2xl font-semibold text-slate-900">Logowanie</h1>
      <p class="mt-2 text-sm text-slate-600">
        Uzyj konta demo, aby wejsc do panelu z rola przypisana przez backend.
      </p>

      <AlertMessage v-if="error" variant="error" class="mt-6">
        {{ error }}
      </AlertMessage>

      <form class="mt-6 space-y-4" @submit.prevent="submit">
        <FormField label="Email" for-id="email" required>
          <AppInput id="email" v-model="form.email" type="email" required autocomplete="username" />
        </FormField>

        <FormField label="Haslo" for-id="password" required>
          <AppInput
            id="password"
            v-model="form.password"
            type="password"
            required
            autocomplete="current-password"
          />
        </FormField>

        <AppButton type="submit" class="w-full" :loading="loading">
          Zaloguj
        </AppButton>
      </form>

      <div class="mt-6 rounded-lg border border-slate-200 bg-slate-50 px-4 py-3 text-sm text-slate-600">
        <p class="font-medium text-slate-800">Konta demo</p>
        <p class="mt-1">Haslo dla kazdego konta: <span class="font-medium">Password123!</span></p>
        <ul class="mt-3 space-y-1">
          <li v-for="account in demoAccounts" :key="account">
            <button
              type="button"
              class="text-left text-emerald-700 hover:text-emerald-900"
              @click="form.email = account"
            >
              {{ account }}
            </button>
          </li>
        </ul>
      </div>
    </section>
  </main>
</template>
