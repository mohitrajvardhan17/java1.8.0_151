package sun.security.acl;

import java.security.acl.Permission;

public class PermissionImpl
  implements Permission
{
  private String permission;
  
  public PermissionImpl(String paramString)
  {
    permission = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Permission))
    {
      Permission localPermission = (Permission)paramObject;
      return permission.equals(localPermission.toString());
    }
    return false;
  }
  
  public String toString()
  {
    return permission;
  }
  
  public int hashCode()
  {
    return toString().hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\PermissionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */