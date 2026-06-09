import { onMounted, ref } from 'vue'
import { fetchSeniorActivityReport } from '@/api/reports'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapSeniorActivityFromApi } from '@/api/mappers/reportMapper'

export function useSeniorActivityReport() {
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
        fetchSeniorActivityReport(filters),
      ])
      seniorHomes.value = homes
      rows.value = report.map(mapSeniorActivityFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać raportu aktywności seniorów'
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
