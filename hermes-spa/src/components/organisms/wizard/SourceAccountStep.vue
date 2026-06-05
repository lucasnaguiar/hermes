<script setup lang="ts">
import { ref } from 'vue'
import { NInput, NSpace } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import { useAccountLookup } from '../../../composables/useAccountLookup'
import AccountCard from '../../molecules/AccountCard.vue'
import BaseButton from '../../atoms/BaseButton.vue'

const store = useTransferScheduleStore()
const { account, loading, error, lookup } = useAccountLookup()

const accountNumber = ref(store.sourceAccount?.accountNumber ?? '')

async function validate() {
  await lookup(accountNumber.value)
  if (account.value) {
    store.sourceAccount = account.value
  }
}

async function next() {
  await validate()
  if (account.value) store.goToStep(2)
}
</script>

<template>
  <div class="flex flex-col gap-4">
    <p class="text-sm text-gray-500">Informe o número da conta de origem (10 dígitos).</p>

    <n-input
      v-model:value="accountNumber"
      placeholder="Ex: 1234567890"
      maxlength="10"
      :status="error ? 'error' : undefined"
      @keyup.enter="next"
    />
    <span v-if="error" class="text-red-500 text-sm">{{ error }}</span>

    <account-card v-if="account" :account="account" title="Conta de Origem" />

    <n-space justify="end">
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
