package java.security;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

final class UnresolvedPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient Map<String, List<UnresolvedPermission>> perms = new HashMap(11);
  private static final long serialVersionUID = -7176153071733132400L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Hashtable.class) };
  
  public UnresolvedPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof UnresolvedPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    UnresolvedPermission localUnresolvedPermission = (UnresolvedPermission)paramPermission;
    Object localObject1;
    synchronized (this)
    {
      localObject1 = (List)perms.get(localUnresolvedPermission.getName());
      if (localObject1 == null)
      {
        localObject1 = new ArrayList();
        perms.put(localUnresolvedPermission.getName(), localObject1);
      }
    }
    synchronized (localObject1)
    {
      ((List)localObject1).add(localUnresolvedPermission);
    }
  }
  
  /* Error */
  List<UnresolvedPermission> getUnresolvedPermissions(Permission paramPermission)
  {
    // Byte code:
    //   0: aload_0
    //   1: dup
    //   2: astore_2
    //   3: monitorenter
    //   4: aload_0
    //   5: getfield 165	java/security/UnresolvedPermissionCollection:perms	Ljava/util/Map;
    //   8: aload_1
    //   9: invokevirtual 174	java/lang/Object:getClass	()Ljava/lang/Class;
    //   12: invokevirtual 172	java/lang/Class:getName	()Ljava/lang/String;
    //   15: invokeinterface 201 2 0
    //   20: checkcast 108	java/util/List
    //   23: aload_2
    //   24: monitorexit
    //   25: areturn
    //   26: astore_3
    //   27: aload_2
    //   28: monitorexit
    //   29: aload_3
    //   30: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	31	0	this	UnresolvedPermissionCollection
    //   0	31	1	paramPermission	Permission
    //   2	26	2	Ljava/lang/Object;	Object
    //   26	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	25	26	finally
    //   26	29	26	finally
  }
  
  public boolean implies(Permission paramPermission)
  {
    return false;
  }
  
  public Enumeration<Permission> elements()
  {
    ArrayList localArrayList = new ArrayList();
    synchronized (this)
    {
      Iterator localIterator = perms.values().iterator();
      while (localIterator.hasNext())
      {
        List localList = (List)localIterator.next();
        synchronized (localList)
        {
          localArrayList.addAll(localList);
        }
      }
    }
    return Collections.enumeration(localArrayList);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Hashtable localHashtable = new Hashtable(perms.size() * 2);
    synchronized (this)
    {
      Set localSet = perms.entrySet();
      Iterator localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        List localList = (List)localEntry.getValue();
        Vector localVector = new Vector(localList.size());
        synchronized (localList)
        {
          localVector.addAll(localList);
        }
        localHashtable.put(localEntry.getKey(), localVector);
      }
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("permissions", localHashtable);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Hashtable localHashtable = (Hashtable)localGetField.get("permissions", null);
    perms = new HashMap(localHashtable.size() * 2);
    Set localSet = localHashtable.entrySet();
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Vector localVector = (Vector)localEntry.getValue();
      ArrayList localArrayList = new ArrayList(localVector.size());
      localArrayList.addAll(localVector);
      perms.put(localEntry.getKey(), localArrayList);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\UnresolvedPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */