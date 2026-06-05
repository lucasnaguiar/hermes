<script setup lang="ts">
import { ref, reactive, onMounted, h, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  NDataTable,
  NSpace,
  NInput,
  NButton,
  NSpin,
  NAlert,
} from 'naive-ui'
import type { DataTableColumns, PaginationProps } from 'naive-ui'
import BaseBadge from '../atoms/BaseBadge.vue'
import CurrencyDisplay from '../atoms/CurrencyDisplay.vue'
import { transferService } from '../../services/transferService'
import type { TransferScheduleResponse } from '../../types/transfer'

const router = useRouter()

const data = ref<TransferScheduleResponse[]>([])
const loading = ref(false)
const error = ref('')

const filters = reactive({ sourceAccount: '', targetAccount: '' })

const pagination = reactive<PaginationProps>({
  page: 1,
  pageSize: 10,
  itemCount: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50],
  onChange: (page: number) => {
    pagination.page = page
    fetchData()
  },
  onUpdatePageSize: (size: number) => {
    pagination.pageSize = size
    pagination.page = 1
    fetchData()
  },
})

const formatDate = (date: string) =>
  new Date(date + 'T00:00:00').toLocaleDateString('pt-BR')

const columns: DataTableColumns<TransferScheduleResponse> = [
  { title: 'Conta Origem', key: 'sourceAccount' },
  { title: 'Conta Destino', key: 'targetAccount' },
  {
    title: 'Valor',
    key: 'transferAmount',
    render: (row) => h(CurrencyDisplay, { value: row.transferAmount }),
  },
  {
    title: 'Data Transferência',
    key: 'transferDate',
    render: (row) => formatDate(row.transferDate),
  },
  {
    title: 'Data Agendamento',
    key: 'schedulingDate',
    render: (row) => formatDate(row.schedulingDate),
  },
  {
    title: 'Status',
    key: 'status',
    render: (row) => h(BaseBadge, { status: row.status }),
  },
  {
    title: 'Taxa',
    key: 'feeAmount',
    render: (row) => h(CurrencyDisplay, { value: row.feeAmount }),
  },
  {
    title: 'Detalhes',
    key: 'actions',
    render: (row) =>
      h(
        NButton,
        {
          size: 'small',
          quaternary: true,
          type: 'primary',
          onClick: () => router.push({ name: 'transfer-detail', params: { id: row.id } }),
        },
        { default: () => 'Ver detalhes' },
      ),
  },
]

async function fetchData() {
  loading.value = true
  error.value = ''
  try {
    const result = await transferService.list({
      sourceAccount: filters.sourceAccount || undefined,
      targetAccount: filters.targetAccount || undefined,
      page: (pagination.page ?? 1) - 1,
      size: pagination.pageSize,
    })
    data.value = result.content
    pagination.itemCount = result.totalElements
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Erro ao carregar agendamentos.'
  } finally {
    loading.value = false
  }
}

function applyFilters() {
  pagination.page = 1
  fetchData()
}

function clearFilters() {
  filters.sourceAccount = ''
  filters.targetAccount = ''
  pagination.page = 1
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div class="flex flex-col gap-4">
    <!-- Filters -->
    <div class="flex flex-col md:flex-row gap-3">
      <n-input
        v-model:value="filters.sourceAccount"
        placeholder="Filtrar por conta origem"
        maxlength="10"
        clearable
        class="md:max-w-60"
        @keyup.enter="applyFilters"
      />
      <n-input
        v-model:value="filters.targetAccount"
        placeholder="Filtrar por conta destino"
        maxlength="10"
        clearable
        class="md:max-w-60"
        @keyup.enter="applyFilters"
      />
      <n-space>
        <n-button type="primary" @click="applyFilters">Filtrar</n-button>
        <n-button @click="clearFilters">Limpar</n-button>
      </n-space>
    </div>

    <n-alert v-if="error" type="error">{{ error }}</n-alert>

    <n-spin :show="loading">
      <n-data-table
        remote
        :columns="columns"
        :data="data"
        :pagination="pagination"
        :bordered="true"
        :scroll-x="800"
      />
    </n-spin>
  </div>
</template>
