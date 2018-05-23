package java.security;

import java.io.Serializable;

public abstract class Permission
  implements Guard, Serializable
{
  private static final long serialVersionUID = -5636570222231596674L;
  private String name;
  
  public Permission(String paramString)
  {
    name = paramString;
  }
  
  public void checkGuard(Object paramObject)
    throws SecurityException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(this);
    }
  }
  
  public abstract boolean implies(Permission paramPermission);
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public final String getName()
  {
    return name;
  }
  
  public abstract String getActions();
  
  public PermissionCollection newPermissionCollection()
  {
    return null;
  }
  
  public String toString()
  {
    String str = getActions();
    if ((str == null) || (str.length() == 0)) {
      return "(\"" + getClass().getName() + "\" \"" + name + "\")";
    }
    return "(\"" + getClass().getName() + "\" \"" + name + "\" \"" + str + "\")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Permission.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */