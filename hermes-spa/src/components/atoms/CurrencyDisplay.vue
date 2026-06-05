<script setup lang="ts">
interface Props {
  value: number
  highlight?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  highlight: false,
})

const formatted = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
}).format(props.value)
</script>

<template>
  <span :class="props.highlight ? 'font-semibold text-primary' : ''">
    {{ formatted }}
  </span>
</template>
