import { apiGet } from '@/api/request'

export async function fetchRoomOccupancyReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null) query.set('seniorHomeId', String(params.seniorHomeId))
  if (params.floor != null && params.floor !== '') query.set('floor', String(params.floor))
  if (params.standard?.trim()) query.set('standard', params.standard.trim())
  const qs = query.toString()
  return apiGet(`/api/reports/room-occupancy${qs ? `?${qs}` : ''}`)
}

export async function fetchSeniorActivityReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null && params.seniorHomeId !== '') {
    query.set('seniorHomeId', String(params.seniorHomeId))
  }
  if (params.status?.trim()) query.set('status', params.status.trim())
  const qs = query.toString()
  return apiGet(`/api/reports/senior-activity${qs ? `?${qs}` : ''}`)
}
