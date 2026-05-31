import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  ROLES,
  ROLE_LABELS,
  canRoleReadRoute,
  canRoleWriteRoute,
} from '@/constants/roles'

const STORAGE_KEY = 'seniorhouse-app-role'

function loadStoredRole() {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored && Object.values(ROLES).includes(stored)) return stored
  } catch {
    /* localStorage niedostępny */
  }
  return ROLES.MANAGER
}

export const useAuthStore = defineStore('auth', () => {
  const role = ref(loadStoredRole())

  const roleLabel = computed(() => ROLE_LABELS[role.value] ?? role.value)

  function setRole(newRole) {
    role.value = newRole
    try {
      localStorage.setItem(STORAGE_KEY, newRole)
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

  return { role, roleLabel, setRole, canRead, canWrite }
})
