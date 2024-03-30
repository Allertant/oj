package com.shiyixi.ojsanbox.security;


import java.security.Permission;

public class DefaultSecurityManager extends SecurityManager {
    /**
     * 默认检查所有的权限
     * @param perm   the requested permission.
     */
    @Override
    public void checkPermission(Permission perm) {
        System.out.println("默认做全局的权限限制");
        super.checkPermission(perm);
    }


}
