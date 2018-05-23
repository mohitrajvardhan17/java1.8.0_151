package sun.security.acl;

import java.security.acl.Permission;

public class AllPermissionsImpl
  extends PermissionImpl
{
  public AllPermissionsImpl(String paramString)
  {
    super(paramString);
  }
  
  public boolean equals(Permission paramPermission)
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\AllPermissionsImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */