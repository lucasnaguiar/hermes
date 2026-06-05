<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  NDataTable,
  NInput,
  NButton,
  NSpin,
  NAlert,
  NDrawer,
  NDrawerContent,
  NSpace,
  NIcon,
  NCard,
  NPagination,
} from 'naive-ui'
import { FilterOutline } from '@vicons/ionicons5'
import type { DataTableColumns, PaginationProps } from 'naive-ui'
import BaseBadge from '../atoms/BaseBadge.vue'
import CurrencyDisplay from '../atoms/CurrencyDisplay.vue'
import { transferService } from '../../services/transferService'
import type { TransferScheduleResponse } from '../../types/transfer'

const router = useRouter()

const data = ref<TransferScheduleResponse[]>([])
const loading = ref(false)
const error = ref('')
const filterDrawerOpen = ref(false)

const filters = reactive({ sourceAccount: '', targetAccount: '' })
const appliedFilters = reactive({ sourceAccount: '', targetAccount: '' })

const hasActiveFilters = ref(false)

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
      sourceAccount: appliedFilters.sourceAccount || undefined,
      targetAccount: appliedFilters.targetAccount || undefined,
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
  appliedFilters.sourceAccount = filters.sourceAccount
  appliedFilters.targetAccount = filters.targetAccount
  hasActiveFilters.value = !!(filters.sourceAccount || filters.targetAccount)
  pagination.page = 1
  filterDrawerOpen.value = false
  fetchData()
}

function clearFilters() {
  filters.sourceAccount = ''
  filters.targetAccount = ''
  appliedFilters.sourceAccount = ''
  appliedFilters.targetAccount = ''
  hasActiveFilters.value = false
  pagination.page = 1
  filterDrawerOpen.value = false
  fetchData()
}

const mobilePage = computed({
  get: () => pagination.page ?? 1,
  set: (val: number) => {
    pagination.page = val
    fetchData()
  },
})

onMounted(fetchData)
</script>

<template>
  <div class="flex flex-col gap-4">
    <!-- Toolbar -->
    <div class="flex items-center justify-between">
      <n-button
        :type="hasActiveFilters ? 'primary' : 'default'"
        @click="filterDrawerOpen = true"
      >
        <template #icon>
          <n-icon><filter-outline /></n-icon>
        </template>
        Filtrar
        <span v-if="hasActiveFilters" class="ml-1 text-xs opacity-80">(ativo)</span>
      </n-button>

      <n-button v-if="hasActiveFilters" quaternary @click="clearFilters">
        Limpar filtros
      </n-button>
    </div>

    <n-alert v-if="error" type="error">{{ error }}</n-alert>

    <n-spin :show="loading">
      <!-- Desktop: tabela -->
      <div class="hidden md:block">
        <n-data-table
          remote
          :columns="columns"
          :data="data"
          :pagination="pagination"
          :bordered="true"
          :scroll-x="800"
        />
      </div>

      <!-- Mobile: cards -->
      <div class="flex flex-col gap-3 md:hidden">
        <n-card
          v-for="transfer in data"
          :key="transfer.id"
          size="small"
          hoverable
          class="cursor-pointer"
          @click="router.push({ name: 'transfer-detail', params: { id: transfer.id } })"
        >
          <div class="flex items-start justify-between gap-2 mb-3">
            <div class="flex flex-col gap-0.5">
              <span class="text-xs text-gray-400">Origem → Destino</span>
              <span class="font-medium text-sm">
                {{ transfer.sourceAccount }} → {{ transfer.targetAccount }}
              </span>
            </div>
            <base-badge :status="transfer.status" />
          </div>

          <div class="grid grid-cols-2 gap-y-2 gap-x-4 text-sm">
            <div class="flex flex-col">
              <span class="text-xs text-gray-400">Valor</span>
              <currency-display :value="transfer.transferAmount" :highlight="true" />
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-gray-400">Taxa</span>
              <currency-display :value="transfer.feeAmount" />
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-gray-400">Data transferência</span>
              <span>{{ formatDate(transfer.transferDate) }}</span>
            </div>
            <div class="flex flex-col">
              <span class="text-xs text-gray-400">Data agendamento</span>
              <span>{{ formatDate(transfer.schedulingDate) }}</span>
            </div>
          </div>
        </n-card>

        <div v-if="!loading && data.length === 0" class="text-center text-gray-400 py-8">
          Nenhum agendamento encontrado.
        </div>

        <n-pagination
          v-model:page="mobilePage"
          :page-count="Math.ceil((pagination.itemCount ?? 0) / (pagination.pageSize ?? 10))"
          class="justify-center"
        />
      </div>
    </n-spin>

    <!-- Filter drawer -->
    <n-drawer v-model:show="filterDrawerOpen" placement="right" :width="300">
      <n-drawer-content title="Filtros" closable>
        <div class="flex flex-col gap-4">
          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Conta origem</span>
            <n-input
              v-model:value="filters.sourceAccount"
              placeholder="Ex: 1234567890"
              maxlength="10"
              clearable
              @keyup.enter="applyFilters"
            />
          </div>

          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Conta destino</span>
            <n-input
              v-model:value="filters.targetAccount"
              placeholder="Ex: 0987654321"
              maxlength="10"
              clearable
              @keyup.enter="applyFilters"
            />
          </div>
        </div>

        <template #footer>
          <n-space justify="end" class="w-full">
            <n-button @click="clearFilters">Limpar</n-button>
            <n-button type="primary" @click="applyFilters">Aplicar</n-button>
          </n-space>
        </template>
      </n-drawer-content>
    </n-drawer>
  </div>
</template>
