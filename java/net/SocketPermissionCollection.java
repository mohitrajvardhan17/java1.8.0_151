package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

final class SocketPermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient List<SocketPermission> perms = new ArrayList();
  private static final long serialVersionUID = 2787186408602843674L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public SocketPermissionCollection() {}
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof SocketPermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    synchronized (this)
    {
      perms.add(0, (SocketPermission)paramPermission);
    }
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof SocketPermission)) {
      return false;
    }
    SocketPermission localSocketPermission1 = (SocketPermission)paramPermission;
    int i = localSocketPermission1.getMask();
    int j = 0;
    int k = i;
    synchronized (this)
    {
      int m = perms.size();
      for (int n = 0; n < m; n++)
      {
        SocketPermission localSocketPermission2 = (SocketPermission)perms.get(n);
        if (((k & localSocketPermission2.getMask()) != 0) && (localSocketPermission2.impliesIgnoreMask(localSocketPermission1)))
        {
          j |= localSocketPermission2.getMask();
          if ((j & i) == i) {
            return true;
          }
          k = i ^ j;
        }
      }
    }
    return false;
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
    //   5: getfield 131	java/net/SocketPermissionCollection:perms	Ljava/util/List;
    //   8: invokestatic 150	java/util/Collections:enumeration	(Ljava/util/Collection;)Ljava/util/Enumeration;
    //   11: aload_1
    //   12: monitorexit
    //   13: areturn
    //   14: astore_2
    //   15: aload_1
    //   16: monitorexit
    //   17: aload_2
    //   18: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	19	0	this	SocketPermissionCollection
    //   2	14	1	Ljava/lang/Object;	Object
    //   14	4	2	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   4	13	14	finally
    //   14	17	14	finally
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    Vector localVector = new Vector(perms.size());
    synchronized (this)
    {
      localVector.addAll(perms);
    }
    ??? = paramObjectOutputStream.putFields();
    ((ObjectOutputStream.PutField)???).put("permissions", localVector);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
    Vector localVector = (Vector)localGetField.get("permissions", null);
    perms = new ArrayList(localVector.size());
    perms.addAll(localVector);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\SocketPermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */