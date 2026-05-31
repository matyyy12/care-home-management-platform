import { apiGet, apiPost } from '@/api/request'
import { mapPositionToCreateRequest } from '@/api/mappers/positionMapper'

export async function fetchPositions() {
  return apiGet('/api/positions')
}

export async function createPosition(form) {
  return apiPost('/api/positions', mapPositionToCreateRequest(form))
}
