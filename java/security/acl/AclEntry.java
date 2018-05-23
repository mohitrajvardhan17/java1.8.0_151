package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public abstract interface AclEntry
  extends Cloneable
{
  public abstract boolean setPrincipal(Principal paramPrincipal);
  
  public abstract Principal getPrincipal();
  
  public abstract void setNegativePermissions();
  
  public abstract boolean isNegative();
  
  public abstract boolean addPermission(Permission paramPermission);
  
  public abstract boolean removePermission(Permission paramPermission);
  
  public abstract boolean checkPermission(Permission paramPermission);
  
  public abstract Enumeration<Permission> permissions();
  
  public abstract String toString();
  
  public abstract Object clone();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\acl\AclEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */