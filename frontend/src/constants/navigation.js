export const navigationGroups = [
  {
    label: 'Przegląd',
    items: [{ to: '/', label: 'Pulpit' }],
  },
  {
    label: 'Ewidencja',
    items: [
      { to: '/domy-seniora', label: 'Domy seniora' },
      { to: '/adresy', label: 'Adresy i poczty' },
      { to: '/seniorzy', label: 'Seniorzy' },
      { to: '/pracownicy', label: 'Pracownicy' },
      { to: '/stanowiska', label: 'Stanowiska' },
      { to: '/pomieszczenia', label: 'Pomieszczenia' },
      { to: '/zajecia', label: 'Zajęcia' },
      { to: '/sprzet', label: 'Sprzęt' },
      { to: '/przypisania', label: 'Przypisania' },
    ],
  },
  {
    label: 'Raporty',
    items: [
      { to: '/raporty', label: 'Wszystkie raporty' },
      { to: '/raporty/oblozenie-pokoi', label: 'Obłożenie pokoi' },
      { to: '/raporty/aktywnosc-seniorow', label: 'Aktywność seniorów' },
      { to: '/raporty/obciazenie-pracownikow', label: 'Obciążenie pracowników' },
      { to: '/raporty/jakosc-danych', label: 'Jakość danych' },
    ],
  },
]
