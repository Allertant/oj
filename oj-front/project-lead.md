### 1. 同步后端接口

```bash
 openapi --input http://localhost:8081/api/v2/api-docs --output ./generated --client axios
```

### 2. 自定义模板 —— live Template 中针对指定文件进行设置

自定义模板变量

```text
camelCase(fileNameWithoutExtension())
```

模板语法

```text
<template>
  <div id="$ID$"></div>
</template>

<script setup lang="ts">
$END$
</script>

<style scoped>
#$ID$ {
}
</style>
```

### 3. 