import { onMounted, ref } from 'vue'
import { createEquipment, deleteEquipment, fetchEquipment, updateEquipment } from '@/api/equipment'
import { mapEquipmentFromApi } from '@/api/mappers/equipmentMapper'

export function useEquipment() {
  const equipmentList = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const equipment = await fetchEquipment()
      equipmentList.value = equipment.map(mapEquipmentFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addEquipment(form) {
    saving.value = true
    error.value = null

    try {
      await createEquipment(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać sprzętu'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editEquipment(id, form) {
    saving.value = true
    error.value = null

    try {
      await updateEquipment(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zaktualizować sprzętu'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeEquipment(id) {
    saving.value = true
    error.value = null

    try {
      await deleteEquipment(id)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć sprzętu'
      throw e
    } finally {
      saving.value = false
    }
  }

  onMounted(reload)

  return {
    equipmentList,
    loading,
    saving,
    error,
    reload,
    addEquipment,
    editEquipment,
    removeEquipment,
  }
}
