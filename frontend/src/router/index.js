import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: AppLayout,
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/dashboard/DashboardView.vue'),
        },
        {
          path: 'domy-seniora',
          name: 'senior-homes',
          component: () => import('@/views/senior-homes/SeniorHomeListView.vue'),
        },
        {
          path: 'adresy',
          name: 'addresses',
          component: () => import('@/views/addresses/AddressListView.vue'),
        },
        {
          path: 'seniorzy',
          name: 'seniors',
          component: () => import('@/views/seniors/SeniorListView.vue'),
        },
        {
          path: 'pracownicy',
          name: 'employees',
          component: () => import('@/views/employees/EmployeeListView.vue'),
        },
        {
          path: 'stanowiska',
          name: 'positions',
          component: () => import('@/views/positions/PositionListView.vue'),
        },
        {
          path: 'pomieszczenia',
          name: 'rooms',
          component: () => import('@/views/rooms/RoomListView.vue'),
        },
        {
          path: 'zajecia',
          name: 'activities',
          component: () => import('@/views/activities/ActivityListView.vue'),
        },
        {
          path: 'sprzet',
          name: 'equipment',
          component: () => import('@/views/equipment/EquipmentListView.vue'),
        },
        {
          path: 'przypisania',
          name: 'assignments',
          component: () => import('@/views/assignments/AssignmentsView.vue'),
        },
        {
          path: 'raporty',
          name: 'reports',
          component: () => import('@/views/reports/ReportsHubView.vue'),
        },
        {
          path: 'raporty/oblozenie-pokoi',
          name: 'report-room-occupancy',
          component: () => import('@/views/reports/RoomOccupancyReportView.vue'),
        },
        {
          path: 'raporty/aktywnosc-seniorow',
          name: 'report-senior-activity',
          component: () => import('@/views/reports/SeniorActivityReportView.vue'),
        },
        {
          path: 'raporty/obciazenie-pracownikow',
          name: 'report-employee-workload',
          component: () => import('@/views/reports/EmployeeWorkloadReportView.vue'),
        },
        {
          path: 'raporty/jakosc-danych',
          name: 'report-data-quality',
          component: () => import('@/views/reports/DataQualityReportView.vue'),
        },
      ],
    },
  ],
})

export default router
