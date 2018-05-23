package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

final class BasicPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private static final long serialVersionUID = 739301742472979399L;
  private transient Map<String, Permission> perms = new HashMap(11);
  private boolean all_allowed = false;
  private Class<?> permClass;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("permClass", Class.class) };
  
  public BasicPermissionCollection(Class<?> paramClass)
  {
    permClass = paramClass;
  }
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof BasicPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    BasicPermission localBasicPermission = (BasicPermission)paramPermission;
    if (permClass == null) {
      permClass = localBasicPermission.getClass();
    } else if (localBasicPermission.getClass() != permClass) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    synchronized (this)
    {
      perms.put(localBasicPermission.getCanonicalName(), paramPermission);
    }
    if ((!all_allowed) && (localBasicPermission.getCanonicalName().equals("*"))) {
      all_allowed = true;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof BasicPermission)) {
      return false;
    }
    BasicPermission localBasicPermission = (BasicPermission)paramPermission;
    if (localBasicPermission.getClass() != permClass) {
      return false;
    }
    if (all_allowed) {
      return true;
    }
    String str = localBasicPermission.getCanonicalName();
    Permission localPermission;
    synchronized (this)
    {
      localPermission = (Permission)perms.get(str);
    }
    if (localPermission != null) {
      return localPermission.implies(paramPermission);
    }
    int i;
    for (int j = str.length() - 1; (i = str.lastIndexOf(".", j)) != -1; j = i - 1)
    {
      str = str.substring(0, i + 1) + "*";
      synchronized (this)
      {
        localPermission = (Permission)perms.get(str);
      }
      if (localPermission != null) {
        return localPermission.implies(paramPermission);
      }
    }
    return false;
  }
  
  /* Error */
  public Enumeration<Permission> elements()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 184	java/security/BasicPermissionCollection:perms	Ljava/util/Map;
    //   8: invokeinterface 217 1 0
    //   13: invokestatic 208	java/util/Collections:enumeration	(Ljava/util/Collection;)Ljava/util/Enumeration;
    //   16: aload_1
    //   17: monitorexit
    //   18: areturn
    //   19: astore_2
    //   20: aload_1
    //   21: monitorexit
    //   22: aload_2
    //   23: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	24	0	this	BasicPermissionCollection
    //   2	19	1	Ljava/lang/Object;	Object
    //   19	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	18	19	finally
    //   19	22	19	finally
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable(perms.size() * 2);
    synchronized (this)
    {
      localHashtable.putAll(perms);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("all_allowed", all_allowed);
    ((ObjectOutputStream.PutField)???).put("permissions", localHashtable);
    ((ObjectOutputStream.PutField)???).put("permClass", permClass);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Hashtable localHashtable = (Hashtable)localGetField.get("permissions", null);
    perms = new HashMap(localHashtable.size() * 2);
    perms.putAll(localHashtable);
    all_allowed = localGetField.get("all_allowed", false);
    permClass = ((Class)localGetField.get("permClass", null));
    if (permClass == null)
    {
      Enumeration localEnumeration = localHashtable.elements();
      if (localEnumeration.hasMoreElements())
      {
        Permission localPermission = (Permission)localEnumeration.nextElement();
        permClass = localPermission.getClass();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\BasicPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */