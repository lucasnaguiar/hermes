<script setup lang="ts">
import { NLayout, NLayoutContent } from 'naive-ui'
import TheNavbar from '../organisms/TheNavbar.vue'
</script>

<template>
  <n-layout class="min-h-screen">
    <the-navbar />
    <n-layout-content class="px-4 py-6 md:px-8 md:py-10">
      <div class="max-w-5xl mx-auto w-full">
        <slot />
      </div>
    </n-layout-content>
  </n-layout>
</template>
