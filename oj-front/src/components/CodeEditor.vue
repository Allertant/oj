<template>
  <div
    id="codeEditor"
    ref="codeEditorRef"
    style="min-height: 400px; height: 70vh"
  ></div>
</template>

<script lang="ts" setup>
import * as monaco from "monaco-editor";
import { editor } from "monaco-editor";
import { defineProps, onMounted, ref, toRaw, watch, withDefaults } from "vue";

const codeEditorRef = ref();
const codeEditor = ref();

interface Props {
  value: string;
  language: string;
  handleChange: (v: string) => void;
}

const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  handleChange: (v: string) => {
    console.log(v);
  },
  mode: () => "split",
  language: () => "java",
});

watch(
  () => props.language,
  () => {
    // codeEditor 示例已经存在
    if (codeEditor.value) {
      // 重新设置编程语言
      monaco.editor.setModelLanguage(
        toRaw(codeEditor.value.getModel()),
        props.language
      );
      // 重新设置代码
      editor.getModels()[0]?.setValue(props.value);
    }
  }
);

onMounted(() => {
  if (!codeEditorRef.value) {
    return;
  }

  // 绑定对象
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: true,
    minimap: {
      enabled: true,
    },
    readOnly: false,
    theme: "vs-dark",
  });
  // 绑定时间监听器
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});
</script>
