import { computed, onMounted, ref } from 'vue'
import { createRoom, deleteRoom, fetchRooms, updateRoom } from '@/api/rooms'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapRoomFromApi } from '@/api/mappers/roomMapper'

export function emptyRoomForm() {
  return {
    roomNumber: '',
    floor: 0,
    inUse: 'true',
    seniorHomeId: '',
    type: '',
    capacity: 2,
    standard: 'BASIC',
    officeKind: '',
    equipmentDescription: '',
    gameEquipment: [],
  }
}

export function useRooms() {
  const roomList = ref([])
  const seniorHomes = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const deletingId = ref(null)
  const error = ref(null)
  const filterHomeId = ref('')

  const homeOptions = computed(() =>
    seniorHomes.value.map((home) => ({ value: home.id, label: home.name })),
  )

  const filteredRooms = computed(() => {
    if (!filterHomeId.value) {
      return roomList.value
    }
    const home = seniorHomes.value.find((h) => String(h.id) === String(filterHomeId.value))
    if (!home) {
      return roomList.value
    }
    return roomList.value.filter((row) => row.seniorHome === home.name)
  })

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const homes = await fetchSeniorHomes()
      seniorHomes.value = homes.map((home) => ({ id: home.id, name: home.name }))
      const rooms = await fetchRooms()
      roomList.value = rooms.map(mapRoomFromApi)
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  function seniorHomeIdForRow(row) {
    if (row.seniorHomeId != null) {
      return row.seniorHomeId
    }
    const home = seniorHomes.value.find((h) => h.name === row.seniorHome)
    return home?.id ?? ''
  }

  async function addRoom(form) {
    saving.value = true
    error.value = null
    try {
      await createRoom(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać pomieszczenia'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editRoom(id, form) {
    saving.value = true
    error.value = null
    try {
      await updateRoom(id, form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać zmian'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeRoom(id) {
    deletingId.value = id
    error.value = null
    try {
      await deleteRoom(id)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się usunąć pomieszczenia'
      throw e
    } finally {
      deletingId.value = null
    }
  }

  onMounted(reload)

  return {
    roomList,
    filteredRooms,
    seniorHomes,
    homeOptions,
    filterHomeId,
    loading,
    saving,
    deletingId,
    error,
    reload,
    addRoom,
    editRoom,
    removeRoom,
    seniorHomeIdForRow,
  }
}
