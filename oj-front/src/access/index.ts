import router from "@/router";
import store from "@/store";
import ACCESS_ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";

router.beforeEach(async (to, from, next) => {
  // 自动登录
  let loginUser = store.state.user.loginUser;
  if (
    !loginUser ||
    !loginUser.userRole ||
    loginUser.userRole === ACCESS_ENUM.NOT_LOGON
  ) {
    // 登录用户登陆了成功后，再执行后续代码
    console.log("登录中...");
    await store.dispatch("user/getLoginUser");
    // 重新刷新值
    loginUser = store.state.user.loginUser;
    console.log("登录成功...");
  }

  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGON; // 获取页面的使用权限
  if (needAccess !== ACCESS_ENUM.NOT_LOGON) {
    // 如果未登录，则跳转到登录页面
    console.log("信息校验...");
    console.log(loginUser);
    console.log(loginUser.userRole);
    if (
      !loginUser ||
      !loginUser.userRole ||
      loginUser.userRole === ACCESS_ENUM.NOT_LOGON
    ) {
      console.log("未登录、跳转登录...");
      next(`/user/login?redirect=${to.fullPath}`);
      return;
    }
    // 如果已登录，则校验权限：权限不足则跳转到无权限页面
    if (!checkAccess(loginUser, needAccess)) {
      next("/noAuth");
      return;
    }
  }

  next();
});
