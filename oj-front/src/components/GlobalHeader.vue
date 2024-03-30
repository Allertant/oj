<template>
  <a-row id="globalHeader" :wrap="false" align="center">
    <a-col flex="auto">
      <a-menu
        :default-selected-keys="['1']"
        :selected-keys="selectKeys"
        mode="horizontal"
        @menu-item-click="doMenuItemClick"
      >
        <a-menu-item
          key="0"
          :style="{ padding: 0, marginRight: '38px' }"
          disabled
        >
          <div class="title-bar" @click="router.push('/')">
            <img alt="oj" class="logo" src="@/assets/logo.png" />
            <div class="title">在线 OJ</div>
          </div>
        </a-menu-item>
        <a-menu-item v-for="item in visibleRoutes" :key="item.path">
          {{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div>
        <a-dropdown @select="handleSelect">
          <a-avatar :style="{ backgroundColor: '#3370ff' }"
            >{{ store.state.user?.loginUser?.userName }}
          </a-avatar>
          <template #content>
            <a-doption
              >{{
                store.state.user?.loginUser?.userName === "未登录"
                  ? "登录"
                  : "退出登录"
              }}
            </a-doption>
          </template>
        </a-dropdown>
      </div>
    </a-col>
  </a-row>
  <div></div>
</template>

<script lang="ts" setup>
import { routes } from "@/router/routes";
import { useRoute, useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";
import { UserControllerService } from "../../generated";
import message from "@arco-design/web-vue/es/message";

const store = useStore();

const visibleRoutes = computed(() => {
  return routes.filter((item, index) => {
    // 根据菜单项是否隐藏返回菜单
    if (item.meta?.hideInMenu) {
      return false;
    }
    // 根据权限返回菜单
    return checkAccess(store.state.user.loginUser, item.meta?.access as string);
  });
});

const router = useRouter();
const route = useRoute();
// 默认页面
const selectKeys = ref(["/"]);
// 路由更新触发
router.afterEach((to) => {
  selectKeys.value = [to.path];
});
const doMenuItemClick = (key: string) => {
  router.push(key);
};
const handleSelect = async (v: string) => {
  if (v == "退出登录") {
    const res = await UserControllerService.userLogoutUsingPost();
    if (res.code === 0) {
      message.success("退出登录成功");
      // 请求获取用户数据
      await store.dispatch("user/getLoginUser");
      // 退出登录成功
      router.push("/");
    } else {
      message.error("退出登录失败," + res.message);
    }
  } else if (v == "登录") {
    router.push("/user/login?redirect=" + route.path);
  }
};
</script>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: #444;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
