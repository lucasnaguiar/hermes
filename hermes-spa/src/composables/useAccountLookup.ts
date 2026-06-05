import { ref } from 'vue'
import { accountService } from '../services/accountService'
import type { AccountResponse } from '../types/account'

export function useAccountLookup() {
  const account = ref<AccountResponse | null>(null)
  const loading = ref(false)
  const error = ref('')

  async function lookup(accountNumber: string) {
    if (!/^\d{10}$/.test(accountNumber)) {
      error.value = 'O número da conta deve ter exatamente 10 dígitos.'
      account.value = null
      return
    }

    loading.value = true
    error.value = ''
    account.value = null

    try {
      account.value = await accountService.findByAccountNumber(accountNumber)
    } catch (e: unknown) {
      error.value = e instanceof Error ? e.message : 'Conta não encontrada.'
    } finally {
      loading.value = false
    }
  }

  function reset() {
    account.value = null
    loading.value = false
    error.value = ''
  }

  return { account, loading, error, lookup, reset }
}
