import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

export function useHomeScope() {
  const auth = useAuthStore()

  const isScoped = computed(() => auth.isHomeScoped)
  const homeId = computed(() => auth.assignedSeniorHomeId)
  const homeName = computed(() => auth.assignedHomeName)

  function filterItems(items, { homeIdKey, homeNameKey = 'seniorHome' } = {}) {
    if (!isScoped.value) return items

    const scopedId = homeId.value
    const scopedName = homeName.value

    return items.filter((item) => {
      if (homeIdKey && item[homeIdKey] != null) {
        return Number(item[homeIdKey]) === Number(scopedId)
      }
      if (homeNameKey && item[homeNameKey] != null) {
        return item[homeNameKey] === scopedName
      }
      return true
    })
  }

  function scopedHomeSelectOptions(allHomes) {
    const options = allHomes.map((home) =>
      typeof home === 'object'
        ? { value: home.id, label: home.name }
        : { value: home.value, label: home.label },
    )

    if (!isScoped.value) return options

    return options.filter((option) => Number(option.value) === Number(homeId.value))
  }

  return {
    isScoped,
    homeId,
    homeName,
    filterItems,
    scopedHomeSelectOptions,
  }
}
