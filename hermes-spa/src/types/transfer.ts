export type TransferStatus = 'SCHEDULED' | 'CANCELLED' | 'EXECUTED'

export interface TransferRequest {
  sourceAccount: string
  targetAccount: string
  transferAmount: number
  transferDate: string // ISO date: YYYY-MM-DD
}

export interface TransferSimulateResponse {
  sourceAccount: string
  targetAccount: string
  transferAmount: number
  transferDate: string
  schedulingDate: string
  feeAmount: number
  feeRate: number
  feeFixedAmount: number
  feeDaysRangeFrom: number
  feeDaysRangeTo: number
}

export interface TransferScheduleResponse {
  id: string
  sourceAccount: string
  targetAccount: string
  transferAmount: number
  transferDate: string
  schedulingDate: string
  status: TransferStatus
  feeAmount: number
  feeRate: number
  feeFixedAmount: number
  feeDaysRangeFrom: number
  feeDaysRangeTo: number
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface TransferListParams {
  sourceAccount?: string
  targetAccount?: string
  page?: number
  size?: number
}
