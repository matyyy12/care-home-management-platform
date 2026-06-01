import { onMounted, ref } from 'vue'
import {
  createSeniorHome,
  deleteSeniorHome,
  fetchSeniorHomes,
  updateSeniorHome,
} from '@/api/seniorHomes'
import { mapSeniorHomeFromApi, mapSeniorHomeFromMock } from '@/api/mappers/seniorHomeMapper'
import { apiEnabled } from '@/composables/apiMode'
import { seniorHomes as mockSeniorHomes } from '@/mock/data'

export function useSeniorHomes() {
  const homeList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  function applyMockData() {
    homeList.value = mockSeniorHomes.map(mapSeniorHomeFromMock)
    usingMock.value = true
  }

  async function reload() {
    loading.value = true
    error.value = null

    if (!apiEnabled) {
      applyMockData()
      loading.value = false
      return
    }

    try {
      const homes = await fetchSeniorHomes()
      homeList.value = homes.map(mapSeniorHomeFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addHome(form) {
    if (usingMock.value) {
      const nextId = Math.max(0, ...homeList.value.map((h) => Number(h.id) || 0)) + 1
      homeList.value.push(
        mapSeniorHomeFromMock({
          id: nextId,
          name: form.name,
          phone: form.phone,
          email: form.email,
          website: form.website,
          foundedAt: form.foundedAt || new Date().toISOString().slice(0, 10),
          addressLabel: [form.city, form.street, form.apartmentNumber].filter(Boolean).join(', ') || '—',
          owners: [],
        }),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createSeniorHome(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać domu seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editHome(id, form) {
    if (usingMock.value) {
      const index = homeList.value.findIndex((h) => String(h.id) === String(id))
      if (index !== -1) {
        homeList.value[index] = {
          ...homeList.value[index],
          name: form.name ?? homeList.value[index].name,
          phone: form.phone ?? homeList.value[index].phone,
          email: form.email ?? homeList.value[index].email,
          website: form.website ?? homeList.value[index].website,
        }
      }
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await updateSeniorHome(id, form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować domu seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeHome(id) {
    if (usingMock.value) {
      homeList.value = homeList.value.filter((h) => String(h.id) !== String(id))
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await deleteSeniorHome(id)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć domu seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    homeList,
    loading,
    saving,
    error,
    usingMock,
    reload,
    addHome,
    editHome,
    removeHome,
  }
}
