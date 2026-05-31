<script setup>
import AlertMessage from './AlertMessage.vue'
</script>

<template>
  <AlertMessage variant="warning">
    <slot>Masz dostęp tylko do podglądu — brak uprawnień do dodawania i edycji w tym module.</slot>
  </AlertMessage>
</template>
