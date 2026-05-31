export const apiEnabled = import.meta.env.VITE_USE_API !== 'false'

export function sourceNotice(usingMock) {
  if (!apiEnabled) {
    return 'API wyłączone (VITE_USE_API=false) — wyświetlamy dane demonstracyjne.'
  }
  if (usingMock) {
    return 'Brak połączenia z backendem — wyświetlamy dane demonstracyjne.'
  }
  return ''
}
