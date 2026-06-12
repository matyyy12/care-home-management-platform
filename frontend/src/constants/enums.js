export const ACTIVITY_TYPES = [
  { value: 'MEDICAL', label: 'Medyczne' },
  { value: 'RECREATIONAL', label: 'Rekreacyjne' },
  { value: 'CULTURAL', label: 'Kulturalne' },
]

export const ROOM_TYPES = [
  { value: 'RESIDENTIAL', label: 'Pokój mieszkalny' },
  { value: 'OFFICE', label: 'Gabinet' },
  { value: 'GAME_ROOM', label: 'Sala gier' },
]

export const ROOM_STANDARDS = [
  { value: 'BASIC', label: 'Podstawowy' },
  { value: 'COMFORT', label: 'Komfort' },
  { value: 'PREMIUM', label: 'Premium' },
]

export const ROOM_STANDARD_LABELS = Object.fromEntries(
  ROOM_STANDARDS.map((option) => [option.value, option.label]),
)

export const GAME_ROOM_EQUIPMENT_TYPES = [
  { value: 'TV', label: 'Telewizor' },
  { value: 'RADIO', label: 'Radio' },
  { value: 'COMPUTER', label: 'Komputer' },
  { value: 'CROSSWORD', label: 'Krzyżówki' },
  { value: 'BOARD_GAME', label: 'Gry planszowe' },
]

export const GENDER_OPTIONS = [
  { value: 'F', label: 'Kobieta' },
  { value: 'M', label: 'Mężczyzna' },
]

export const GENDER_LABELS = Object.fromEntries(GENDER_OPTIONS.map((option) => [option.value, option.label]))
export const ACTIVITY_TYPE_LABELS = Object.fromEntries(
  ACTIVITY_TYPES.map((option) => [option.value, option.label]),
)

export function formatCurrency(value) {
  return new Intl.NumberFormat('pl-PL', {
    style: 'currency',
    currency: 'PLN',
    minimumFractionDigits: 2,
  }).format(Number(value) || 0)
}

export const SENIOR_STATUS_LABELS = {
  ACTIVE: 'Aktywny',
  DECEASED: 'Zmarły',
  DISCHARGED: 'Wypisany',
}

export const EMPLOYMENT_STATUS_LABELS = {
  ACTIVE: 'Aktywny',
  TERMINATED: 'Zwolniony',
}
