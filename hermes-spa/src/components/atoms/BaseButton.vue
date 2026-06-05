<script setup lang="ts">
import { NButton } from 'naive-ui'
import type { ButtonProps } from 'naive-ui'

interface Props {
  variant?: 'primary' | 'default' | 'danger'
  size?: ButtonProps['size']
  loading?: boolean
  disabled?: boolean
  block?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'default',
  size: 'medium',
  loading: false,
  disabled: false,
  block: false,
})

const typeMap: Record<NonNullable<Props['variant']>, ButtonProps['type']> = {
  primary: 'primary',
  default: 'default',
  danger: 'error',
}
</script>

<template>
  <n-button
    :type="typeMap[props.variant]"
    :size="props.size"
    :loading="props.loading"
    :disabled="props.disabled"
    :block="props.block"
  >
    <slot />
  </n-button>
</template>
