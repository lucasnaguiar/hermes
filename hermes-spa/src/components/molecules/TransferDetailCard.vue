<script setup lang="ts">
import { computed } from 'vue'
import { NCard, NDescriptions, NDescriptionsItem } from 'naive-ui'
import CurrencyDisplay from '../atoms/CurrencyDisplay.vue'
import BaseBadge from '../atoms/BaseBadge.vue'
import type { TransferScheduleResponse } from '../../types/transfer'

interface Props {
  transfer: TransferScheduleResponse
}

const props = defineProps<Props>()

const feeRangeLabel = computed(() => {
  const { feeDaysRangeFrom, feeDaysRangeTo } = props.transfer
  if (feeDaysRangeFrom === 0 && feeDaysRangeTo === 0) return 'Transferência no mesmo dia'
  return `Entre ${feeDaysRangeFrom} e ${feeDaysRangeTo} dias`
})

const formatDate = (date: string) =>
  new Date(date + 'T00:00:00').toLocaleDateString('pt-BR')
</script>

<template>
  <div class="flex flex-col gap-4">
    <n-card title="Dados da Transferência" size="small">
      <n-descriptions :column="1" label-placement="left" bordered size="small">
        <n-descriptions-item label="ID">
          {{ props.transfer.id }}
        </n-descriptions-item>
        <n-descriptions-item label="Status">
          <base-badge :status="props.transfer.status" />
        </n-descriptions-item>
        <n-descriptions-item label="Conta origem">
          {{ props.transfer.sourceAccount }}
        </n-descriptions-item>
        <n-descriptions-item label="Conta destino">
          {{ props.transfer.targetAccount }}
        </n-descriptions-item>
        <n-descriptions-item label="Valor">
          <currency-display :value="props.transfer.transferAmount" :highlight="true" />
        </n-descriptions-item>
        <n-descriptions-item label="Data de transferência">
          {{ formatDate(props.transfer.transferDate) }}
        </n-descriptions-item>
        <n-descriptions-item label="Data de agendamento">
          {{ formatDate(props.transfer.schedulingDate) }}
        </n-descriptions-item>
      </n-descriptions>
    </n-card>

    <n-card title="Taxa Aplicada" size="small">
      <n-descriptions :column="1" label-placement="left" bordered size="small">
        <n-descriptions-item label="Regra aplicada">
          {{ feeRangeLabel }}
        </n-descriptions-item>
        <n-descriptions-item v-if="props.transfer.feeRate > 0" label="Percentual">
          {{ props.transfer.feeRate }}%
        </n-descriptions-item>
        <n-descriptions-item v-if="props.transfer.feeFixedAmount > 0" label="Taxa fixa">
          <currency-display :value="props.transfer.feeFixedAmount" />
        </n-descriptions-item>
        <n-descriptions-item label="Total de taxa">
          <currency-display :value="props.transfer.feeAmount" :highlight="true" />
        </n-descriptions-item>
      </n-descriptions>
    </n-card>
  </div>
</template>
