<script setup lang="ts">
import { NDatePicker, NSpace } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import BaseButton from '../../atoms/BaseButton.vue'

const store = useTransferScheduleStore()

function isDateDisabled(ts: number): boolean {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const maxDate = new Date()
  maxDate.setDate(maxDate.getDate() + 50)
  maxDate.setHours(23, 59, 59, 999)
  return ts < today.getTime() || ts > maxDate.getTime()
}
</script>

<template>
  <div class="flex flex-col gap-4">
    <p class="text-sm text-gray-500">
      Escolha a data da transferência (hoje até 50 dias à frente).
    </p>

    <n-date-picker
      v-model:value="store.transferDateTs"
      type="date"
      :is-date-disabled="isDateDisabled"
      placeholder="Selecione uma data"
      class="w-full"
    />

    <n-space justify="space-between">
      <base-button @click="store.goToStep(3)">Voltar</base-button>
      <base-button
        variant="primary"
        :disabled="!store.transferDateTs"
        @click="store.goToStep(5)"
      >
        Simular transferência
      </base-button>
    </n-space>
  </div>
</template>
