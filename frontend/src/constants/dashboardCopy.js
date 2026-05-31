import { ROLES } from '@/constants/roles'

/** Tytuł strony pulpitu — dopasowany do roli, bez tonu „ograniczeń”. */
export const DASHBOARD_TITLES = {
  [ROLES.DIRECTOR]: 'Pulpit dyrektora',
  [ROLES.MANAGER]: 'Pulpit menadżera',
  [ROLES.EMPLOYEE]: 'Pulpit pracownika',
  [ROLES.MEDICAL]: 'Pulpit medyczny',
  [ROLES.ADMIN_TECH]: 'Pulpit administratora',
}

export const DASHBOARD_DESCRIPTIONS = {
  [ROLES.DIRECTOR]:
    'Podsumowanie placówek, ewidencji organizacyjnej i raportów. Kliknij kartę, aby przejść do szczegółów.',
  [ROLES.MANAGER]:
    'Operacyjny przegląd seniorów, zespołu i obłożenia pokoi w Twoim zakresie odpowiedzialności.',
  [ROLES.EMPLOYEE]:
    'Twoje moduły: seniorzy, pomieszczenia, zajęcia i przypisania — najważniejsze liczby na start dnia.',
  [ROLES.MEDICAL]:
    'Przegląd seniorów, zajęć i raportów aktywności w zakresie opieki medycznej.',
  [ROLES.ADMIN_TECH]:
    'Utrzymanie i konfiguracja systemu. Pozostałe sekcje są dostępne w trybie podglądu.',
}

export const DASHBOARD_SECTION_LABELS = {
  stats: 'Wskaźniki operacyjne',
  directorOverview: 'Przegląd organizacji',
}

export const DEMO_FOOTER =
  'Dane demonstracyjne. Po wdrożeniu logowania Twoja rola będzie przypisana do konta — w demo możesz ją zmienić w panelu bocznym.'

const REPORT_ROUTE_PREFIX = 'report-'

/** Etykieta linku na karcie KPI — raport vs ewidencja. */
export function statLinkLabel(routeName) {
  if (routeName.startsWith(REPORT_ROUTE_PREFIX)) {
    return 'Zobacz raport →'
  }
  switch (routeName) {
    case 'seniors':
      return 'Lista seniorów →'
    case 'employees':
      return 'Lista pracowników →'
    case 'senior-homes':
      return 'Ewidencja domów →'
    default:
      return 'Przejdź do modułu →'
  }
}

export function dashboardTitle(role, roleLabel) {
  return DASHBOARD_TITLES[role] ?? `Pulpit — ${roleLabel}`
}

export function dashboardDescription(role, roleLabel) {
  return (
    DASHBOARD_DESCRIPTIONS[role] ??
    `Witaj. Poniżej wskaźniki dostępne dla roli: ${roleLabel}.`
  )
}
