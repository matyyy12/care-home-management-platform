import { ref } from 'vue'

function cloneData(data) {
  return JSON.parse(JSON.stringify(data))
}

export function useLocalCollection(initialData) {
  const items = ref(cloneData(initialData))

  function nextId() {
    const ids = items.value.map((i) => Number(i.id) || 0)
    return Math.max(0, ...ids) + 1
  }

  function add(record) {
    items.value.push({ ...record, id: nextId() })
  }

  function reset() {
    items.value = cloneData(initialData)
  }

  return { items, add, reset }
}
