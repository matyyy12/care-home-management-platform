import { computed, onMounted, ref } from 'vue'
import {
  createActivity,
  deleteActivity,
  fetchAllActivities,
  updateActivity,
} from '@/api/activities'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapActivityFromApi } from '@/api/mappers/activityMapper'

export function useActivities() {
  const activityList = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const deletingId = ref(null)
  const error = ref(null)
  const filterHomeId = ref('')

  const homeOptions = computed(() =>
    seniorHomes.value.map((home) => ({ value: home.id, label: home.name })),
  )

  const filteredActivities = computed(() => {
    if (!filterHomeId.value) {
      return activityList.value
    }
    const home = seniorHomes.value.find((h) => String(h.id) === String(filterHomeId.value))
    if (!home) {
      return activityList.value
    }
    return activityList.value.filter((row) => row.seniorHome === home.name)
  })

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const homes = await fetchSeniorHomes()
      seniorHomes.value = homes.map((home) => ({ id: home.id, name: home.name }))
      const activities = await fetchAllActivities(seniorHomes.value)
      activityList.value = activities.map(mapActivityFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addActivity(form) {
    saving.value = true
    error.value = null
    try {
      await createActivity(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać zajęć'
      throw e
    } finally {
      saving.value = false
    }
  }

  function seniorHomeIdForRow(row) {
    const home = seniorHomes.value.find((h) => h.name === row.seniorHome)
    return home?.id ?? ''
  }

  async function editActivity(id, form) {
    saving.value = true
    error.value = null
    try {
      await updateActivity(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać zmian'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeActivity(id) {
    deletingId.value = id
    error.value = null
    try {
      await deleteActivity(id)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć zajęć'
      throw e
    } finally {
      deletingId.value = null
    }
  }

  onMounted(reload)

  return {
    activityList,
    filteredActivities,
    seniorHomes,
    homeOptions,
    filterHomeId,
    loading,
    saving,
    deletingId,
    error,
    reload,
    addActivity,
    editActivity,
    removeActivity,
    seniorHomeIdForRow,
  }
}
