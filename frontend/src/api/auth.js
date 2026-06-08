import { apiGet, apiPost } from './request'

export function login(credentials) {
  return apiPost('/api/auth/login', credentials)
}

export function getCurrentUser() {
  return apiGet('/api/auth/me')
}
