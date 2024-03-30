import ACCESS_ENUM from "@/access/accessEnum";

/**
 * 校验用户权限
 * @param loginUser 登录用户
 * @param needAccess 需要的权限
 * @return boolean 校验权限的结果
 */
const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGON) => {
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGON;
  if (needAccess === ACCESS_ENUM.NOT_LOGON) {
    return true;
  }
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGON) {
      return false;
    }
  }
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }
  return true;
};
export default checkAccess;
