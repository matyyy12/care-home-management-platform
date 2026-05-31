<script setup>
import PageHeader from '@/components/layout/PageHeader.vue'
import AppCard from '@/components/ui/AppCard.vue'
import DataTable from '@/components/ui/DataTable.vue'
import { equipment, equipmentBrands, equipmentModels } from '@/mock/data'

const brandColumns = [{ key: 'name', label: 'Marka' }]
const modelColumns = [
  { key: 'name', label: 'Model' },
  { key: 'brand', label: 'Marka' },
]
const equipmentColumns = [
  { key: 'type', label: 'Typ' },
  { key: 'description', label: 'Opis' },
  { key: 'brand', label: 'Marka' },
  { key: 'model', label: 'Model' },
  { key: 'office', label: 'Gabinet' },
]
</script>

<template>
  <div class="space-y-8">
    <PageHeader
      title="Sprzęt"
      description="Marki, modele i sprzęt gabinetowy — hierarchia marka → model → sprzęt."
    />

    <div class="grid gap-6 lg:grid-cols-3">
      <AppCard title="Marki">
        <DataTable :columns="brandColumns" :rows="equipmentBrands" />
      </AppCard>
      <AppCard title="Modele">
        <DataTable :columns="modelColumns" :rows="equipmentModels" />
      </AppCard>
      <AppCard title="Sprzęt" class="lg:col-span-1">
        <DataTable :columns="equipmentColumns" :rows="equipment" />
      </AppCard>
    </div>
  </div>
</template>
