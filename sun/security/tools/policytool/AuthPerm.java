package sun.security.tools.policytool;

class AuthPerm
  extends Perm
{
  public AuthPerm()
  {
    super("AuthPermission", "javax.security.auth.AuthPermission", new String[] { "doAs", "doAsPrivileged", "getSubject", "getSubjectFromDomainCombiner", "setReadOnly", "modifyPrincipals", "modifyPublicCredentials", "modifyPrivateCredentials", "refreshCredential", "destroyCredential", "createLoginContext.<" + PolicyTool.getMessage("name") + ">", "getLoginConfiguration", "setLoginConfiguration", "createLoginConfiguration.<" + PolicyTool.getMessage("configuration.type") + ">", "refreshLoginConfiguration" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\AuthPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */