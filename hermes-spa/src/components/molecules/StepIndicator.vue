<script setup lang="ts">
import { computed } from 'vue'
import { NSteps, NStep, NProgress } from 'naive-ui'

interface Step {
  title: string
  description?: string
}

interface Props {
  current: number
  steps: Step[]
}

const props = defineProps<Props>()

const currentStep = computed(() => props.steps[props.current - 1])
const progressPercent = computed(() => Math.round((props.current / props.steps.length) * 100))
</script>

<template>
  <!-- Desktop: steps horizontais -->
  <div class="hidden md:block">
    <n-steps :current="props.current" size="small">
      <n-step
        v-for="(step, index) in props.steps"
        :key="index"
        :title="step.title"
        :description="step.description"
      />
    </n-steps>
  </div>

  <!-- Mobile: progresso compacto -->
  <div class="flex flex-col gap-2 md:hidden">
    <div class="flex items-center justify-between text-sm">
      <span class="font-medium">{{ currentStep?.title }}</span>
      <span class="text-gray-400">{{ props.current }} de {{ props.steps.length }}</span>
    </div>
    <n-progress
      type="line"
      :percentage="progressPercent"
      :show-indicator="false"
      :height="6"
      border-radius="4px"
    />
  </div>
</template>
