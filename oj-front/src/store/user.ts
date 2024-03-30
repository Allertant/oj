// initial state
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: {
    loginUser: {
      userName: "未登录",
    },
  },
  actions: {
    async getLoginUser({ commit, state }, payload) {
      // 从远程请求获取登录信息
      const res = await UserControllerService.getLoginUserUsingGet();
      // 已登录状态
      if (res.code === 0) {
        commit("updateUser", res.data);
      } else {
        // 未登录状态
        commit("updateUser", {
          userName: "未登录",
          useRole: ACCESS_ENUM.NOT_LOGON,
        });
      }
    },
  },
  mutations: {
    updateUser(state, payLoad) {
      console.log(payLoad);
      state.loginUser = payLoad;
    },
  },
} as StoreOptions<any>;
