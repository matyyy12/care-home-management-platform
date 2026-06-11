import axios from 'axios'

/**
 * Dev: puste VITE_API_BASE_URL → ścieżki /api przez proxy Vite.
 * Prod: ustaw VITE_API_BASE_URL (np. http://localhost:8080).
 */
const API_BASE = (import.meta.env.VITE_API_BASE_URL ?? '').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(message, { status, body } = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.body = body
  }
}

function errorMessage(status, body) {
  if (body?.message) return body.message
  if (typeof body === 'string') return body
  if (Array.isArray(body?.errors)) return body.errors.join(', ')
  return `Błąd serwera (${status})`
}

const http = axios.create({
  baseURL: API_BASE || undefined,
  headers: {
    Accept: 'application/json',
  },
})

const auth = {
  getToken: () => null,
  onUnauthorized: null,
}

export function configureApiAuth({ getToken, onUnauthorized }) {
  auth.getToken = getToken
  auth.onUnauthorized = onUnauthorized
}

http.interceptors.request.use((config) => {
  const token = auth.getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    if (response.status === 204) return null
    if (response.config.responseType === 'blob') return response.data
    const contentType = response.headers['content-type'] ?? ''
    if (contentType.includes('application/json')) {
      return response.data
    }
    return null
  },
  (error) => {
    if (axios.isAxiosError(error) && error.response) {
      const { status, data: body } = error.response
      if (status === 401) {
        auth.onUnauthorized?.()
      }
      throw new ApiError(errorMessage(status, body), { status, body })
    }
    throw error
  },
)

export async function apiRequest(path, config = {}) {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  return http.request({
    url: normalizedPath,
    ...config,
  })
}

export function apiGet(path) {
  return apiRequest(path)
}

export function apiPost(path, data) {
  return apiRequest(path, {
    method: 'POST',
    data,
  })
}

export function apiPatch(path, data) {
  return apiRequest(path, {
    method: 'PATCH',
    data,
  })
}

export function apiPut(path, data) {
  return apiRequest(path, {
    method: 'PUT',
    data,
  })
}

export function apiDelete(path) {
  return apiRequest(path, {
    method: 'DELETE',
  })
}

export function apiBlob(path) {
  return apiRequest(path, {
    method: 'GET',
    responseType: 'blob',
  })
}
