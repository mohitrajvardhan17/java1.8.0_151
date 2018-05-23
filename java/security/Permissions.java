package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class Permissions
  extends PermissionCollection
  implements Serializable
{
  private transient Map<Class<?>, PermissionCollection> permsMap = new HashMap(11);
  private transient boolean hasUnresolved = false;
  PermissionCollection allPermission = null;
  private static final long serialVersionUID = 4858622370623524688L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("perms", Hashtable.class), new ObjectStreamField("allPermission", PermissionCollection.class) };
  
  public Permissions() {}
  
  public void add(Permission paramPermission)
  {
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly Permissions object");
    }
    PermissionCollection localPermissionCollection;
    synchronized (this)
    {
      localPermissionCollection = getPermissionCollection(paramPermission, true);
      localPermissionCollection.add(paramPermission);
    }
    if ((paramPermission instanceof AllPermission)) {
      allPermission = localPermissionCollection;
    }
    if ((paramPermission instanceof UnresolvedPermission)) {
      hasUnresolved = true;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (allPermission != null) {
      return true;
    }
    synchronized (this)
    {
      PermissionCollection localPermissionCollection = getPermissionCollection(paramPermission, false);
      if (localPermissionCollection != null) {
        return localPermissionCollection.implies(paramPermission);
      }
      return false;
    }
  }
  
  /* Error */
  public Enumeration<Permission> elements()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: new 103	java/security/PermissionsEnumerator
    //   7: dup
    //   8: aload_0
    //   9: getfield 173	java/security/Permissions:permsMap	Ljava/util/Map;
    //   12: invokeinterface 204 1 0
    //   17: invokeinterface 199 1 0
    //   22: invokespecial 190	java/security/PermissionsEnumerator:<init>	(Ljava/util/Iterator;)V
    //   25: aload_1
    //   26: monitorexit
    //   27: areturn
    //   28: astore_2
    //   29: aload_1
    //   30: monitorexit
    //   31: aload_2
    //   32: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	33	0	this	Permissions
    //   2	28	1	Ljava/lang/Object;	Object
    //   28	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	27	28	finally
    //   28	31	28	finally
  }
  
  private PermissionCollection getPermissionCollection(Permission paramPermission, boolean paramBoolean)
  {
    Class localClass = paramPermission.getClass();
    Object localObject = (PermissionCollection)permsMap.get(localClass);
    if ((!hasUnresolved) && (!paramBoolean)) {
      return (PermissionCollection)localObject;
    }
    if (localObject == null)
    {
      localObject = hasUnresolved ? getUnresolvedPermissions(paramPermission) : null;
      if ((localObject == null) && (paramBoolean))
      {
        localObject = paramPermission.newPermissionCollection();
        if (localObject == null) {
          localObject = new PermissionsHash();
        }
      }
      if (localObject != null) {
        permsMap.put(localClass, localObject);
      }
    }
    return (PermissionCollection)localObject;
  }
  
  private PermissionCollection getUnresolvedPermissions(Permission paramPermission)
  {
    UnresolvedPermissionCollection localUnresolvedPermissionCollection = (UnresolvedPermissionCollection)permsMap.get(UnresolvedPermission.class);
    if (localUnresolvedPermissionCollection == null) {
      return null;
    }
    List localList = localUnresolvedPermissionCollection.getUnresolvedPermissions(paramPermission);
    if (localList == null) {
      return null;
    }
    Certificate[] arrayOfCertificate = null;
    Object[] arrayOfObject = paramPermission.getClass().getSigners();
    int i = 0;
    if (arrayOfObject != null)
    {
      for (int j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Certificate)) {
          i++;
        }
      }
      arrayOfCertificate = new Certificate[i];
      i = 0;
      for (j = 0; j < arrayOfObject.length; j++) {
        if ((arrayOfObject[j] instanceof Certificate)) {
          arrayOfCertificate[(i++)] = ((Certificate)arrayOfObject[j]);
        }
      }
    }
    Object localObject1 = null;
    synchronized (localList)
    {
      int k = localList.size();
      for (int m = 0; m < k; m++)
      {
        UnresolvedPermission localUnresolvedPermission = (UnresolvedPermission)localList.get(m);
        Permission localPermission = localUnresolvedPermission.resolve(paramPermission, arrayOfCertificate);
        if (localPermission != null)
        {
          if (localObject1 == null)
          {
            localObject1 = paramPermission.newPermissionCollection();
            if (localObject1 == null) {
              localObject1 = new PermissionsHash();
            }
          }
          ((PermissionCollection)localObject1).add(localPermission);
        }
      }
    }
    return (PermissionCollection)localObject1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable(permsMap.size() * 2);
    synchronized (this)
    {
      localHashtable.putAll(permsMap);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("allPermission", allPermission);
    ((ObjectOutputStream.PutField)???).put("perms", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    allPermission = ((PermissionCollection)localGetField.get("allPermission", null));
    Hashtable localHashtable = (Hashtable)localGetField.get("perms", null);
    permsMap = new HashMap(localHashtable.size() * 2);
    permsMap.putAll(localHashtable);
    UnresolvedPermissionCollection localUnresolvedPermissionCollection = (UnresolvedPermissionCollection)permsMap.get(UnresolvedPermission.class);
    hasUnresolved = ((localUnresolvedPermissionCollection != null) && (localUnresolvedPermissionCollection.elements().hasMoreElements()));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Permissions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */