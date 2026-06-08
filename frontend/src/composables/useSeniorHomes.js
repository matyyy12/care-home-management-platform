import { onMounted, ref } from 'vue'
import {
  createSeniorHome,
  deleteSeniorHome,
  fetchSeniorHomes,
  updateSeniorHome,
} from '@/api/seniorHomes'
import { mapSeniorHomeFromApi } from '@/api/mappers/seniorHomeMapper'

export function useSeniorHomes() {
  const homeList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const homes = await fetchSeniorHomes()
      homeList.value = homes.map(mapSeniorHomeFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addHome(form) {
    saving.value = true
    error.value = null
    try {
      await createSeniorHome(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać domu seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editHome(id, form) {
    saving.value = true
    error.value = null
    try {
      await updateSeniorHome(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować domu seniora'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeHome(id) {
    saving.value = true
    error.value = null
    try {
      await deleteSeniorHome(id)
      await reload()
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
    reload,
    addHome,
    editHome,
    removeHome,
  }
}
