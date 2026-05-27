<script setup>
import { onMounted, ref } from 'vue'
import { createSenior, fetchSeniors } from '@/api/seniors'

const seniors = ref([])
const loading = ref(true)
const error = ref(null)
const submitting = ref(false)

const form = ref({
  firstName: '',
  lastName: '',
  roomNumber: '',
})

async function loadSeniors() {
  loading.value = true
  error.value = null
  try {
    seniors.value = await fetchSeniors()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  submitting.value = true
  error.value = null
  try {
    const created = await createSenior({
      firstName: form.value.firstName,
      lastName: form.value.lastName,
      roomNumber: form.value.roomNumber || null,
    })
    seniors.value = [...seniors.value, created]
    form.value = { firstName: '', lastName: '', roomNumber: '' }
  } catch (e) {
    error.value = e.message
  } finally {
    submitting.value = false
  }
}

onMounted(loadSeniors)
</script>

<template>
  <div class="mx-auto max-w-2xl space-y-8">
    <header>
      <h1 class="text-2xl font-semibold text-slate-900">Seniorzy</h1>
      <p class="mt-1 text-slate-600">Lista mieszkańców domu seniora.</p>
    </header>

    <p v-if="error" class="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-800">
      {{ error }}
    </p>

    <form class="space-y-4 rounded-xl border border-slate-200 bg-white p-6 shadow-sm" @submit.prevent="handleSubmit">
      <h2 class="text-lg font-medium text-slate-900">Dodaj seniora</h2>

      <div class="grid gap-4 sm:grid-cols-2">
        <label class="block">
          <span class="text-sm font-medium text-slate-700">Imię</span>
          <input
            v-model="form.firstName"
            required
            class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
            type="text"
          />
        </label>

        <label class="block">
          <span class="text-sm font-medium text-slate-700">Nazwisko</span>
          <input
            v-model="form.lastName"
            required
            class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
            type="text"
          />
        </label>
      </div>

      <label class="block">
        <span class="text-sm font-medium text-slate-700">Numer pokoju (opcjonalnie)</span>
        <input
          v-model="form.roomNumber"
          class="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
          type="text"
        />
      </label>

      <button
        type="submit"
        :disabled="submitting"
        class="rounded-lg bg-emerald-600 px-4 py-2 font-medium text-white hover:bg-emerald-700 disabled:opacity-50"
      >
        {{ submitting ? 'Zapisywanie…' : 'Dodaj' }}
      </button>
    </form>

    <section>
      <h2 class="mb-3 text-lg font-medium text-slate-900">Lista</h2>

      <p v-if="loading" class="text-slate-600">Ładowanie…</p>

      <p v-else-if="seniors.length === 0" class="text-slate-600">Brak seniorów.</p>

      <ul v-else class="divide-y divide-slate-200 rounded-xl border border-slate-200 bg-white">
        <li
          v-for="senior in seniors"
          :key="senior.id"
          class="flex items-center justify-between px-4 py-3"
        >
          <span class="font-medium text-slate-900">
            {{ senior.firstName }} {{ senior.lastName }}
          </span>
          <span v-if="senior.roomNumber" class="text-sm text-slate-500">
            pokój {{ senior.roomNumber }}
          </span>
        </li>
      </ul>
    </section>
  </div>
</template>
