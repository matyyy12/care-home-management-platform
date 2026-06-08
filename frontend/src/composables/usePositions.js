import { onMounted, ref } from 'vue'
import { fetchPositions, createPosition } from '@/api/positions'
import { mapPositionFromApi } from '@/api/mappers/positionMapper'

export function usePositions() {
  const positionList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const positions = await fetchPositions()
      positionList.value = positions.map(mapPositionFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addPosition(form) {
    saving.value = true
    error.value = null
    try {
      await createPosition(form)
      await reload()
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
    reload,
    addPosition,
  }
}
