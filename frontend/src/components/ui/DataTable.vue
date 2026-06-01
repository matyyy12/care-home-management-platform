<script setup>
defineProps({
  columns: { type: Array, required: true },
  rows: { type: Array, default: () => [] },
  rowKey: { type: String, default: 'id' },
  emptyMessage: { type: String, default: 'Brak danych do wyświetlenia.' },
  clickable: { type: Boolean, default: false },
})

defineEmits(['row-click'])

function cellValue(row, column) {
  if (column.format) return column.format(row)
  const value = row[column.key]
  return value ?? '—'
}
</script>

<template>
  <div class="overflow-x-auto rounded-lg border border-slate-200">
    <table class="min-w-full divide-y divide-slate-200 text-left text-sm">
      <thead class="bg-slate-50">
        <tr>
          <th
            v-for="col in columns"
            :key="col.key"
            scope="col"
            class="px-4 py-3 font-medium text-slate-700"
            :class="col.headerClass"
          >
            {{ col.label }}
          </th>
          <th v-if="$slots.actions" scope="col" class="px-4 py-3 text-right font-medium text-slate-700">
            Akcje
          </th>
        </tr>
      </thead>
      <tbody class="divide-y divide-slate-100 bg-white">
        <tr v-if="rows.length === 0">
          <td
            :colspan="columns.length + ($slots.actions ? 1 : 0)"
            class="px-4 py-8 text-center text-slate-500"
          >
            {{ emptyMessage }}
          </td>
        </tr>
        <tr
          v-for="row in rows"
          :key="row[rowKey]"
          class="hover:bg-slate-50/80"
          :class="{ 'cursor-pointer': clickable }"
          @click="clickable && $emit('row-click', row)"
        >
          <td
            v-for="col in columns"
            :key="col.key"
            class="px-4 py-3 text-slate-800"
            :class="col.cellClass"
          >
            <slot :name="`cell-${col.key}`" :row="row" :value="row[col.key]">
              {{ cellValue(row, col) }}
            </slot>
          </td>
          <td v-if="$slots.actions" class="px-4 py-3 text-right" @click.stop>
            <slot name="actions" :row="row" />
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
