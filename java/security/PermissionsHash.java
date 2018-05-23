package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

final class PermissionsHash
  extends PermissionCollection
  implements Serializable
{
  private transient Map<Permission, Permission> permsMap = new HashMap(11);
  private static final long serialVersionUID = -8491988220802933440L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("perms", Hashtable.class) };
  
  PermissionsHash() {}
  
  public void add(Permission paramPermission)
  {
    synchronized (this)
    {
      permsMap.put(paramPermission, paramPermission);
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    synchronized (this)
    {
      Permission localPermission1 = (Permission)permsMap.get(paramPermission);
      if (localPermission1 == null)
      {
        Iterator localIterator = permsMap.values().iterator();
        while (localIterator.hasNext())
        {
          Permission localPermission2 = (Permission)localIterator.next();
          if (localPermission2.implies(paramPermission)) {
            return true;
          }
        }
        return false;
      }
      return true;
    }
  }
  
  /* Error */
  public java.util.Enumeration<Permission> elements()
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_1
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 118	java/security/PermissionsHash:permsMap	Ljava/util/Map;
    //   8: invokeinterface 136 1 0
    //   13: invokestatic 127	java/util/Collections:enumeration	(Ljava/util/Collection;)Ljava/util/Enumeration;
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
    //   0	24	0	this	PermissionsHash
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
    Hashtable localHashtable = new Hashtable(permsMap.size() * 2);
    synchronized (this)
    {
      localHashtable.putAll(permsMap);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("perms", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Hashtable localHashtable = (Hashtable)localGetField.get("perms", null);
    permsMap = new HashMap(localHashtable.size() * 2);
    permsMap.putAll(localHashtable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\PermissionsHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */