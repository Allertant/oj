<template>
  <div id="addQuestionView">
    <h2>{{ updatePage ? "修改题目" : "添加题目" }}</h2>
    <a-form :model="form" label-align="left">
      <a-form-item field="title" label="标题">
        <a-input v-model="form.title" placeholder="请输入标题" />
      </a-form-item>
      <a-form-item field="tags" label="标签">
        <a-input-tag
          v-model="form.tags"
          allow-clear
          placeholder="请选择标签"
        ></a-input-tag>
      </a-form-item>
      <a-form-item field="answer" label="答案">
        <md-editor
          :handle-change="answerHandler"
          :value="form.answer"
          style=""
        />
      </a-form-item>
      <a-form-item field="content" label="内容">
        <md-editor :handle-change="contentHandler" :value="form.content" />
      </a-form-item>
      <a-form-item :content-flex="false" :merge-prop="false" label="判题配置">
        <a-space direction="vertical" style="min-width: 480px">
          <a-form-item field="judgeConfig.timeLimit" label="时间限制">
            <a-input-number
              v-model="form.judgeConfig.timeLimit"
              mode="button"
              placeholder="请输入时间限制"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.stackLimit" label="堆栈限制">
            <a-input-number
              v-model="form.judgeConfig.stackLimit"
              mode="button"
              placeholder="请输入堆栈限制"
              size="large"
            />
          </a-form-item>
          <a-form-item field="judgeConfig.memoryLimit" label="内存限制">
            <a-input-number
              v-model="form.judgeConfig.memoryLimit"
              mode="button"
              placeholder="请输入内存限制"
              size="large"
            />
          </a-form-item>
        </a-space>
      </a-form-item>
      <a-form-item :content-flex="false" label="测试用例">
        <a-form-item
          v-for="(judgeCaseItem, index) of form.judgeCase"
          :key="index"
          :field="`form.judgeCase[${index}].input`"
          no-style
        >
          <a-space direction="vertical" style="min-width: 480px">
            <a-form-item
              :key="index"
              :field="`form.judgeCase[${index}].input`"
              :label="`输入用例-${index}`"
            >
              <a-input
                v-model="judgeCaseItem.input"
                placeholder="请输入测试输入用例"
              />
            </a-form-item>
            <a-form-item
              :key="index"
              :field="`form.judgeCase[${index}].input`"
              :label="`输出用例-${index}`"
            >
              <a-input
                v-model="judgeCaseItem.output"
                placeholder="请输入测试输出用例"
              />
            </a-form-item>
            <a-button
              status="danger"
              style="margin-bottom: 12px"
              @click="handleDelete(index)"
            >
              删除
            </a-button>
          </a-space>
        </a-form-item>
        <div style="margin-top: 32px">
          <a-button status="success" type="outline" @click="handleAdd">
            新增测试用例
          </a-button>
        </div>
      </a-form-item>
      <div style="margin-top: 16px"></div>
      <a-form-item>
        <a-button style="min-width: 200px" type="primary" @click="doSubmit"
          >提交
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import { QuestionControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRoute } from "vue-router";

let form = ref({
  title: "",
  tags: [],
  answer: "",
  content: "",
  judgeCase: [
    {
      input: "",
      output: "",
    },
  ],
  judgeConfig: {
    memoryLimit: 1000,
    stackLimit: 1000,
    timeLimit: 1000,
  },
});
const answerHandler = (value: string) => {
  form.value.answer = value;
};

const route = useRoute();

// 判断是否为更新操作
const updatePage = route.path.includes("update");

const contentHandler = (value: string) => {
  form.value.content = value;
};
const handleAdd = () => {
  form.value.judgeCase.push({
    input: "",
    output: "",
  });
};

const loadData = async () => {
  let id = route.query.id;
  if (!id) return;
  const res = await QuestionControllerService.getQuestionByIdUsingGet(
    id as any
  );
  if (res.code === 0) {
    form.value = res.data as any;
    // 转换数据 —— tags
    if (!form.value.tags) {
      form.value.tags = [];
    } else {
      // JSON 格式转换
      form.value.tags = JSON.parse(form.value.tags as any);
    }
    // 转换数据 —— judgeConfig
    if (!form.value.judgeConfig) {
      form.value.judgeConfig = {
        memoryLimit: 1000,
        stackLimit: 1000,
        timeLimit: 1000,
      };
    } else {
      form.value.judgeConfig = JSON.parse(form.value.judgeConfig as any);
    }
    // 转换数据 —— judgeCase
    if (!form.value.judgeCase) {
      form.value.judgeCase = [
        {
          input: "",
          output: "",
        },
      ];
    } else {
      form.value.judgeCase = JSON.parse(form.value.judgeCase as any);
    }
  } else {
    message.error("加载失败，" + res.message);
  }
};

onMounted(() => {
  if (updatePage) loadData();
});

const handleDelete = (index: number) => {
  form.value.judgeCase.splice(index, 1);
};
const doSubmit = async () => {
  if (updatePage) {
    // 更新
    const res = await QuestionControllerService.updateQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("更新成功");
    } else {
      message.error("更新失败：" + res.message);
    }
  } else {
    // 创建
    const res = await QuestionControllerService.addQuestionUsingPost(
      form.value
    );
    if (res.code === 0) {
      message.success("创建成功");
    } else {
      message.error("创建失败：" + res.message);
    }
  }
};
</script>

<style scoped>
#addQuestionView {
}
</style>
