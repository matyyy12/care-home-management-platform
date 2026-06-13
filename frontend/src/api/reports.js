import { apiBlob, apiGet } from '@/api/request'

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

export async function fetchEmployeeWorkloadReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null && params.seniorHomeId !== '') {
    query.set('seniorHomeId', String(params.seniorHomeId))
  }
  if (params.positionId != null && params.positionId !== '') {
    query.set('positionId', String(params.positionId))
  }
  if (params.medicalOnly != null && params.medicalOnly !== '') {
    query.set('medicalOnly', String(params.medicalOnly))
  }
  const qs = query.toString()
  return apiGet(`/api/reports/employee-workload${qs ? `?${qs}` : ''}`)
}

export async function downloadSeniorHomeSummaryPdf(homeId) {
  return apiBlob(`/api/reports/senior-home-summary/${homeId}/pdf`)
}

export async function fetchDataQualityReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null && params.seniorHomeId !== '') {
    query.set('seniorHomeId', String(params.seniorHomeId))
  }
  if (params.severity?.trim()) query.set('severity', params.severity.trim())
  const qs = query.toString()
  return apiGet(`/api/reports/data-quality${qs ? `?${qs}` : ''}`)
}

export async function fetchRoomUtilizationReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null && params.seniorHomeId !== '') {
    query.set('seniorHomeId', String(params.seniorHomeId))
  }
  if (params.inUse != null && params.inUse !== '') {
    query.set('inUse', String(params.inUse))
  }
  if (params.type?.trim()) query.set('type', params.type.trim())
  const qs = query.toString()
  return apiGet(`/api/reports/room-utilization${qs ? `?${qs}` : ''}`)
}

export async function fetchMedicalActivityReport(params = {}) {
  const query = new URLSearchParams()
  if (params.seniorHomeId != null && params.seniorHomeId !== '') {
    query.set('seniorHomeId', String(params.seniorHomeId))
  }
  const qs = query.toString()
  return apiGet(`/api/reports/medical-activities${qs ? `?${qs}` : ''}`)
}
