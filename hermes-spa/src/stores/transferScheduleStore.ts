import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { AccountResponse } from '../types/account'
import type { TransferSimulateResponse, TransferRequest } from '../types/transfer'

export const useTransferScheduleStore = defineStore('transferSchedule', () => {
  const step = ref(1)
  const sourceAccount = ref<AccountResponse | null>(null)
  const targetAccount = ref<AccountResponse | null>(null)
  const transferAmount = ref<number | null>(null)
  const transferDateTs = ref<number | null>(null)
  const simulation = ref<TransferSimulateResponse | null>(null)

  const transferDateString = computed<string>(() => {
    if (!transferDateTs.value) return ''
    const d = new Date(transferDateTs.value)
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
  })

  const transferRequest = computed<TransferRequest | null>(() => {
    if (!sourceAccount.value || !targetAccount.value || !transferAmount.value || !transferDateString.value) {
      return null
    }
    return {
      sourceAccount: sourceAccount.value.accountNumber,
      targetAccount: targetAccount.value.accountNumber,
      transferAmount: transferAmount.value,
      transferDate: transferDateString.value,
    }
  })

  function goToStep(n: number) {
    step.value = n
  }

  function reset() {
    step.value = 1
    sourceAccount.value = null
    targetAccount.value = null
    transferAmount.value = null
    transferDateTs.value = null
    simulation.value = null
  }

  return {
    step,
    sourceAccount,
    targetAccount,
    transferAmount,
    transferDateTs,
    simulation,
    transferDateString,
    transferRequest,
    goToStep,
    reset,
  }
})
