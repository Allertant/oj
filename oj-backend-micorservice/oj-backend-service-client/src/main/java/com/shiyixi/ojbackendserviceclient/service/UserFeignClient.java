package com.shiyixi.ojbackendserviceclient.service;

import com.shiyixi.ojbackendcommon.common.ErrorCode;
import com.shiyixi.ojbackendcommon.exception.BusinessException;
import com.shiyixi.ojbackendmodel.entity.User;
import com.shiyixi.ojbackendmodel.enums.UserRoleEnum;
import com.shiyixi.ojbackendmodel.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;

import static com.shiyixi.ojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 *
 * @author shiyixi
 */
@FeignClient(name = "oj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient {

    /**
     * 根据 id 获取用户信息
     * @param userId 用户 id
     * @return 用户信息
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") long userId);

    /**
     * 根据 ids 获取用户列表
     * @param idList id 列表
     * @return 用户列表
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList") Collection<Long> idList);

    /**
     * 根据用户名称获取用户对象
     * @param userName 用户名称
     * @return 用户对象
     */
    @GetMapping("/get/username")
    User getUserByUserName(@RequestParam("userName") String userName);



    /**
     * 以下方法很简单，可以自主实现，无需远程调用，自己实现即可
     */

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }


    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user) {
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

}
