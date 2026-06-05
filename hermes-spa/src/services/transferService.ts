import http from './http'
import type {
  TransferRequest,
  TransferSimulateResponse,
  TransferScheduleResponse,
  TransferListParams,
  Page,
} from '../types/transfer'

export const transferService = {
  simulate(request: TransferRequest): Promise<TransferSimulateResponse> {
    return http.post<TransferSimulateResponse>('/transfers/simulate', request).then((r) => r.data)
  },

  schedule(request: TransferRequest): Promise<void> {
    return http.post('/transfers', request).then(() => undefined)
  },

  findById(id: string): Promise<TransferScheduleResponse> {
    return http.get<TransferScheduleResponse>(`/transfers/${id}`).then((r) => r.data)
  },

  list(params: TransferListParams = {}): Promise<Page<TransferScheduleResponse>> {
    return http
      .get<Page<TransferScheduleResponse>>('/transfers', { params })
      .then((r) => r.data)
  },
}
