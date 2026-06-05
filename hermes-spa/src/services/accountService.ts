import http from './http'
import type { AccountResponse } from '../types/account'

export const accountService = {
  findByAccountNumber(accountNumber: string): Promise<AccountResponse> {
    return http.get<AccountResponse>(`/accounts/${accountNumber}`).then((r) => r.data)
  },
}
