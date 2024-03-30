package com.shiyixi.ojbackenduserservice.controller.inner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shiyixi.ojbackendmodel.entity.User;
import com.shiyixi.ojbackendserviceclient.service.UserFeignClient;
import com.shiyixi.ojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 该服务仅内部调用
 */
@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {
    @Resource
    private UserService userService;
    /**
     * 根据用户 id 获取用户
     * @param userId 用户 id
     * @return 用户
     */
    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam("userId") long userId) {
        return userService.getById(userId);
    }

    /**
     * 根据 ids 获取用户列表
     * @param idList id 列表
     * @return 用户信息列表
     */
    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList) {
        return userService.listByIds(idList);
    }

    /**
     * 根据用户名称获取用户对象
     * @param userName 用户名称
     * @return 用户对象
     */
    @Override
    @GetMapping("/get/username")
    public User getUserByUserName(@RequestParam("userName") String userName) {
        return userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUserName, userName));
    }
}
