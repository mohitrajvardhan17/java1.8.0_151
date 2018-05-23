package javax.security.auth.kerberos;

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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

final class KrbServicePermissionCollection
  extends PermissionCollection
  implements Serializable
{
  private transient List<Permission> perms = new ArrayList();
  private static final long serialVersionUID = -4118834211490102011L;
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("permissions", Vector.class) };
  
  public KrbServicePermissionCollection() {}
  
  public boolean implies(Permission paramPermission)
  {
    if (!(paramPermission instanceof ServicePermission)) {
      return false;
    }
    ServicePermission localServicePermission1 = (ServicePermission)paramPermission;
    Permission localPermission1 = localServicePermission1.getMask();
    if (localPermission1 == 0)
    {
      Iterator localIterator = perms.iterator();
      while (localIterator.hasNext())
      {
        localPermission2 = (Permission)localIterator.next();
        ServicePermission localServicePermission2 = (ServicePermission)localPermission2;
        if (localServicePermission2.impliesIgnoreMask(localServicePermission1)) {
          return true;
        }
      }
      return false;
    }
    int i = 0;
    Permission localPermission2 = localPermission1;
    synchronized (this)
    {
      int j = perms.size();
      for (int k = 0; k < j; k++)
      {
        ServicePermission localServicePermission3 = (ServicePermission)perms.get(k);
        if (((localPermission2 & localServicePermission3.getMask()) != 0) && (localServicePermission3.impliesIgnoreMask(localServicePermission1)))
        {
          i |= localServicePermission3.getMask();
          if ((i & localPermission1) == localPermission1) {
            return true;
          }
          localPermission2 = localPermission1 ^ i;
        }
      }
    }
    return false;
  }
  
  public void add(Permission paramPermission)
  {
    if (!(paramPermission instanceof ServicePermission)) {
      throw new IllegalArgumentException("invalid permission: " + paramPermission);
    }
    if (isReadOnly()) {
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
    }
    synchronized (this)
    {
      perms.add(0, paramPermission);
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
    //   5: getfield 141	javax/security/auth/kerberos/KrbServicePermissionCollection:perms	Ljava/util/List;
    //   8: invokestatic 157	java/util/Collections:enumeration	(Ljava/util/Collection;)Ljava/util/Enumeration;
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
    //   0	19	0	this	KrbServicePermissionCollection
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\kerberos\KrbServicePermissionCollection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */