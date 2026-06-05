<script setup lang="ts">
import { NInputNumber, NSpace } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import BaseButton from '../../atoms/BaseButton.vue'

const store = useTransferScheduleStore()
</script>

<template>
  <div class="flex flex-col gap-4">
    <p class="text-sm text-gray-500">Informe o valor a ser transferido.</p>

    <n-input-number
      v-model:value="store.transferAmount"
      :min="0.01"
      :precision="2"
      placeholder="0,00"
      :show-button="false"
      class="w-full"
    >
      <template #prefix>R$</template>
    </n-input-number>

    <n-space justify="space-between">
      <base-button @click="store.goToStep(2)">Voltar</base-button>
      <base-button
        variant="primary"
        :disabled="!store.transferAmount || store.transferAmount < 0.01"
        @click="store.goToStep(4)"
      >
        Avançar
      </base-button>
    </n-space>
  </div>
</template>
