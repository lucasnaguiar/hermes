<script setup lang="ts">
import { computed } from 'vue'
import { NCard, NDescriptions, NDescriptionsItem, NAlert } from 'naive-ui'
import CurrencyDisplay from '../atoms/CurrencyDisplay.vue'
import type { TransferSimulateResponse } from '../../types/transfer'

interface Props {
  simulation: TransferSimulateResponse
}

const props = defineProps<Props>()

const feeRangeLabel = computed(() => {
  const { feeDaysRangeFrom, feeDaysRangeTo } = props.simulation
  if (feeDaysRangeFrom === 0 && feeDaysRangeTo === 0) return 'Transferência no mesmo dia'
  return `Entre ${feeDaysRangeFrom} e ${feeDaysRangeTo} dias`
})

const formattedTransferDate = computed(() =>
  new Date(props.simulation.transferDate + 'T00:00:00').toLocaleDateString('pt-BR'),
)

const formattedSchedulingDate = computed(() =>
  new Date(props.simulation.schedulingDate + 'T00:00:00').toLocaleDateString('pt-BR'),
)
</script>

<template>
  <div class="flex flex-col gap-4">
    <n-alert type="info" :show-icon="false">
      Confira o resumo da transferência antes de confirmar.
    </n-alert>

    <n-card title="Resumo da Transferência" size="small">
      <n-descriptions :column="1" label-placement="left" bordered size="small">
        <n-descriptions-item label="Conta origem">
          {{ props.simulation.sourceAccount }}
        </n-descriptions-item>
        <n-descriptions-item label="Conta destino">
          {{ props.simulation.targetAccount }}
        </n-descriptions-item>
        <n-descriptions-item label="Valor">
          <currency-display :value="props.simulation.transferAmount" :highlight="true" />
        </n-descriptions-item>
        <n-descriptions-item label="Data de transferência">
          {{ formattedTransferDate }}
        </n-descriptions-item>
        <n-descriptions-item label="Data de agendamento">
          {{ formattedSchedulingDate }}
        </n-descriptions-item>
      </n-descriptions>
    </n-card>

    <n-card title="Taxa Aplicada" size="small">
      <n-descriptions :column="1" label-placement="left" bordered size="small">
        <n-descriptions-item label="Regra aplicada">
          {{ feeRangeLabel }}
        </n-descriptions-item>
        <n-descriptions-item v-if="props.simulation.feeRate > 0" label="Percentual">
          {{ props.simulation.feeRate }}%
        </n-descriptions-item>
        <n-descriptions-item v-if="props.simulation.feeFixedAmount > 0" label="Taxa fixa">
          <currency-display :value="props.simulation.feeFixedAmount" />
        </n-descriptions-item>
        <n-descriptions-item label="Total de taxa">
          <currency-display :value="props.simulation.feeAmount" :highlight="true" />
        </n-descriptions-item>
      </n-descriptions>
    </n-card>
  </div>
</template>
