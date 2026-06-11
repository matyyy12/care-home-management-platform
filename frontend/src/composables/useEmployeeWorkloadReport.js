import { onMounted, ref } from 'vue'
import { fetchEmployeeWorkloadReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { fetchPositions } from '@/api/positions'
import { mapSeniorHomeFromApi } from '@/api/mappers/seniorHomeMapper'
import { mapPositionFromApi } from '@/api/mappers/positionMapper'
import { mapEmployeeWorkloadFromApi } from '@/api/mappers/reportMapper'

export function useEmployeeWorkloadReport() {
  const rows = ref([])
  const seniorHomes = ref([])
  const positions = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function reload(filters = {}) {
    loading.value = true
    error.value = null

    try {
      const [homes, positionList, report] = await Promise.all([
        fetchSeniorHomes(),
        fetchPositions(),
        fetchEmployeeWorkloadReport(filters),
      ])
      seniorHomes.value = homes.map(mapSeniorHomeFromApi)
      positions.value = positionList.map(mapPositionFromApi)
      rows.value = report.map(mapEmployeeWorkloadFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu obciążenia pracowników'
    } finally {
      loading.value = false
    }
  }

  onMounted(() => reload())

  return {
    rows,
    seniorHomes,
    positions,
    loading,
    error,
    reload,
  }
}
