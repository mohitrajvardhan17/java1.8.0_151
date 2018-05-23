package java.security.acl;

import java.security.Principal;
import java.util.Enumeration;

public abstract interface Acl
  extends Owner
{
  public abstract void setName(Principal paramPrincipal, String paramString)
    throws NotOwnerException;
  
  public abstract String getName();
  
  public abstract boolean addEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException;
  
  public abstract boolean removeEntry(Principal paramPrincipal, AclEntry paramAclEntry)
    throws NotOwnerException;
  
  public abstract Enumeration<Permission> getPermissions(Principal paramPrincipal);
  
  public abstract Enumeration<AclEntry> entries();
  
  public abstract boolean checkPermission(Principal paramPrincipal, Permission paramPermission);
  
  public abstract String toString();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\acl\Acl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */