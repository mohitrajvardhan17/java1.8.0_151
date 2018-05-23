package java.security;

import java.io.Serializable;
import java.util.Enumeration;
import sun.security.util.SecurityConstants;

final class AllPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private static final long serialVersionUID = -4023755556366636806L;
  private boolean all_allowed = false;
  
  public AllPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof AllPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    all_allowed = true;
  }
  
  public boolean implies(Permission paramPermission)
  {
    return all_allowed;
  }
  
  public Enumeration<Permission> elements()
  {
    new Enumeration()
    {
      private boolean hasMore = all_allowed;
      
      public boolean hasMoreElements()
      {
        return hasMore;
      }
      
      public Permission nextElement()
      {
        hasMore = false;
        return SecurityConstants.ALL_PERMISSION;
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AllPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */