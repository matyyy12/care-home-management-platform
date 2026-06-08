import { computed, onMounted, ref } from 'vue'
import {
  assignEmployeeToRoom as assignEmployeeToRoomRequest,
  assignEmployeesToRoom as assignEmployeesToRoomRequest,
  createEmployee,
  fetchEmployees,
} from '@/api/employees'
import { fetchPositions } from '@/api/positions'
import { fetchSeniorHomes } from '@/api/seniorHomes'
import { buildHomeNameMap, mapEmployeeFromApi } from '@/api/mappers/employeeMapper'
import { mapPositionFromApi } from '@/api/mappers/positionMapper'
import { mapSeniorHomeFromApi } from '@/api/mappers/seniorHomeMapper'
import { useHomeScope } from '@/composables/useHomeScope'

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

  const employeeList = computed(() =>
    filterItems(allEmployees.value, { homeIdKey: 'seniorHomeId' }),
  )

  const seniorHomes = computed(() => {
    if (!isScoped.value) return allSeniorHomes.value
    return allSeniorHomes.value.filter((home) => Number(home.id) === Number(homeId.value))
  })

  const positions = computed(() => allPositions.value)

  async function reload() {
    loading.value = true
    error.value = null

    try {
      const [homes, positionsResult, employees] = await Promise.all([
        fetchSeniorHomes(),
        fetchPositions(),
        fetchEmployees(),
      ])
      allSeniorHomes.value = homes.map(mapSeniorHomeFromApi)
      allPositions.value = positionsResult.map(mapPositionFromApi)
      const homeNames = buildHomeNameMap(allSeniorHomes.value)
      allEmployees.value = employees.map((dto) => mapEmployeeFromApi(dto, homeNames))
    } catch (e) {
      error.value = e.message ?? 'Nie udało się połączyć z API'
    } finally {
      loading.value = false
    }
  }

  async function addEmployee(form) {
    saving.value = true
    error.value = null
    try {
      await createEmployee(form)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się zapisać pracownika'
      throw e
    } finally {
      saving.value = false
    }
  }

  async function assignEmployeeToRoom(employeeId, roomId) {
    assigning.value = true
    error.value = null
    try {
      await assignEmployeeToRoomRequest(employeeId, roomId)
      await reload()
    } catch (e) {
      error.value = e.message ?? 'Nie udało się przypisać pracownika do pomieszczenia'
      throw e
    } finally {
      assigning.value = false
    }
  }

  async function assignEmployeesToRoom(employeeIds, roomId) {
    assigning.value = true
    error.value = null
    try {
      await assignEmployeesToRoomRequest(employeeIds, roomId)
      await reload()
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
    reload,
    addEmployee,
    assignEmployeeToRoom,
    assignEmployeesToRoom,
  }
}
