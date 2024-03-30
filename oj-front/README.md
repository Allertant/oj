# oj-front

## Project setup

```
npm install
```

### Compiles and hot-reloads for development

```
npm run serve
```

### Compiles and minifies for production

```
npm run build
```

### Lints and fixes files

```
npm run lint
```

### Customize configuration

See [Configuration Reference](https://cli.vuejs.org/config/).

### 根据后台生成接口代码

```bash
openapi --input http://localhost:8081/api/v2/api-docs --output ./generated --client axios
```

### 如何复用页面

```text
1. 使用路由
2. 判断是否携带了请求参数
```

### 时间处理

```bash
npm i moment
```