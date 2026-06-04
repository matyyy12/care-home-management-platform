import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUser, login as apiLogin } from '@/api/auth'
import { setAuthTokenProvider, setUnauthorizedHandler } from '@/api/request'
import {
  ROLE_LABELS,
  canRoleReadRoute,
  canRoleWriteRoute,
  isHomeScopedRole,
} from '@/constants/roles'
import { seniorHomes as mockSeniorHomes } from '@/mock/data'

const TOKEN_STORAGE_KEY = 'seniorhouse-app-token'
const LEGACY_USER_STORAGE_KEY = 'seniorhouse-app-user'

function loadStoredToken() {
  try {
    localStorage.removeItem(LEGACY_USER_STORAGE_KEY)
    return localStorage.getItem(TOKEN_STORAGE_KEY)
  } catch {
    return null
  }
}

function persistToken(nextToken) {
  try {
    if (nextToken) localStorage.setItem(TOKEN_STORAGE_KEY, nextToken)
    else localStorage.removeItem(TOKEN_STORAGE_KEY)
  } catch {
    /* ignore */
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(loadStoredToken())
  const user = ref(null)
  const isLoading = ref(false)

  setAuthTokenProvider(() => token.value)
  setUnauthorizedHandler(() => clearSession())

  const isAuthenticated = computed(() => Boolean(token.value))
  const email = computed(() => user.value?.email ?? '')
  const role = computed(() => user.value?.role ?? null)
  const assignedSeniorHomeId = computed(() => user.value?.assignedSeniorHomeId ?? null)
  const roleLabel = computed(() => (role.value ? ROLE_LABELS[role.value] ?? role.value : ''))

  const isHomeScoped = computed(() => isHomeScopedRole(role.value))

  const assignedHome = computed(
    () => mockSeniorHomes.find((home) => home.id === assignedSeniorHomeId.value) ?? null,
  )

  const assignedHomeName = computed(() => assignedHome.value?.name ?? '')

  const seniorHomeOptions = computed(() =>
    mockSeniorHomes.map((home) => ({ value: home.id, label: home.name })),
  )

  function setSession(nextToken, nextUser) {
    token.value = nextToken
    user.value = nextUser
    persistToken(nextToken)
  }

  function clearSession() {
    setSession(null, null)
  }

  async function login(credentials) {
    const response = await apiLogin(credentials)
    setSession(response.token, response.user)
    return response.user
  }

  async function loadCurrentUser() {
    if (!token.value) return null

    isLoading.value = true
    try {
      const currentUser = await getCurrentUser()
      user.value = currentUser
      return currentUser
    } catch (error) {
      clearSession()
      throw error
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    clearSession()
  }

  function canRead(routeName) {
    if (!role.value) return false
    return canRoleReadRoute(role.value, routeName)
  }

  function canWrite(routeName) {
    if (!role.value) return false
    return canRoleWriteRoute(role.value, routeName)
  }

  return {
    token,
    user,
    email,
    role,
    roleLabel,
    assignedSeniorHomeId,
    assignedHome,
    assignedHomeName,
    isHomeScoped,
    isAuthenticated,
    isLoading,
    seniorHomeOptions,
    login,
    loadCurrentUser,
    logout,
    clearSession,
    canRead,
    canWrite,
  }
})
