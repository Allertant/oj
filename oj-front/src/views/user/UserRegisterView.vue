<template>
  <div id="userLoginView">
    <h2 style="margin-bottom: 16px">注册</h2>
    <a-form
      :model="form"
      auto-label-width="auto-label-width"
      label-align="left"
      style="max-width: 480px; margin: auto"
      @submit="handleSubmit"
    >
      <a-form-item field="userAccount" label="账户" tooltip="">
        <a-input v-model="form.userAccount" placeholder="请输入邮箱" />
      </a-form-item>
      <a-form-item field="userAccount" label="验证码" tooltip="">
        <a-button
          type="primary"
          style="margin-right: 30px"
          @click="getVerifyCode"
          :disabled="isDisable"
          >{{ buttonText }}
        </a-button>
        <a-input v-model="form.code" placeholder="请输入验证码" />
      </a-form-item>
      <a-form-item field="userName" label="用户名" tooltip="">
        <a-input v-model="form.userName" placeholder="请输入用户名" />
      </a-form-item>
      <a-form-item field="userPassword" label="密码">
        <a-input-password
          v-model="form.userPassword"
          placeholder="请输入密码"
          tooltip="密码不得少于8位"
        />
      </a-form-item>
      <a-form-item field="userPassword" label="重复密码">
        <a-input-password
          v-model="form.checkPassword"
          placeholder="请再次输入密码"
          tooltip="密码不得少于8位"
        />
      </a-form-item>
      <a-form-item style="justify-content: center">
        <div style="width: 100%; display: flex; justify-content: space-around">
          <a-button html-type="submit" style="width: 120px" type="primary"
            >注册
          </a-button>
          <div>
            <a-link
              style="line-height: 32px; text-decoration: underline"
              @click="router.replace('/user/login')"
              >登录
            </a-link>
          </div>
        </div>
      </a-form-item>
    </a-form>
  </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from "vue";
import { UserControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";

const form = reactive({
  checkPassword: "",
  userAccount: "",
  userName: "",
  userPassword: "",
  type: 1,
  code: "",
});

const buttonText = ref("获取验证码");
const isDisable = ref(false);

const router = useRouter();

const validateEmail = (email: string): boolean => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

const handleSubmit = async () => {
  if (
    !form.userAccount ||
    !form.userPassword ||
    !form.checkPassword ||
    !form.userName
  )
    return message.error("表单不全");
  if (!validateEmail(form.userAccount)) return message.error("邮箱格式错误");
  if (form.checkPassword != form.checkPassword)
    return message.error("密码不一致");

  form.userAccount = form.userAccount.trim();
  form.userName = form.userName.trim();
  form.userPassword = form.userPassword.trim();
  form.code = form.code.trim();
  form.checkPassword = form.checkPassword.trim();

  if (form.userAccount.includes(" "))
    return message.error("账户名中不得包含空格");
  if (form.userName.includes(" ")) return message.error("用户名中不得包含空格");
  if (form.userPassword.includes(" "))
    return message.error("账户名中不得包含空格");
  if (form.code.includes(" ")) return message.error("验证码中不得包含空格");
  if (form.checkPassword.includes(" "))
    return message.error("校验密码中不得包含空格");

  form.type = 2;
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    message.success("注册成功");
    // 注册成功，跳转到登录页面
    router.push({
      path: "/user/login",
      replace: true,
    });
  } else {
    message.error("注册失败," + res.message);
  }
};
const getVerifyCode = async () => {
  if (!form.userAccount) return message.error("账户名为空");
  if (!validateEmail(form.userAccount)) return message.error("邮箱格式错误");
  // 验证邮箱格式
  form.type = 1;
  const res = await UserControllerService.userRegisterUsingPost(form);
  if (res.code === 0) {
    message.success("获取验证码成功");
    // 修改按钮状态
    isDisable.value = true;
  } else {
    message.error("获取验证码失败," + res.message);
  }
};
</script>
