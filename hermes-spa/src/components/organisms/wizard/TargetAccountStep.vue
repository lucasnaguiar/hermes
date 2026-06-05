<script setup lang="ts">
import { ref } from 'vue'
import { NInput, NSpace, NDivider } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import { useAccountLookup } from '../../../composables/useAccountLookup'
import AccountCard from '../../molecules/AccountCard.vue'
import BaseButton from '../../atoms/BaseButton.vue'

const store = useTransferScheduleStore()
const { account, loading, error, lookup } = useAccountLookup()

const accountNumber = ref(store.targetAccount?.accountNumber ?? '')

async function next() {
  await lookup(accountNumber.value)
  if (account.value) {
    store.targetAccount = account.value
    store.goToStep(3)
  }
}
</script>

<template>
  <div class="flex flex-col gap-4">
    <account-card :account="store.sourceAccount!" title="Conta de Origem" />

    <n-divider />

    <p class="text-sm text-gray-500">Informe o número da conta de destino (10 dígitos).</p>

    <n-input
      v-model:value="accountNumber"
      placeholder="Ex: 0987654321"
      maxlength="10"
      :status="error ? 'error' : undefined"
      @keyup.enter="next"
    />
    <span v-if="error" class="text-red-500 text-sm">{{ error }}</span>

    <account-card v-if="account" :account="account" title="Conta de Destino" />

    <n-space justify="space-between">
      <base-button @click="store.goToStep(1)">Voltar</base-button>
      <base-button
        variant="primary"
        :loading="loading"
        :disabled="!accountNumber"
        @click="next"
      >
        Validar e avançar
      </base-button>
    </n-space>
  </div>
</template>
