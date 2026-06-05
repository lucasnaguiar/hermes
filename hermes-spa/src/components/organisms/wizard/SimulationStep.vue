<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { NSpin, NAlert, NSpace } from 'naive-ui'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import { transferService } from '../../../services/transferService'
import SimulateResultCard from '../../molecules/SimulateResultCard.vue'
import BaseButton from '../../atoms/BaseButton.vue'

const store = useTransferScheduleStore()

const loading = ref(false)
const simError = ref('')
const submitError = ref('')
const submitting = ref(false)

async function simulate() {
  loading.value = true
  simError.value = ''
  store.simulation = null
  try {
    store.simulation = await transferService.simulate(store.transferRequest!)
  } catch (e: unknown) {
    simError.value = e instanceof Error ? e.message : 'Erro ao simular transferência.'
  } finally {
    loading.value = false
  }
}

async function confirm() {
  submitting.value = true
  submitError.value = ''
  try {
    await transferService.schedule(store.transferRequest!)
    store.goToStep(6)
  } catch (e: unknown) {
    submitError.value = e instanceof Error ? e.message : 'Erro ao agendar transferência.'
  } finally {
    submitting.value = false
  }
}

onMounted(simulate)
</script>

<template>
  <div class="flex flex-col gap-4">
    <n-spin v-if="loading" class="self-center py-8" />

    <template v-else>
      <n-alert v-if="simError" type="error">{{ simError }}</n-alert>
      <simulate-result-card v-if="store.simulation" :simulation="store.simulation" />
      <n-alert v-if="submitError" type="error">{{ submitError }}</n-alert>

      <n-space justify="space-between">
        <base-button :disabled="submitting" @click="store.goToStep(4)">Voltar</base-button>
        <base-button
          v-if="store.simulation && !simError"
          variant="primary"
          :loading="submitting"
          @click="confirm"
        >
          Confirmar agendamento
        </base-button>
      </n-space>
    </template>
  </div>
</template>
