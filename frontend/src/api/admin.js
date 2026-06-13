import { apiGet, apiPost } from '@/api/request'

export async function fetchAdminHealth() {
  return apiGet('/api/admin/health')
}

export async function fetchAdminConfig() {
  return apiGet('/api/admin/config')
}

export async function createAdminBackup() {
  return apiPost('/api/admin/backup')
}
