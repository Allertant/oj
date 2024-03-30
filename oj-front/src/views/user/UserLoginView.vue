<template>
  <div id="userLoginView">
    <h2 style="margin-bottom: 16px">登录</h2>
    <a-form
      :model="form"
      auto-label-width="auto-label-width"
      label-align="left"
      style="max-width: 480px; margin: auto"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账户" tooltip="">
        <a-input v-model="form.userAccount" placeholder="请输入邮箱地址" />
      </a-form-item>
      <a-form-item field="userPassword" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
          tooltip="密码不得少于8位"
        />
      </a-form-item>
      <a-form-item>
        <div style="width: 100%; display: flex; justify-content: space-around">
          <a-button html-type="submit" style="width: 120px" type="primary"
            >提交
          </a-button>
          <div>
            <a-link
              style="line-height: 32px; text-decoration: underline"
              @click="router.replace('/user/register')"
              >注册
            </a-link>
          </div>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive } from "vue";
import { UserControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

const form = reactive({
  userAccount: "",
  userPassword: "",
});

const router = useRouter();
const store = useStore();

const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};
const handleSubmit = async () => {
  if (!form.userPassword || !form.userAccount) return message.error("参数不全");
  if (form.userAccount.length < 4) return message.error("账户名不得少于4位");
  if (!validateEmail(form.userAccount)) return message.error("账户名格式错误");
  if (form.userPassword.length < 8) return message.error("密码不得少于8位");
  form.userAccount = form.userAccount.trim();
  form.userPassword = form.userPassword.trim();
  if (form.userAccount.includes(" "))
    return message.error("账户中不得包含空格");
  if (form.userPassword.includes(" "))
    return message.error("密码中不得包含空格");
  const res = await UserControllerService.userLoginUsingPost(form);
  if (res.code === 0) {
    message.success("登录成功");
    // 请求获取用户数据
    store.dispatch("user/getLoginUser");
    // 登录成功，直接返回主页
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("登录失败," + res.message);
  }
};
</script>
