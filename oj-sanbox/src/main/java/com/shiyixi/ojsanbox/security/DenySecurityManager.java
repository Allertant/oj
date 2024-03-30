package com.shiyixi.ojsanbox.security;

import java.security.Permission;

public class DenySecurityManager extends SecurityManager{
    @Override
    public void checkPermission(Permission perm) {
        // super.checkPermission(perm);

        System.out.println(perm);
        throw  new SecurityException("权限不足");
    }
}
