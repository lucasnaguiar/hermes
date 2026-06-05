<script setup lang="ts">
import { NTag } from 'naive-ui'
import type { TransferStatus } from '../../types/transfer'

interface Props {
  status: TransferStatus
}

const props = defineProps<Props>()

const config: Record<TransferStatus, { type: 'success' | 'warning' | 'info'; label: string }> = {
  SCHEDULED: { type: 'info', label: 'Agendado' },
  EXECUTED: { type: 'success', label: 'Executado' },
  CANCELLED: { type: 'warning', label: 'Cancelado' },
}
</script>

<template>
  <n-tag :type="config[props.status].type" :bordered="false" size="small">
    {{ config[props.status].label }}
  </n-tag>
</template>
