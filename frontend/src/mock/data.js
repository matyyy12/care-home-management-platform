export const seniorHomes = [
  {
    id: 1,
    name: 'Dom Seniora „Złota Jesień”',
    phone: '+48 12 345 67 89',
    email: 'kontakt@zlota-jesien.pl',
    website: 'https://zlota-jesien.pl',
    foundedAt: '2010-03-15',
    addressLabel: 'Kraków, ul. Lipowa 12/4',
    owners: ['Jan Kowalski', 'Anna Nowak'],
  },
  {
    id: 2,
    name: 'Dom Seniora „Spokojna Przystań”',
    phone: '+48 22 987 65 43',
    email: 'biuro@spokojna.pl',
    website: '',
    foundedAt: '2015-09-01',
    addressLabel: 'Warszawa, ul. Parkowa 8',
    owners: ['Piotr Wiśniewski'],
  },
]

export const postOffices = [
  { id: 1, postalCode: '30-001', city: 'Kraków' },
  { id: 2, postalCode: '00-001', city: 'Warszawa' },
]

export const addresses = [
  {
    id: 1,
    city: 'Kraków',
    street: 'Lipowa',
    apartmentNumber: '12/4',
    postOffice: '30-001 Kraków',
  },
  {
    id: 2,
    city: 'Warszawa',
    street: 'Parkowa',
    apartmentNumber: '8',
    postOffice: '00-001 Warszawa',
  },
]

export const positions = [
  { id: 1, name: 'Opiekun', duties: 'Opieka nad seniorami, dokumentacja' },
  { id: 2, name: 'Pielęgniarka', duties: 'Obsługa medyczna, podawanie leków' },
  { id: 3, name: 'Koordynator zajęć', duties: 'Organizacja zajęć i harmonogramów' },
]

export const employees = [
  {
    id: 1,
    firstName: 'Maria',
    lastName: 'Lewandowska',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    position: 'Opiekun',
    phone: '+48 500 100 200',
    email: 'm.lewandowska@zlota-jesien.pl',
    hiredAt: '2018-04-01',
    terminatedAt: null,
    isMedical: false,
    salary: 5200,
  },
  {
    id: 2,
    firstName: 'Ewa',
    lastName: 'Zielińska',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    position: 'Pielęgniarka',
    phone: '+48 500 300 400',
    email: 'e.zielinska@zlota-jesien.pl',
    hiredAt: '2016-01-10',
    terminatedAt: null,
    isMedical: true,
    licenseNumber: 'NP-12345',
    specialization: 'Geriatria',
    salary: 6800,
  },
]

export const seniors = [
  {
    id: 1,
    firstName: 'Helena',
    lastName: 'Kowalczyk',
    pesel: '32010112345',
    birthDate: '1932-01-01',
    gender: 'F',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    accommodationDate: '2023-06-15',
    dischargeDate: null,
    status: 'ACTIVE',
    roomNumber: '101',
    guardians: 'Maria Lewandowska',
    diseases: 'Cukrzyca, nadciśnienie',
  },
  {
    id: 2,
    firstName: 'Stanisław',
    lastName: 'Wójcik',
    pesel: '41050567890',
    birthDate: '1941-05-05',
    gender: 'M',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    accommodationDate: '2022-11-20',
    dischargeDate: null,
    status: 'ACTIVE',
    roomNumber: '102',
    guardians: 'Maria Lewandowska',
    diseases: 'Choroba wieńcowa',
  },
]

export const rooms = [
  {
    id: 1,
    number: '101',
    floor: 1,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    inUse: true,
    type: 'RESIDENTIAL',
    capacity: 2,
    standard: 'Podstawowy',
    assignedSeniors: 1,
  },
  {
    id: 2,
    number: '102',
    floor: 1,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    inUse: true,
    type: 'RESIDENTIAL',
    capacity: 2,
    standard: 'Podstawowy',
    assignedSeniors: 1,
  },
  {
    id: 3,
    number: 'G1',
    floor: 0,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    inUse: true,
    type: 'OFFICE',
    officeKind: 'Gabinet pielęgniarki',
    equipment: 'Ciśnieniomierz Omron',
  },
  {
    id: 4,
    number: 'SG1',
    floor: 0,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    inUse: true,
    type: 'GAME_ROOM',
    hasTv: true,
    computers: 2,
    crosswords: 15,
    boardGames: 8,
    hasRadio: true,
  },
]

