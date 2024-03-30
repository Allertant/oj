<template>
  {{
    JSON.stringify({ a: 1, b: 2 }, (k, v) => {
      return k + " " + k[v] + "\n";
    })
  }}
  <div id="questionSubmitView">
    <a-form :model="searchParams" layout="inline">
      <a-form-item field="title" label="编程语言" style="min-width: 280px">
        <a-select
          v-model="searchParams.language"
          :style="{ width: '160px' }"
          placeholder="请选择语言"
          allow-clear
        >
          <a-option>java</a-option>
          <a-option>cpp</a-option>
          <a-option>go</a-option>
          <a-option>html</a-option>
        </a-select>
      </a-form-item>
      <!--      <a-form-item field="questionId" label="题号 id" style="min-width: 280px">-->
      <!--        <a-input-->
      <!--          v-model="searchParams.questionId"-->
      <!--          placeholder="请输入题号 id"-->
      <!--        />-->
      <!--      </a-form-item>-->
      <a-form-item field="status" label="提交状态" style="min-width: 240px">
        <a-select
          v-model="searchParams.status"
          :style="{ width: '160px' }"
          placeholder="请选择执行状态"
          :field-names="questionSubmitStatusEnumToNum"
          :options="options"
          allow-clear
        >
        </a-select>
      </a-form-item>
      <!--      <a-form-item field="useName" label="用户名称" style="min-width: 240px">-->
      <!--        <a-input v-model="searchParams.userName" placeholder="请输入用户名称" />-->
      <!--      </a-form-item>-->
      <a-form-item>
        <icon-refresh
          style="color: blue"
          size="30"
          @click="refreshLoadData()"
        />
      </a-form-item>
    </a-form>
    <a-divider size="0" />
    <a-table
      :columns="columns"
      :data="dataList"
      :pagination="{
        pageSize: searchParams.pageSize,
        current: searchParams.current,
        total,
        showTotal: true,
      }"
      :loading="table.loading"
      @page-change="onPageChange"
    >
      <template #createTime="{ record }">
        {{ moment(record.createTime).format("YYYY-MM-DD") }}
      </template>
      <template #judgeInfo="{ record }">
        <div v-html="record.judgeInfo"></div>
      </template>
      <template #status="{ record }">
        {{ questionSubmitStatusEnum[record.status] }}
      </template>
      <template #user="{ record }">
        {{ record.userVO?.userName }}
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref, watchEffect } from "vue";
import {
  QuestionControllerService,
  QuestionSubmitQueryRequest,
} from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import moment from "moment";

const dataList = ref([]);
const total = ref(0);
const searchParams = ref<QuestionSubmitQueryRequest>({
  language: "",
  userId: undefined,
  questionId: undefined,
  status: undefined,
  pageSize: 10,
  current: 1,
});

const table = ref({
  isloading: false,
});

const questionSubmitStatusEnum = ref(["等待中", "判题中", "成功", "失败"]);
const questionSubmitStatusEnumToNum = { value: "status", label: "text" };
const options = reactive([
  {
    status: 0,
    text: questionSubmitStatusEnum.value[0],
  },
  {
    status: 1,
    text: questionSubmitStatusEnum.value[1],
  },
  {
    status: 2,
    text: questionSubmitStatusEnum.value[2],
  },
  {
    status: 3,
    text: questionSubmitStatusEnum.value[3],
  },
]);
const refreshLoadData = async () => {
  table.value.isloading = true;
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    }
  );
  table.value.isloading = false;
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
    message.success("刷新成功");
  } else {
    message.error("刷新失败，" + res.message);
  }
};
const loadData = async () => {
  table.value.isloading = true;
  const res = await QuestionControllerService.listQuestionSubmitByPageUsingPost(
    {
      ...searchParams.value,
      sortField: "createTime",
      sortOrder: "descend",
    }
  );
  table.value.isloading = false;
  if (res.code === 0) {
    dataList.value = res.data.records;
    total.value = res.data.total;
    // message.success("加载成功");
  } else {
    message.error("加载失败，" + res.message);
  }
};

// const parse_json_string = (json: JudgeInfo) => {
//   const a = Object.keys(json).map((key: keyof JudgeInfo) => {
//     return `<p style="padding-left: 16px">${key}:&nbsp;&nbsp;&nbsp;&nbsp;  ${json[key]}</p>`;
//   });
//   return `{${a.join("")}}`;
// };

// 监听函数中的变量，如果其中的变量发生了改变，则会重新执行这个函数
watchEffect(() => {
  loadData();
});
onMounted(() => {
  // loadData();
});
const columns = [
  {
    title: "提交号",
    dataIndex: "id",
  },
  {
    title: "编程语言",
    dataIndex: "language",
  },
  {
    title: "判题信息",
    slotName: "judgeInfo",
  },
  {
    title: "判题状态",
    slotName: "status",
  },
  {
    title: "题目 id",
    dataIndex: "questionId",
  },
  {
    title: "提交者",
    slotName: "user",
  },
  {
    title: "创建时间",
    slotName: "createTime",
  },
];
const onPageChange = (page: number) => {
  searchParams.value = {
    ...searchParams.value,
    current: page,
  };
};
/**
 * 提交数据
 */
const doSubmit = () => {
  // 重置页号，自动触发查询数据
  searchParams.value = {
    ...searchParams.value,
    current: 1,
  };
};
</script>

<style scoped>
#questionSubmitView {
  max-width: 1280px;
  margin: 0 auto;
}
</style>
