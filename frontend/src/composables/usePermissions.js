import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function usePermissions(routeName) {
  const auth = useAuthStore()

  const canRead = computed(() => (routeName ? auth.canRead(routeName) : true))
  const canWrite = computed(() => (routeName ? auth.canWrite(routeName) : false))

  return {
    role: computed(() => auth.role),
    roleLabel: computed(() => auth.roleLabel),
    canRead,
    canWrite,
    canReadRoute: auth.canRead,
    canWriteRoute: auth.canWrite,
  }
}
