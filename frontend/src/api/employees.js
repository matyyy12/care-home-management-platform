import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import { mapEmployeeToCreateRequest } from '@/api/mappers/employeeMapper'

export async function fetchEmployees(params = {}) {
  const query = new URLSearchParams()
  if (params.id != null) query.set('id', String(params.id))
  const qs = query.toString()
  return apiGet(`/api/employees${qs ? `?${qs}` : ''}`)
}

export async function createEmployee(form) {
  return apiPost('/api/employees', mapEmployeeToCreateRequest(form))
}

export async function assignEmployeeToRoom(employeeId, roomId) {
  return apiPatch(`/api/employees/${employeeId}/rooms/${roomId}`)
}

export async function assignEmployeesToRoom(employeeIds, roomId) {
  return apiPatch(`/api/employees/rooms/${roomId}`, { employeeIds })
}

export async function unassignEmployeeFromRoom(employeeId, roomId) {
  return apiDelete(`/api/employees/${employeeId}/rooms/${roomId}`)
}

export async function assignSeniorToEmployee(employeeId, seniorId) {
  return apiPatch(`/api/employees/${employeeId}/seniors/${seniorId}`)
}

export async function unassignSeniorFromEmployee(employeeId, seniorId) {
  return apiDelete(`/api/employees/${employeeId}/seniors/${seniorId}`)
}

export async function setEmployeeSeniors(employeeId, seniorIds) {
  return apiPatch(`/api/employees/${employeeId}/seniors`, {
    seniorIds: seniorIds.map((id) => Number(id)),
  })
}

export async function deleteEmployee(id) {
  return apiDelete(`/api/employees/${id}`)
}
