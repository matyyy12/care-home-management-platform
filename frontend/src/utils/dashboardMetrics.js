/** Metryki operacyjne pulpitu — czyste funkcje na danych z API. */

export function computeRoomOccupancy(rows) {
  if (!rows.length) {
    return { percent: 0, capacity: 0, assigned: 0, free: 0 }
  }
  const capacity = rows.reduce((sum, row) => sum + row.capacity, 0)
  const assigned = rows.reduce((sum, row) => sum + row.assigned, 0)
  const free = rows.reduce((sum, row) => sum + row.free, 0)
  const percent = capacity ? Math.round((assigned / capacity) * 100) : 0
  return { percent, capacity, assigned, free }
}

export function computeSeniorCounts(seniorList) {
  const active = seniorList.filter((s) => !s.dischargeDate).length
  const discharged = seniorList.length - active
  return { active, discharged }
}

export function computeQualityCounts(issues) {
  const warnings = issues.filter((i) => i.severity === 'warning').length
  const errors = issues.filter((i) => i.severity === 'error').length
  return { total: issues.length, warnings, errors }
}

function pluralize(count, one, few, many) {
  if (count === 1) return one
  const mod10 = count % 10
  const mod100 = count % 100
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) return few
  return many
}

export function formatQualitySubtitle({ warnings, errors, total }) {
  if (total === 0) return 'Brak wykrytych problemów'
  const parts = []
  if (warnings > 0) {
    parts.push(
      `${warnings} ${pluralize(warnings, 'ostrzeżenie', 'ostrzeżenia', 'ostrzeżeń')}`,
    )
  }
  if (errors > 0) {
    parts.push(`${errors} ${pluralize(errors, 'błąd', 'błędy', 'błędów')}`)
  }
  return parts.join(', ')
}

export function formatProblemsCount(total) {
  return `${total} ${pluralize(total, 'problem', 'problemy', 'problemów')}`
}

export function computeSeniorsPerHome(seniorList, homes) {
  return homes.map((home) => ({
    homeId: home.id,
    homeName: home.name,
    active: seniorList.filter((s) => {
      if (s.dischargeDate) return false
      if (s.seniorHomeId != null) return Number(s.seniorHomeId) === Number(home.id)
      return s.seniorHome === home.name
    }).length,
  }))
}

export function computeActiveEmployees(employeeList) {
  return employeeList.filter((e) => !e.terminatedAt).length
}
