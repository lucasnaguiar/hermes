<script setup lang="ts">
import { NFormItem, NInput } from 'naive-ui'
import type { InputProps } from 'naive-ui'

interface Props {
  modelValue: string
  label?: string
  placeholder?: string
  errorMessage?: string
  maxlength?: number
  disabled?: boolean
  type?: InputProps['type']
}

const props = withDefaults(defineProps<Props>(), {
  label: '',
  placeholder: '',
  errorMessage: '',
  disabled: false,
  type: 'text',
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()
</script>

<template>
  <n-form-item
    :label="props.label"
    :feedback="props.errorMessage"
    :validation-status="props.errorMessage ? 'error' : undefined"
  >
    <n-input
      :value="props.modelValue"
      :placeholder="props.placeholder"
      :maxlength="props.maxlength"
      :disabled="props.disabled"
      :type="props.type"
      @update:value="emit('update:modelValue', $event)"
    />
  </n-form-item>
</template>
