import { computed, onMounted, ref } from 'vue'
import {
  createActivity,
  deleteActivity,
  fetchAllActivities,
  updateActivity,
} from '@/api/activities'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapActivityFromApi, mapActivityFromMock } from '@/api/mappers/activityMapper'
import { apiEnabled } from '@/composables/apiMode'
import { activities as mockActivities, seniorHomes as mockSeniorHomes } from '@/mock/data'

function loadMockHomes() {
  return mockSeniorHomes.map((home) => ({ id: home.id, name: home.name }))
}

function loadMockActivities() {
  return mockActivities.map(mapActivityFromMock)
}

export function useActivities() {
  const activityList = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const deletingId = ref(null)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)
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

  function applyMockData() {
    seniorHomes.value = loadMockHomes()
    activityList.value = loadMockActivities()
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
      seniorHomes.value = homes.map((home) => ({ id: home.id, name: home.name }))
      const activities = await fetchAllActivities(seniorHomes.value)
      activityList.value = activities.map(mapActivityFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addActivity(form) {
    if (usingMock.value) {
      const home = seniorHomes.value.find((h) => String(h.id) === String(form.seniorHomeId))
      const nextId = Math.max(0, ...activityList.value.map((a) => Number(a.id) || 0)) + 1
      activityList.value.push(
        mapActivityFromMock({
          id: nextId,
          name: form.name,
          type: form.type,
          price: Number(form.price),
          seniorHome: home?.name ?? '—',
          description: form.description,
          instructors: 0,
          participants: 0,
        }),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createActivity(form)
      await reload()
      return { savedLocally: false }
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
    if (usingMock.value) {
      const home = seniorHomes.value.find((h) => String(h.id) === String(form.seniorHomeId))
      const index = activityList.value.findIndex((a) => String(a.id) === String(id))
      if (index === -1) {
        throw new Error('Nie znaleziono zajęć')
      }
      const current = activityList.value[index]
      activityList.value[index] = mapActivityFromMock({
        id: current.id,
        name: form.name,
        type: form.type,
        price: Number(form.price),
        seniorHome: home?.name ?? current.seniorHome,
        description: form.description,
        instructors: current.instructors,
        participants: current.participants,
      })
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await updateActivity(id, form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać zmian'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeActivity(id) {
    if (usingMock.value) {
      activityList.value = activityList.value.filter((a) => String(a.id) !== String(id))
      return { savedLocally: true }
    }

    deletingId.value = id
    error.value = null
    try {
      await deleteActivity(id)
      await reload()
      return { savedLocally: false }
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
    usingMock,
    reload,
    addActivity,
    editActivity,
    removeActivity,
    seniorHomeIdForRow,
  }
}
