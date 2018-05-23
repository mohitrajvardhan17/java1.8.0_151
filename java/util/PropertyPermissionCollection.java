package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;

final class PropertyPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient Map<String, PropertyPermission> perms = new HashMap(32);
  private boolean all_allowed = false;
  private static final long serialVersionUID = 7015263904581634791L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE) };
  
  public PropertyPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof PropertyPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    PropertyPermission localPropertyPermission1 = (PropertyPermission)paramPermission;
    String str1 = localPropertyPermission1.getName();
    synchronized (this)
    {
      PropertyPermission localPropertyPermission2 = (PropertyPermission)perms.get(str1);
      if (localPropertyPermission2 != null)
      {
        int i = localPropertyPermission2.getMask();
        int j = localPropertyPermission1.getMask();
        if (i != j)
        {
          int k = i | j;
          String str2 = PropertyPermission.getActions(k);
          perms.put(str1, new PropertyPermission(str1, str2));
        }
      }
      else
      {
        perms.put(str1, localPropertyPermission1);
      }
    }
    if ((!all_allowed) && (str1.equals("*"))) {
      all_allowed = true;
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof PropertyPermission)) {
      return false;
    }
    PropertyPermission localPropertyPermission1 = (PropertyPermission)paramPermission;
    int i = localPropertyPermission1.getMask();
    int j = 0;
    PropertyPermission localPropertyPermission2;
    if (all_allowed)
    {
      synchronized (this)
      {
        localPropertyPermission2 = (PropertyPermission)perms.get("*");
      }
      if (localPropertyPermission2 != null)
      {
        j |= localPropertyPermission2.getMask();
        if ((j & i) == i) {
          return true;
        }
      }
    }
    ??? = localPropertyPermission1.getName();
    synchronized (this)
    {
      localPropertyPermission2 = (PropertyPermission)perms.get(???);
    }
    if (localPropertyPermission2 != null)
    {
      j |= localPropertyPermission2.getMask();
      if ((j & i) == i) {
        return true;
      }
    }
    int k;
    for (int m = ((String)???).length() - 1; (k = ((String)???).lastIndexOf(".", m)) != -1; m = k - 1)
    {
      ??? = ((String)???).substring(0, k + 1) + "*";
      synchronized (this)
      {
        localPropertyPermission2 = (PropertyPermission)perms.get(???);
      }
      if (localPropertyPermission2 != null)
      {
        j |= localPropertyPermission2.getMask();
        if ((j & i) == i) {
          return true;
        }
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
    //   5: getfield 170	java/util/PropertyPermissionCollection:perms	Ljava/util/Map;
    //   8: invokeinterface 201 1 0
    //   13: invokestatic 190	java/util/Collections:enumeration	(Ljava/util/Collection;)Ljava/util/Enumeration;
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
    //   0	24	0	this	PropertyPermissionCollection
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
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    all_allowed = localGetField.get("all_allowed", false);
    Hashtable localHashtable = (Hashtable)localGetField.get("permissions", null);
    perms = new HashMap(localHashtable.size() * 2);
    perms.putAll(localHashtable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\PropertyPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */