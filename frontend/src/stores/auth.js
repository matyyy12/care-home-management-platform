import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  ROLES,
  ROLE_LABELS,
  canRoleReadRoute,
  canRoleWriteRoute,
  isHomeScopedRole,
} from '@/constants/roles'
import { seniorHomes as mockSeniorHomes } from '@/mock/data'

const ROLE_STORAGE_KEY = 'seniorhouse-app-role'
const HOME_STORAGE_KEY = 'seniorhouse-app-assigned-home-id'

function loadStoredRole() {
  try {
    const stored = localStorage.getItem(ROLE_STORAGE_KEY)
    if (stored && Object.values(ROLES).includes(stored)) return stored
  } catch {
    /* localStorage niedostępny */
  }
  return ROLES.MANAGER
}

function loadStoredHomeId() {
  try {
    const stored = localStorage.getItem(HOME_STORAGE_KEY)
    if (stored) {
      const id = Number(stored)
      if (mockSeniorHomes.some((home) => home.id === id)) return id
    }
  } catch {
    /* localStorage niedostępny */
  }
  return mockSeniorHomes[0]?.id ?? null
}

export const useAuthStore = defineStore('auth', () => {
  const role = ref(loadStoredRole())
  const assignedSeniorHomeId = ref(loadStoredHomeId())

  const roleLabel = computed(() => ROLE_LABELS[role.value] ?? role.value)

  const isHomeScoped = computed(() => isHomeScopedRole(role.value))

  const assignedHome = computed(
    () => mockSeniorHomes.find((home) => home.id === assignedSeniorHomeId.value) ?? null,
  )

  const assignedHomeName = computed(() => assignedHome.value?.name ?? '')

  const seniorHomeOptions = computed(() =>
    mockSeniorHomes.map((home) => ({ value: home.id, label: home.name })),
  )

  function setRole(newRole) {
    role.value = newRole
    try {
      localStorage.setItem(ROLE_STORAGE_KEY, newRole)
    } catch {
      /* ignore */
    }
  }

  function setAssignedSeniorHome(id) {
    assignedSeniorHomeId.value = Number(id)
    try {
      localStorage.setItem(HOME_STORAGE_KEY, String(assignedSeniorHomeId.value))
    } catch {
      /* ignore */
    }
  }

  function canRead(routeName) {
    return canRoleReadRoute(role.value, routeName)
  }

  function canWrite(routeName) {
    return canRoleWriteRoute(role.value, routeName)
  }

  return {
    role,
    roleLabel,
    assignedSeniorHomeId,
    assignedHome,
    assignedHomeName,
    isHomeScoped,
    seniorHomeOptions,
    setRole,
    setAssignedSeniorHome,
    canRead,
    canWrite,
  }
})
