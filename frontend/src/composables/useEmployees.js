import { computed, onMounted, ref } from 'vue'
import {
  assignEmployeeToRoom as assignEmployeeToRoomRequest,
  assignEmployeesToRoom as assignEmployeesToRoomRequest,
  createEmployee,
  fetchEmployees,
} from '@/api/employees'
import { fetchPositions } from '@/api/positions'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import {
  buildHomeNameMap,
  mapEmployeeFromApi,
  mapEmployeeFromMock,
} from '@/api/mappers/employeeMapper'
import { mapPositionFromApi, mapPositionFromMock } from '@/api/mappers/positionMapper'
import { mapSeniorHomeFromApi, mapSeniorHomeFromMock } from '@/api/mappers/seniorHomeMapper'
import { apiEnabled } from '@/composables/apiMode'
import { useHomeScope } from '@/composables/useHomeScope'
import {
  employees as mockEmployees,
  positions as mockPositions,
  seniorHomes as mockSeniorHomes,
} from '@/mock/data'

function loadMockHomes() {
  return mockSeniorHomes.map(mapSeniorHomeFromMock)
}

function loadMockPositions() {
  return mockPositions.map(mapPositionFromMock)
}

function loadMockEmployees(homeNames) {
  return mockEmployees.map((record) => mapEmployeeFromMock(record, homeNames))
}

export function emptyEmployeeForm() {
  return {
    firstName: '',
    lastName: '',
    seniorHomeId: '',
    positionId: '',
    managerId: '',
    salary: 0,
    phone: '',
    email: '',
    birthDate: '',
    hiredAt: '',
    terminatedAt: '',
    city: '',
    street: '',
    number: '',
    postalCode: '',
    postOfficeCity: '',
    isMedical: 'false',
    licenseNumber: '',
    specialization: '',
  }
}

export function useEmployees() {
  const { filterItems, isScoped, homeId } = useHomeScope()
  const allEmployees = ref([])
  const allSeniorHomes = ref([])
  const allPositions = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const assigning = ref(false)
  const error = ref(null)
  const usingMock = ref(!apiEnabled)

  const employeeList = computed(() =>
    filterItems(allEmployees.value, { homeIdKey: 'seniorHomeId' }),
  )

  const seniorHomes = computed(() => {
    if (!isScoped.value) return allSeniorHomes.value
    return allSeniorHomes.value.filter((home) => Number(home.id) === Number(homeId.value))
  })

  const positions = computed(() => allPositions.value)

  function applyMockData() {
    const homes = loadMockHomes()
    const homeNames = buildHomeNameMap(homes)
    allSeniorHomes.value = homes
    allPositions.value = loadMockPositions()
    allEmployees.value = loadMockEmployees(homeNames)
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
      const [homes, positionsResult, employees] = await Promise.all([
        fetchSeniorHomes(),
        fetchPositions(),
        fetchEmployees(),
      ])
      allSeniorHomes.value = homes.map(mapSeniorHomeFromApi)
      allPositions.value = positionsResult.length
        ? positionsResult.map(mapPositionFromApi)
        : loadMockPositions()
      const homeNames = buildHomeNameMap(allSeniorHomes.value)
      allEmployees.value = employees.map((dto) => mapEmployeeFromApi(dto, homeNames))
      usingMock.value = false
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
      applyMockData()
    } finally {
      loading.value = false
    }
  }

  async function addEmployee(form) {
    if (usingMock.value) {
      const home = seniorHomes.value.find((h) => String(h.id) === String(form.seniorHomeId))
      const position = positions.value.find((p) => String(p.id) === String(form.positionId))
      allEmployees.value.push(
        mapEmployeeFromMock({
          ...form,
          id: Math.max(0, ...allEmployees.value.map((employee) => Number(employee.id) || 0)) + 1,
          seniorHome: home?.name ?? '—',
          position: position?.name ?? '—',
          isMedical: form.isMedical === true || form.isMedical === 'true',
        }),
      )
      return { savedLocally: true }
    }

    saving.value = true
    error.value = null
    try {
      await createEmployee(form)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać pracownika'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function assignEmployeeToRoom(employeeId, roomId) {
    if (usingMock.value) {
      const employee = allEmployees.value.find((item) => String(item.id) === String(employeeId))
      if (!employee) throw new Error('Nie znaleziono pracownika')
      const roomIds = new Set(employee.roomIds ?? [])
      roomIds.add(Number(roomId))
      employee.roomIds = [...roomIds]
      return { savedLocally: true }
    }

    assigning.value = true
    error.value = null
    try {
      await assignEmployeeToRoomRequest(employeeId, roomId)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się przypisać pracownika do pomieszczenia'
      throw e
    } finally {
      assigning.value = false
    }
  }

  async function assignEmployeesToRoom(employeeIds, roomId) {
    if (usingMock.value) {
      const selectedIds = new Set(employeeIds.map((id) => String(id)))
      allEmployees.value.forEach((employee) => {
        if (!selectedIds.has(String(employee.id))) return
        const roomIds = new Set(employee.roomIds ?? [])
        roomIds.add(Number(roomId))
        employee.roomIds = [...roomIds]
      })
      return { savedLocally: true }
    }

    assigning.value = true
    error.value = null
    try {
      await assignEmployeesToRoomRequest(employeeIds, roomId)
      await reload()
      return { savedLocally: false }
    } catch (e) {
      error.value = e.message ?? 'Nie udało się przypisać pracowników do pomieszczenia'
      throw e
    } finally {
      assigning.value = false
    }
  }

  onMounted(reload)

  return {
    employeeList,
    seniorHomes,
    positions,
    loading,
    saving,
    assigning,
    error,
    usingMock,
    reload,
    addEmployee,
    assignEmployeeToRoom,
    assignEmployeesToRoom,
  }
}
