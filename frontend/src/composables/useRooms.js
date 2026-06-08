import { computed, onMounted, ref } from 'vue'
import { createRoom, deleteRoom, fetchRooms, updateRoom } from '@/api/rooms'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { mapRoomFromApi, mapRoomFromMock } from '@/api/mappers/roomMapper'
import { apiEnabled } from '@/composables/apiMode'
import { rooms as mockRooms, seniorHomes as mockSeniorHomes } from '@/mock/data'

function loadMockHomes() {
  return mockSeniorHomes.map((home) => ({ id: home.id, name: home.name }))
}

function loadMockRooms() {
  return mockRooms.map(mapRoomFromMock)
}

export function emptyRoomForm() {
  return {
    roomNumber: '',
    floor: 0,
    inUse: 'true',
    seniorHomeId: '',
    type: '',
    capacity: 2,
    standard: '',
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
  const usingMock = ref(!apiEnabled)
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

  function applyMockData() {
    seniorHomes.value = loadMockHomes()
    roomList.value = loadMockRooms()
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
      const homes = await fetchSeniorHomes()
      seniorHomes.value = homes.map((home) => ({ id: home.id, name: home.name }))
      const rooms = await fetchRooms()
      roomList.value = rooms.map(mapRoomFromApi)
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
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

  function buildMockRow(form) {
    const home = seniorHomes.value.find((h) => String(h.id) === String(form.seniorHomeId))
    return mapRoomFromMock({
      name: form.roomNumber,
      number: form.roomNumber,
      floor: Number(form.floor),
      seniorHome: home?.name ?? '—',
      seniorHomeId: home?.id ?? null,
      inUse: form.inUse === true || form.inUse === 'true',
      type: form.inUse === true || form.inUse === 'true' ? form.type : null,
      capacity: form.type === 'RESIDENTIAL' ? Number(form.capacity) : null,
      standard: form.standard,
      assignedSeniors: 0,
      officeKind: form.officeKind,
      equipment: form.equipmentDescription,
      gameEquipment: form.gameEquipment,
    })
  }

  async function addRoom(form) {
    if (usingMock.value) {
      const nextId = Math.max(0, ...roomList.value.map((r) => Number(r.id) || 0)) + 1
      roomList.value.push({ ...buildMockRow(form), id: nextId })
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createRoom(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać pomieszczenia'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function editRoom(id, form) {
    if (usingMock.value) {
      const index = roomList.value.findIndex((r) => String(r.id) === String(id))
      if (index === -1) {
        throw new Error('Nie znaleziono pomieszczenia')
      }
      const current = roomList.value[index]
      roomList.value[index] = {
        ...buildMockRow(form),
        id: current.id,
        assignedSeniors: current.assignedSeniors,
      }
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await updateRoom(id, form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać zmian'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function removeRoom(id) {
    if (usingMock.value) {
      roomList.value = roomList.value.filter((r) => String(r.id) !== String(id))
      return { savedLocally: true }
    }

    deletingId.value = id
    error.value = null
    try {
      await deleteRoom(id)
      await reload()
      return { savedLocally: false }
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
    usingMock,
    reload,
    addRoom,
    editRoom,
    removeRoom,
    seniorHomeIdForRow,
  }
}
