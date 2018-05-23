package sun.security.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Group;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.Vector;

public class AclEntryImpl
  implements AclEntry
{
  private Principal user = null;
  private Vector<Permission> permissionSet = new Vector(10, 10);
  private boolean negative = false;
  
  public AclEntryImpl(Principal paramPrincipal)
  {
    user = paramPrincipal;
  }
  
  public AclEntryImpl() {}
  
  public boolean setPrincipal(Principal paramPrincipal)
  {
    if (user != null) {
      return false;
    }
    user = paramPrincipal;
    return true;
  }
  
  public void setNegativePermissions()
  {
    negative = true;
  }
  
  public boolean isNegative()
  {
    return negative;
  }
  
  public boolean addPermission(Permission paramPermission)
  {
    if (permissionSet.contains(paramPermission)) {
      return false;
    }
    permissionSet.addElement(paramPermission);
    return true;
  }
  
  public boolean removePermission(Permission paramPermission)
  {
    return permissionSet.removeElement(paramPermission);
  }
  
  public boolean checkPermission(Permission paramPermission)
  {
    return permissionSet.contains(paramPermission);
  }
  
  public Enumeration<Permission> permissions()
  {
    return permissionSet.elements();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (negative) {
      localStringBuffer.append("-");
    } else {
      localStringBuffer.append("+");
    }
    if ((user instanceof Group)) {
      localStringBuffer.append("Group.");
    } else {
      localStringBuffer.append("User.");
    }
    localStringBuffer.append(user + "=");
    Enumeration localEnumeration = permissions();
    while (localEnumeration.hasMoreElements())
    {
      Permission localPermission = (Permission)localEnumeration.nextElement();
      localStringBuffer.append(localPermission);
      if (localEnumeration.hasMoreElements()) {
        localStringBuffer.append(",");
      }
    }
    return new String(localStringBuffer);
  }
  
  public synchronized Object clone()
  {
    AclEntryImpl localAclEntryImpl = new AclEntryImpl(user);
    permissionSet = ((Vector)permissionSet.clone());
    negative = negative;
    return localAclEntryImpl;
  }
  
  public Principal getPrincipal()
  {
    return user;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\acl\AclEntryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */