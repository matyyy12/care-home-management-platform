import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import { mapEmployeeToCreateRequest, mapEmployeeToUpdateRequest } from '@/api/mappers/employeeMapper'

export async function fetchEmployees(params = {}) {
  const query = new URLSearchParams()
  if (params.id != null) query.set('id', String(params.id))
  const qs = query.toString()
  return apiGet(`/api/employees${qs ? `?${qs}` : ''}`)
}

export async function createEmployee(form) {
  return apiPost('/api/employees', mapEmployeeToCreateRequest(form))
}

export async function updateEmployee(id, form) {
  return apiPatch(`/api/employees/${id}`, mapEmployeeToUpdateRequest(form))
}

export async function assignEmployeeToRoom(employeeId, roomId) {
  return apiPatch(`/api/employees/${employeeId}/rooms/${roomId}`)
}

export async function assignEmployeesToRoom(employeeIds, roomId) {
  return apiPatch(`/api/employees/rooms/${roomId}`, { employeeIds })
}

export async function deleteEmployee(id) {
  return apiDelete(`/api/employees/${id}`)
}
