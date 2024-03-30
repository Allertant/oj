<template>
  <!--  <MdEditor :value="value" :handle-change="onChange" />-->
  <CodeEditor :handle-change="onChange" :value="value" />
  {{ value }}
  <!--  <div class="home">This is a home page</div>-->
</template>

<script lang="ts" setup>
import { ref } from "vue";
import CodeEditor from "@/components/CodeEditor.vue";

const value = ref("");
const onChange = (v: string) => {
  value.value = v;
};
</script>
