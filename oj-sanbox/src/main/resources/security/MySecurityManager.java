
import java.security.Permission;

public class MySecurityManager extends SecurityManager {
    @Override
    public void checkPermission(Permission perm) {
//        System.out.println("默认不做任何权限限制" + perm.toString());
//        super.checkPermission(perm);
    }

    @Override
    public void checkExec(String cmd) {
        throw new SecurityException("checkExec权限异常" + cmd);
    }

    @Override
    public void checkRead(String file) {
        if (file.contains("hutool")) {
            System.out.println("对该文件不做读的权限限制：" + file);
        }
        throw new SecurityException("checkRead权限异常" + file);

    }

    @Override
    public void checkWrite(String file) {
        throw new SecurityException("checkWrite权限异常" + file);
    }

    @Override
    public void checkDelete(String file) {
        throw new SecurityException("checkDelete权限异常" + file);
    }

    // 连接网络
    @Override
    public void checkConnect(String host, int port) {
        throw new SecurityException("checkConnect权限异常" + host + "," + port);
    }
}
