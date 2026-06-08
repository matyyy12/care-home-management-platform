import { onMounted, ref } from 'vue'
import { fetchRoomOccupancyReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapRoomOccupancyFromApi, mapRoomOccupancyFromMock } from '@/api/mappers/reportMapper'
import { buildHomeNameMap } from '@/api/mappers/seniorMapper'
import { apiEnabled } from '@/composables/apiMode'
import { roomOccupancyReport as mockReport } from '@/mock/data'

export function useRoomOccupancyReport() {
  const rows = ref([])
  const loading = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  function applyMockData() {
    rows.value = mockReport.map(mapRoomOccupancyFromMock)
    usingMock.value = true
  }

  async function reload(filters = {}) {
    loading.value = true
    error.value = null

    if (!apiEnabled) {
      applyMockData()
      loading.value = false
      return
    }

    try {
      const homes = await fetchSeniorHomes()
      const homeNames = buildHomeNameMap(homes.map((h) => ({ id: h.id, name: h.name })))
      const report = await fetchRoomOccupancyReport(filters)
      rows.value = report.map((row) => mapRoomOccupancyFromApi(row, homeNames))
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  onMounted(() => reload())

  return {
    rows,
    loading,
    error,
    usingMock,
    reload,
  }
}
