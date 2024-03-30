<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view />
    </template>
    <template v-else>
      <basic-layout />
    </template>
  </div>
</template>

<style>
#app {
}
</style>
<script lang="ts" setup>
import BasicLayout from "@/layouts/BasicLayout.vue";
import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();
const doInit = () => {
  console.log("global init ...");
};

onMounted(() => {
  doInit();
});
</script>
