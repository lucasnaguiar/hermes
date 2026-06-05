<script setup lang="ts">
import { ref, computed, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { NLayoutHeader, NMenu, NButton, NDrawer, NDrawerContent, NIcon } from 'naive-ui'
import { MenuOutline, CloseOutline } from '@vicons/ionicons5'
import type { MenuOption } from 'naive-ui'

const route = useRoute()
const router = useRouter()
const drawerOpen = ref(false)

const menuOptions: MenuOption[] = [
  { label: 'Agendar Transferência', key: 'schedule' },
  { label: 'Agendamentos', key: 'transfers' },
]

const activeKey = computed(() => route.name as string)

function handleSelect(key: string) {
  router.push({ name: key })
  drawerOpen.value = false
}

function renderIcon(icon: Component) {
  return () => h(NIcon, null, { default: () => h(icon) })
}
</script>

<template>
  <n-layout-header bordered class="px-4 md:px-8 flex items-center justify-between h-16">
    <span class="text-lg font-semibold tracking-wide">Hermes</span>

    <!-- Desktop menu -->
    <div class="hidden md:flex flex-1 justify-end">
      <n-menu
        :value="activeKey"
        :options="menuOptions"
        mode="horizontal"
        @update:value="handleSelect"
      />
    </div>

    <!-- Mobile hamburger -->
    <n-button
      class="flex md:hidden"
      quaternary
      circle
      @click="drawerOpen = true"
    >
      <template #icon>
        <n-icon size="22">
          <menu-outline />
        </n-icon>
      </template>
    </n-button>
  </n-layout-header>

  <!-- Mobile drawer -->
  <n-drawer v-model:show="drawerOpen" placement="right" :width="260">
    <n-drawer-content closable title="Menu">
      <n-menu
        :value="activeKey"
        :options="menuOptions"
        mode="vertical"
        @update:value="handleSelect"
      />
    </n-drawer-content>
  </n-drawer>
</template>
