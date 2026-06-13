/** Role aplikacyjne (§2 PDF) — nie mylić ze słownikiem stanowisk pracownika. */
export const ROLES = {
  DIRECTOR: 'DIRECTOR',
  MANAGER: 'MANAGER',
  EMPLOYEE: 'EMPLOYEE',
  MEDICAL: 'MEDICAL',
  ADMIN_TECH: 'ADMIN_TECH',
}

export const ROLE_OPTIONS = [
  { value: ROLES.DIRECTOR, label: 'Dyrektor' },
  { value: ROLES.MANAGER, label: 'Menadżer' },
  { value: ROLES.EMPLOYEE, label: 'Pracownik' },
  { value: ROLES.MEDICAL, label: 'Pracownik medyczny' },
  { value: ROLES.ADMIN_TECH, label: 'Administrator techniczny' },
]

export const ROLE_LABELS = Object.fromEntries(ROLE_OPTIONS.map((r) => [r.value, r.label]))

/** Role przypisane do jednego domu seniora (§7 PDF). */
export const HOME_SCOPED_ROLES = [ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.MEDICAL]

export function isHomeScopedRole(role) {
  return HOME_SCOPED_ROLES.includes(role)
}

/** Odczyt trasy (nazwa z routera). */
export const ROUTE_READ_ACCESS = {
  dashboard: Object.values(ROLES),
  'senior-homes': [ROLES.DIRECTOR],
  addresses: [ROLES.DIRECTOR],
  seniors: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.MEDICAL],
  employees: [ROLES.DIRECTOR, ROLES.MANAGER],
  positions: [ROLES.DIRECTOR],
  rooms: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.EMPLOYEE],
  activities: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.MEDICAL],
  equipment: [ROLES.DIRECTOR],
  assignments: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.MEDICAL],
  reports: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.MEDICAL],
  'report-room-occupancy': [ROLES.DIRECTOR, ROLES.MANAGER],
  'report-senior-activity': [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.MEDICAL],
  'report-employee-workload': [ROLES.DIRECTOR, ROLES.MANAGER],
  'report-data-quality': [ROLES.DIRECTOR],
  'report-room-utilization': [ROLES.DIRECTOR, ROLES.MANAGER],
  'report-medical-activity': [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.MEDICAL],
  administration: [ROLES.DIRECTOR, ROLES.ADMIN_TECH],
  forbidden: Object.values(ROLES),
}

/** Zapis / formularze w module. */
export const ROUTE_WRITE_ACCESS = {
  'senior-homes': [ROLES.DIRECTOR],
  addresses: [ROLES.DIRECTOR],
  seniors: [ROLES.DIRECTOR, ROLES.MANAGER],
  employees: [ROLES.DIRECTOR, ROLES.MANAGER],
  positions: [ROLES.DIRECTOR],
  rooms: [ROLES.DIRECTOR, ROLES.MANAGER],
  activities: [ROLES.DIRECTOR, ROLES.MANAGER],
  equipment: [ROLES.DIRECTOR],
  assignments: [ROLES.DIRECTOR, ROLES.MANAGER, ROLES.EMPLOYEE, ROLES.MEDICAL],
  administration: [ROLES.ADMIN_TECH],
}

export function canRoleReadRoute(role, routeName) {
  const allowed = ROUTE_READ_ACCESS[routeName]
  return allowed ? allowed.includes(role) : false
}

export function canRoleWriteRoute(role, routeName) {
  const allowed = ROUTE_WRITE_ACCESS[routeName]
  return allowed ? allowed.includes(role) : false
}
