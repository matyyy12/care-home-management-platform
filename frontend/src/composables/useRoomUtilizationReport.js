import { onMounted, ref } from 'vue'
import { fetchRoomUtilizationReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapRoomUtilizationFromApi } from '@/api/mappers/reportMapper'
import { buildHomeNameMap } from '@/api/mappers/seniorMapper'

export function useRoomUtilizationReport() {
  const rows = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function reload(filters = {}) {
    loading.value = true
    error.value = null

    try {
      const homes = await fetchSeniorHomes()
      const homeNames = buildHomeNameMap(homes.map((h) => ({ id: h.id, name: h.name })))
      const report = await fetchRoomUtilizationReport(filters)
      rows.value = report.map((row) => mapRoomUtilizationFromApi(row, homeNames))
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu'
    } finally {
      loading.value = false
    }
  }

  onMounted(() => reload())

  return {
    rows,
    loading,
    error,
    reload,
  }
}
