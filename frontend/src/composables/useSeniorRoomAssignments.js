import { onMounted, ref } from 'vue'
import {
  assignSeniorToRoom,
  fetchSeniorRoomAssignments,
  unassignSeniorFromRoom,
} from '@/api/assignments'
import { mapSeniorRoomAssignmentFromApi } from '@/api/mappers/assignmentMapper'
import { apiEnabled } from '@/composables/apiMode'

export function useSeniorRoomAssignments() {
  const assignments = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const deletingSeniorId = ref(null)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  async function reload(seniorHomeId = null) {
    loading.value = true
    error.value = null

    if (!apiEnabled) {
      assignments.value = []
      usingMock.value = true
      loading.value = false
      return
    }

    try {
      const rows = await fetchSeniorRoomAssignments(
        seniorHomeId != null ? { seniorHomeId } : {},
      )
      assignments.value = rows.map(mapSeniorRoomAssignmentFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się pobrać przypisań'
      assignments.value = []
      usingMock.value = true
    } finally {
      loading.value = false
    }
  }

  async function assign(seniorId, roomId) {
    saving.value = true
    error.value = null
    try {
      await assignSeniorToRoom(seniorId, roomId)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się przypisać seniora do pokoju'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function unassign(seniorId) {
    deletingSeniorId.value = seniorId
    error.value = null
    try {
      await unassignSeniorFromRoom(seniorId)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć przypisania'
      throw e
    } finally {
      deletingSeniorId.value = null
    }
  }

  onMounted(() => reload())

  return {
    assignments,
    loading,
    saving,
    deletingSeniorId,
    error,
    usingMock,
    reload,
    assign,
    unassign,
  }
}
