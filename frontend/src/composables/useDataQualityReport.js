import { onMounted, ref } from 'vue'
import { fetchDataQualityReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapSeniorHomeFromApi } from '@/api/mappers/seniorHomeMapper'
import { mapDataQualityIssueFromApi } from '@/api/mappers/reportMapper'

export function useDataQualityReport() {
  const rows = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function reload(filters = {}) {
    loading.value = true
    error.value = null

    try {
      const [homes, report] = await Promise.all([
        fetchSeniorHomes(),
        fetchDataQualityReport(filters),
      ])
      seniorHomes.value = homes.map(mapSeniorHomeFromApi)
      rows.value = report.map(mapDataQualityIssueFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu jakości danych'
    } finally {
      loading.value = false
    }
  }

  onMounted(() => reload())

  return {
    rows,
    seniorHomes,
    loading,
    error,
    reload,
  }
}
