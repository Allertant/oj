<template>
  <div id="viewQuestionView">
    <a-row :gutter="[24, 24]">
      <a-col :md="12" :xs="24">
        <a-tabs default-active-key="1">
          <a-tab-pane key="1" title="题目">
            <a-card v-if="question" :title="question.title">
              <a-descriptions
                :column="{ xs: 1, md: 2, lg: 3 }"
                :data="question.judgeConfig"
                title="判题条件"
              >
                <a-descriptions-item label="时间限制（ms）">
                  {{ question.judgeConfig.timeLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="内存限制（KB）">
                  {{ question.judgeConfig.memoryLimit ?? 0 }}
                </a-descriptions-item>
                <a-descriptions-item label="堆栈限制（KB）">
                  {{ question.judgeConfig.stackLimit ?? 0 }}
                </a-descriptions-item>
              </a-descriptions>
              <md-viewer :value="question.content ?? ''" />
              <template #extra>
                <a-space wrap>
                  <a-tag
                    v-for="(tag, index) of question.tags"
                    :key="index"
                    color="green"
                    style=""
                  >
                    {{ tag }}
                  </a-tag>
                </a-space>
              </template>
            </a-card>
          </a-tab-pane>
          <a-tab-pane key="2" disabled title="评论区"> 评论</a-tab-pane>
          <a-tab-pane key="3" title="提交记录"> 提交记录</a-tab-pane>
        </a-tabs>
      </a-col>

      <a-col :md="12" :xs="24" style="box-shadow: #ddd 2px 2px 5px">
        <a-form :model="form" layout="inline">
          <a-form-item field="title" label="编程语言" style="min-width: 280px">
            <a-select
              v-model="form.language"
              :style="{ width: '320px' }"
              placeholder="请选择语言"
            >
              <a-option>java</a-option>
              <a-option>cpp</a-option>
              <a-option>go</a-option>
              <a-option>html</a-option>
            </a-select>
          </a-form-item>
        </a-form>
        <CodeEditor
          :handle-change="changeCode"
          :language="form.language"
          :value="form.code"
        />
        <a-divider size="0" />
        <a-button style="min-width: 200px" type="primary" @click="doSubmit">
          提交
        </a-button>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
import { defineProps, onMounted, ref, watch, withDefaults } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitAddRequest,
  QuestionVO,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import CodeEditor from "@/components/CodeEditor.vue";
import MdViewer from "@/components/MdViewer.vue";

interface Props {
  id: string;
}

const initCodes = ref({
  java:
    "import java.util.*;\n" +
    "public class Main {\n" +
    "    public static void main(String[] args) {\n" +
    "        Scanner scanner = new Scanner(System.in);\n" +
    "        Integer a = scanner.nextInt();\n" +
    "        Integer b = scanner.nextInt();\n" +
    "        System.out.println(a + b);\n" +
    "    }\n" +
    "}\n",
  cpp:
    "#include<iostream>\n" +
    "using namespace std;\n" +
    "int main() {\n" +
    '    cout << "hello,world";\n' +
    "}",
  go:
    "package main\n" +
    'import "fmt"\n' +
    'import . "nc_tools"\n' +
    "/*\n" +
    " * type ListNode struct{\n" +
    " *   Val int\n" +
    " *   Next *ListNode\n" +
    " * }\n" +
    " */\n" +
    "\n" +
    "/**\n" +
    " * 代码中的类名、方法名、参数名已经指定，请勿修改，直接返回方法规定的值即可\n" +
    " *\n" +
    " * \n" +
    " * @param head ListNode类 \n" +
    " * @return ListNode类\n" +
    "*/\n" +
    "func ReverseList( head *ListNode ) *ListNode {\n" +
    "    // write code here\n" +
    "}",
  html:
    "<!DOCTYPE html>\n" +
    '<html lang="en">\n' +
    "<head>\n" +
    '    <meta charset="UTF-8">\n' +
    '    <meta name="viewport" content="width=device-width, initial-scale=1.0">\n' +
    "    <title>Document</title>\n" +
    "</head>\n" +
    "<body>\n" +
    "</body>\n" +
    "</html>",
});

const props = withDefaults(defineProps<Props>(), {
  id: () => "",
});
const question = ref<QuestionVO>();
const loadData = async () => {
  const res = await QuestionControllerService.getQuestionVoByIdUsingGet(
    props.id as any
  );
  if (res.code === 0) {
    question.value = res.data;
    message.success("加载成功");
  } else {
    message.error("加载失败，" + res.message);
  }
};
onMounted(() => {
  loadData();
});
const form = ref<QuestionSubmitAddRequest>({
  language: "java",
  code: "",
});
/**
 * 提交代码
 */
const doSubmit = async () => {
  if (!form.value.language || !form.value.code || !question.value?.id)
    return message.warning("参数不全");
  const res = await QuestionControllerService.doQuestionSubmitUsingPost({
    ...form.value,
    questionId: question.value?.id,
  });
  if (res.code === 0) {
    message.success("提交成功");
  } else {
    message.error("提交失败，" + res.message);
  }
};
/**
 * 每次输入代码时，都将这些代码保存好
 */
const setSavedCode = (language: string | undefined) => {
  if (language === "java") {
    initCodes.value.java = form.value.code;
  } else if (language === "cpp") {
    initCodes.value.cpp = form.value.code;
  } else if (language === "go") {
    initCodes.value.go = form.value.code;
  } else if (language === "html") {
    initCodes.value.html = form.value.code;
  }
};
/**
 * 切换语言时，读取保存好的代码
 */
const getSavedCode = (language: string) => {
  // const language = form.value.language;
  if (language === "java") {
    console.log(initCodes.value.java);
    form.value.code = initCodes.value.java;
  } else if (language === "cpp") {
    console.log(initCodes.value.cpp);
    form.value.code = initCodes.value.cpp;
  } else if (language === "go") {
    console.log(initCodes.value.go);
    form.value.code = initCodes.value.go;
  } else if (language === "html") {
    console.log(initCodes.value.html);
    form.value.code = initCodes.value.html;
  }
};
const changeCode = (value: string) => {
  form.value.code = value;
};
watch(
  () => form.value.language,
  (val, preVal) => {
    if (val !== preVal) {
      // 保存旧的代码
      setSavedCode(preVal);
      // 加载新的代码
      console.log("替换代码," + val);
      getSavedCode(val);
    }
  },
  {
    immediate: true,
  }
);
</script>

<style>
#viewQuestionView {
  max-width: 1400px;
  margin: 0 auto;
}

.arco-space-horizontal .arco-space-item {
  margin-bottom: 0px !important;
}
</style>
