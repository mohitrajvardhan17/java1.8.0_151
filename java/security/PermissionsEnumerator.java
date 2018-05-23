package java.security;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class PermissionsEnumerator
  implements Enumeration<Permission>
{
  private Iterator<PermissionCollection> perms;
  private Enumeration<Permission> permset;
  
  PermissionsEnumerator(Iterator<PermissionCollection> paramIterator)
  {
    perms = paramIterator;
    permset = getNextEnumWithMore();
  }
  
  public boolean hasMoreElements()
  {
    if (permset == null) {
      return false;
    }
    if (permset.hasMoreElements()) {
      return true;
    }
    permset = getNextEnumWithMore();
    return permset != null;
  }
  
  public Permission nextElement()
  {
    if (hasMoreElements()) {
      return (Permission)permset.nextElement();
    }
    throw new NoSuchElementException("PermissionsEnumerator");
  }
  
  private Enumeration<Permission> getNextEnumWithMore()
  {
    while (perms.hasNext())
    {
      PermissionCollection localPermissionCollection = (PermissionCollection)perms.next();
      Enumeration localEnumeration = localPermissionCollection.elements();
      if (localEnumeration.hasMoreElements()) {
        return localEnumeration;
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PermissionsEnumerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */