package sun.security.tools.policytool;

class SecurityPerm
  extends Perm
{
  public SecurityPerm()
  {
    super("SecurityPermission", "java.security.SecurityPermission", new String[] { "createAccessControlContext", "getDomainCombiner", "getPolicy", "setPolicy", "createPolicy.<" + PolicyTool.getMessage("policy.type") + ">", "getProperty.<" + PolicyTool.getMessage("property.name") + ">", "setProperty.<" + PolicyTool.getMessage("property.name") + ">", "insertProvider.<" + PolicyTool.getMessage("provider.name") + ">", "removeProvider.<" + PolicyTool.getMessage("provider.name") + ">", "clearProviderProperties.<" + PolicyTool.getMessage("provider.name") + ">", "putProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">", "removeProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">" }, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\SecurityPerm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */