<script setup lang="ts">
import { computed } from 'vue'
import { NCard } from 'naive-ui'
import { useTransferScheduleStore } from '../../stores/transferScheduleStore'
import StepIndicator from '../molecules/StepIndicator.vue'
import SourceAccountStep from './wizard/SourceAccountStep.vue'
import TargetAccountStep from './wizard/TargetAccountStep.vue'
import AmountStep from './wizard/AmountStep.vue'
import DateStep from './wizard/DateStep.vue'
import SimulationStep from './wizard/SimulationStep.vue'
import SuccessStep from './wizard/SuccessStep.vue'

const store = useTransferScheduleStore()

const WIZARD_STEPS = [
  { title: 'Conta Origem' },
  { title: 'Conta Destino' },
  { title: 'Valor' },
  { title: 'Data' },
  { title: 'Simulação' },
]

const stepComponents = [
  SourceAccountStep,
  TargetAccountStep,
  AmountStep,
  DateStep,
  SimulationStep,
  SuccessStep,
]

const currentStepComponent = computed(() => stepComponents[store.step - 1])
</script>

<template>
  <n-card class="max-w-2xl mx-auto w-full">
    <step-indicator
      v-if="store.step <= WIZARD_STEPS.length"
      :current="store.step"
      :steps="WIZARD_STEPS"
      class="mb-8"
    />
    <component :is="currentStepComponent" />
  </n-card>
</template>
