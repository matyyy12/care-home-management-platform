import { apiDelete, apiGet, apiPost } from '@/api/request'

export async function fetchSeniorRoomAssignments(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null) query.set('seniorHomeId', String(params.seniorHomeId))
  const qs = query.toString()
  return apiGet(`/api/assignments/senior-rooms${qs ? `?${qs}` : ''}`)
}

export async function assignSeniorToRoom(seniorId, roomId) {
  return apiPost('/api/assignments/senior-rooms', { seniorId: Number(seniorId), roomId: Number(roomId) })
}

export async function unassignSeniorFromRoom(seniorId) {
  return apiDelete(`/api/assignments/senior-rooms/${seniorId}`)
}
