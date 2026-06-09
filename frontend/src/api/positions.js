import { apiGet, apiPatch, apiPost } from '@/api/request'
import { mapPositionToCreateRequest, mapPositionToUpdateRequest } from '@/api/mappers/positionMapper'

export async function fetchPositions() {
  return apiGet('/api/positions')
}

export async function createPosition(form) {
  return apiPost('/api/positions', mapPositionToCreateRequest(form))
}

export async function updatePosition(id, form) {
  return apiPatch(`/api/positions/${id}`, mapPositionToUpdateRequest(form))
}
