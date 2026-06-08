import { apiDelete, apiGet, apiPatch, apiPost } from '@/api/request'
import { mapRoomToCreateRequest, mapRoomToUpdateRequest } from '@/api/mappers/roomMapper'

export async function fetchRooms(params = {}) {
  const query = new URLSearchParams()
  if (params.id != null) query.set('id', String(params.id))
  if (params.seniorHomeId != null) query.set('seniorHomeId', String(params.seniorHomeId))
  const qs = query.toString()
  return apiGet(`/api/rooms${qs ? `?${qs}` : ''}`)
}

export async function createRoom(form) {
  return apiPost('/api/rooms', mapRoomToCreateRequest(form))
}

export async function updateRoom(id, form) {
  return apiPatch(`/api/rooms/${id}`, mapRoomToUpdateRequest(form))
}

export async function deleteRoom(id) {
  return apiDelete(`/api/rooms/${id}`)
}
