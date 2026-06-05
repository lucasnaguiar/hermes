<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { NSkeleton, NAlert } from 'naive-ui'
import MainLayout from '../components/templates/MainLayout.vue'
import TransferDetailCard from '../components/molecules/TransferDetailCard.vue'
import BaseButton from '../components/atoms/BaseButton.vue'
import { transferService } from '../services/transferService'
import type { TransferScheduleResponse } from '../types/transfer'

const route = useRoute()

const transfer = ref<TransferScheduleResponse | null>(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  try {
    transfer.value = await transferService.findById(route.params.id as string)
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Agendamento não encontrado.'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <main-layout>
    <div class="max-w-2xl mx-auto w-full">
      <div class="flex items-center justify-between mb-6">
        <h1 class="text-2xl font-semibold">Detalhes do Agendamento</h1>
        <base-button @click="$router.back()">Voltar</base-button>
      </div>

      <!-- Loading skeleton -->
      <div v-if="loading" class="flex flex-col gap-3">
        <n-skeleton height="40px" width="100%" />
        <n-skeleton height="40px" width="100%" />
        <n-skeleton height="40px" width="100%" />
        <n-skeleton height="40px" width="75%" />
      </div>

      <!-- Erro -->
      <div v-else-if="error" class="flex flex-col gap-4">
        <n-alert type="error" :title="error" />
        <base-button @click="$router.push({ name: 'transfers' })">
          Ver todos os agendamentos
        </base-button>
      </div>

      <!-- Detalhe -->
      <transfer-detail-card v-else-if="transfer" :transfer="transfer" />
    </div>
  </main-layout>
</template>
