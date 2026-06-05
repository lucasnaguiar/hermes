<script setup lang="ts">
import { NCard, NDescriptions, NDescriptionsItem } from 'naive-ui'
import CurrencyDisplay from '../atoms/CurrencyDisplay.vue'
import type { AccountResponse } from '../../types/account'

interface Props {
  account: AccountResponse
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Dados da Conta',
})
</script>

<template>
  <n-card :title="props.title" size="small">
    <n-descriptions :column="1" label-placement="left" bordered size="small">
      <n-descriptions-item label="Número">
        {{ props.account.accountNumber }}
      </n-descriptions-item>
      <n-descriptions-item label="Saldo total">
        <currency-display :value="props.account.balance" />
      </n-descriptions-item>
      <n-descriptions-item label="Saldo comprometido">
        <currency-display :value="props.account.committedBalance" />
      </n-descriptions-item>
      <n-descriptions-item label="Saldo disponível">
        <currency-display :value="props.account.availableBalance" :highlight="true" />
      </n-descriptions-item>
    </n-descriptions>
  </n-card>
</template>
