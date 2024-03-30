package com.shiyixi.ojbackenduserservice.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiyixi.ojbackendmodel.entity.User;
import org.apache.ibatis.annotations.CacheNamespace;

/**
 * 用户数据库操作
 *
 * @author shiyixi

 */
@CacheNamespace
public interface UserMapper extends BaseMapper<User> {

}




