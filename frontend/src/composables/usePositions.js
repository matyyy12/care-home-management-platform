import { onMounted, ref } from 'vue'
import { fetchPositions, createPosition } from '@/api/positions'
import { mapPositionFromApi, mapPositionFromMock } from '@/api/mappers/positionMapper'
import { apiEnabled } from '@/composables/apiMode'
import { positions as mockPositions } from '@/mock/data'

export function usePositions() {
  const positionList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  function applyMockData() {
    positionList.value = mockPositions.map(mapPositionFromMock)
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
      const positions = await fetchPositions()
      positionList.value = positions.map(mapPositionFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addPosition(form) {
    if (usingMock.value) {
      const nextId = Math.max(0, ...positionList.value.map((p) => Number(p.id) || 0)) + 1
      positionList.value.push(mapPositionFromMock({ id: nextId, ...form }))
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createPosition(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać stanowiska'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    positionList,
    loading,
    saving,
    error,
    usingMock,
    reload,
    addPosition,
  }
}
