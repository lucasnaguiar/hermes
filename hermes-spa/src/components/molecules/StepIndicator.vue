<script setup lang="ts">
import { NSteps, NStep } from 'naive-ui'

interface Step {
  title: string
  description?: string
}

interface Props {
  current: number
  steps: Step[]
}

const props = defineProps<Props>()
</script>

<template>
  <n-steps :current="props.current" size="small">
    <n-step
      v-for="(step, index) in props.steps"
      :key="index"
      :title="step.title"
      :description="step.description"
    />
  </n-steps>
</template>
