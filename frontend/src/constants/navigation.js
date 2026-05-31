/** Pozycje menu — `routeName` musi zgadzać się z `name` w routerze. */
export const navigationGroups = [
  {
    label: 'Przegląd',
    items: [{ to: '/', label: 'Pulpit', routeName: 'dashboard' }],
  },
  {
    label: 'Ewidencja',
    items: [
      { to: '/domy-seniora', label: 'Domy seniora', routeName: 'senior-homes' },
      { to: '/adresy', label: 'Adresy i poczty', routeName: 'addresses' },
      { to: '/seniorzy', label: 'Seniorzy', routeName: 'seniors' },
      { to: '/pracownicy', label: 'Pracownicy', routeName: 'employees' },
      { to: '/stanowiska', label: 'Stanowiska', routeName: 'positions' },
      { to: '/pomieszczenia', label: 'Pomieszczenia', routeName: 'rooms' },
      { to: '/zajecia', label: 'Zajęcia', routeName: 'activities' },
      { to: '/sprzet', label: 'Sprzęt', routeName: 'equipment' },
      { to: '/przypisania', label: 'Przypisania', routeName: 'assignments' },
    ],
  },
  {
    label: 'Raporty',
    items: [
      { to: '/raporty', label: 'Wszystkie raporty', routeName: 'reports' },
      { to: '/raporty/oblozenie-pokoi', label: 'Obłożenie pokoi', routeName: 'report-room-occupancy' },
      {
        to: '/raporty/aktywnosc-seniorow',
        label: 'Aktywność seniorów',
        routeName: 'report-senior-activity',
      },
      {
        to: '/raporty/obciazenie-pracownikow',
        label: 'Obciążenie pracowników',
        routeName: 'report-employee-workload',
      },
      { to: '/raporty/jakosc-danych', label: 'Jakość danych', routeName: 'report-data-quality' },
    ],
  },
  {
    label: 'System',
    items: [{ to: '/administracja', label: 'Administracja', routeName: 'administration' }],
  },
]