export const activities = [
  {
    id: 1,
    name: 'Gimnastyka lekka',
    type: 'RECREATIONAL',
    price: 0,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    description: 'Ćwiczenia poranne w sali gier',
    instructors: 1,
    participants: 12,
  },
  {
    id: 2,
    name: 'Kontrola ciśnienia',
    type: 'MEDICAL',
    price: 25,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    description: 'Pomiar i konsultacja',
    instructors: 1,
    participants: 8,
  },
]

export const equipmentBrands = [
  { id: 1, name: 'Omron' },
  { id: 2, name: 'Philips' },
]

export const equipmentModels = [
  { id: 1, name: 'M3 Comfort', brand: 'Omron' },
  { id: 2, name: 'Inhalator Compact', brand: 'Philips' },
]

export const equipment = [
  {
    id: 1,
    type: 'Ciśnieniomierz',
    description: 'Automatyczny, nadgarstkowy',
    model: 'M3 Comfort',
    brand: 'Omron',
    office: 'G1',
  },
]

export const assignments = [
  {
    id: 1,
    type: 'SENIOR_ROOM',
    label: 'Senior → pokój',
    from: 'Helena Kowalczyk',
    to: 'Pokój 101',
    seniorHome: 'Dom Seniora „Złota Jesień”',
  },
  {
    id: 2,
    type: 'EMPLOYEE_SENIOR',
    label: 'Pracownik → senior',
    from: 'Maria Lewandowska',
    to: 'Helena Kowalczyk',
    seniorHome: 'Dom Seniora „Złota Jesień”',
  },
  {
    id: 3,
    type: 'SENIOR_ACTIVITY',
    label: 'Senior → zajęcia',
    from: 'Helena Kowalczyk',
    to: 'Gimnastyka lekka',
    seniorHome: 'Dom Seniora „Złota Jesień”',
  },
]

export const roomOccupancyReport = [
  {
    id: 1,
    roomNumber: '101',
    floor: 1,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    capacity: 2,
    assigned: 1,
    free: 1,
    standard: 'Podstawowy',
  },
  {
    id: 2,
    roomNumber: '102',
    floor: 1,
    seniorHome: 'Dom Seniora „Złota Jesień”',
    capacity: 2,
    assigned: 1,
    free: 1,
    standard: 'Podstawowy',
  },
]

export const seniorActivityReport = [
  {
    id: 1,
    senior: 'Helena Kowalczyk',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    status: 'Aktywny',
    activityCount: 2,
    activityTypes: 'Rekreacyjne, medyczne',
    guardians: 'Maria Lewandowska',
  },
  {
    id: 2,
    senior: 'Jan Malinowski',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    status: 'Aktywny',
    activityCount: 0,
    activityTypes: '—',
    guardians: '—',
  },
]

export const employeeWorkloadReport = [
  {
    id: 1,
    employee: 'Maria Lewandowska',
    position: 'Opiekun',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    isMedical: false,
    wards: 2,
    rooms: 1,
    activities: 1,
    status: 'Aktywny',
  },
  {
    id: 2,
    employee: 'Ewa Zielińska',
    position: 'Pielęgniarka',
    seniorHome: 'Dom Seniora „Złota Jesień”',
    isMedical: true,
    wards: 0,
    rooms: 1,
    activities: 1,
    status: 'Aktywny',
  },
]

export const dataQualityIssues = [
  {
    id: 1,
    category: 'Seniorzy',
    issue: 'Aktywny senior bez opiekuna',
    entity: 'Jan Malinowski',
    severity: 'warning',
  },
  {
    id: 2,
    category: 'Seniorzy',
    issue: 'Aktywny senior bez pokoju',
    entity: 'Jan Malinowski',
    severity: 'warning',
  },
]
