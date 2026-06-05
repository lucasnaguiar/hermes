<script setup lang="ts">
import { NAlert, NSpace } from 'naive-ui'
import { useRouter } from 'vue-router'
import { useTransferScheduleStore } from '../../../stores/transferScheduleStore'
import BaseButton from '../../atoms/BaseButton.vue'

const router = useRouter()
const store = useTransferScheduleStore()

function newSchedule() {
  store.reset()
}
</script>

<template>
  <div class="flex flex-col items-center gap-6 py-6">
    <n-alert
      type="success"
      title="Transferência agendada com sucesso!"
      :bordered="false"
      class="w-full"
    />
    <n-space>
      <base-button variant="primary" @click="router.push({ name: 'transfers' })">
        Ver agendamentos
      </base-button>
      <base-button @click="newSchedule">Novo agendamento</base-button>
    </n-space>
  </div>
</template>
