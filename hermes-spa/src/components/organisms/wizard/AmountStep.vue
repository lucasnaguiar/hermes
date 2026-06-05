<script setup lang="ts">
import { computed } from 'vue'
import { NInputNumber, NSpace, NAlert } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import BaseButton from '../../atoms/BaseButton.vue'
import CurrencyDisplay from '../../atoms/CurrencyDisplay.vue'

const store = useTransferScheduleStore()

const availableBalance = computed(() => store.sourceAccount?.availableBalance ?? 0)

const exceedsBalance = computed(() =>
  !!store.transferAmount && store.transferAmount > availableBalance.value
)

const canAdvance = computed(() =>
  !!store.transferAmount && store.transferAmount >= 0.01 && !exceedsBalance.value
)
</script>

<template>
  <div class="flex flex-col gap-4">
    <p class="text-sm text-gray-500">
      Informe o valor a ser transferido. Saldo disponível:
      <currency-display :value="availableBalance" :highlight="true" />
    </p>

    <n-input-number
      v-model:value="store.transferAmount"
      :min="0.01"
      :max="availableBalance"
      :precision="2"
      placeholder="0,00"
      :show-button="false"
      class="w-full"
    >
      <template #prefix>R$</template>
    </n-input-number>

    <n-alert v-if="exceedsBalance" type="error" :show-icon="false">
      O valor informado supera o saldo disponível de
      <currency-display :value="availableBalance" />.
    </n-alert>

    <n-space justify="space-between">
      <base-button @click="store.goToStep(2)">Voltar</base-button>
      <base-button
        variant="primary"
        :disabled="!canAdvance"
        @click="store.goToStep(4)"
      >
        Avançar
      </base-button>
    </n-space>
  </div>
</template>
