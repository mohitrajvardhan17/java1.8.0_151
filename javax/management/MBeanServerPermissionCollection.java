package javax.management;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;

class MBeanServerPermissionCollection
  extends PermissionCollection
{
  private MBeanServerPermission collectionPermission;
  private static final long serialVersionUID = -5661980843569388590L;
  
  MBeanServerPermissionCollection() {}
  
  public synchronized void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof MBeanServerPermission))
    {
      localObject = "Permission not an MBeanServerPermission: " + paramPermission;
      throw new IllegalArgumentException((String)localObject);
    }
    if (isReadOnly()) {
      throw new SecurityException("Read-only permission collection");
    }
    Object localObject = (MBeanServerPermission)paramPermission;
    if (collectionPermission == null)
    {
      collectionPermission = ((MBeanServerPermission)localObject);
    }
    else if (!collectionPermission.implies(paramPermission))
    {
      int i = collectionPermission.mask | mask;
      collectionPermission = new MBeanServerPermission(i);
    }
  }
  
  public synchronized boolean implies(Permission paramPermission)
  {
    return (collectionPermission != null) && (collectionPermission.implies(paramPermission));
  }
  
  public synchronized Enumeration<Permission> elements()
  {
    Set localSet;
    if (collectionPermission == null) {
      localSet = Collections.emptySet();
    } else {
      localSet = Collections.singleton(collectionPermission);
    }
    return Collections.enumeration(localSet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\MBeanServerPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */