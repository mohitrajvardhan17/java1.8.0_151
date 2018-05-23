package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.Iterator;

public class RoleResult
  implements Serializable
{
  private static final long oldSerialVersionUID = 3786616013762091099L;
  private static final long newSerialVersionUID = -6304063118040985512L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myRoleList", RoleList.class), new ObjectStreamField("myRoleUnresList", RoleUnresolvedList.class) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("roleList", RoleList.class), new ObjectStreamField("unresolvedRoleList", RoleUnresolvedList.class) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private RoleList roleList = null;
  private RoleUnresolvedList unresolvedRoleList = null;
  
  public RoleResult(RoleList paramRoleList, RoleUnresolvedList paramRoleUnresolvedList)
  {
    setRoles(paramRoleList);
    setRolesUnresolved(paramRoleUnresolvedList);
  }
  
  public RoleList getRoles()
  {
    return roleList;
  }
  
  public RoleUnresolvedList getRolesUnresolved()
  {
    return unresolvedRoleList;
  }
  
  public void setRoles(RoleList paramRoleList)
  {
    if (paramRoleList != null)
    {
      roleList = new RoleList();
      Iterator localIterator = paramRoleList.iterator();
      while (localIterator.hasNext())
      {
        Role localRole = (Role)localIterator.next();
        roleList.add((Role)localRole.clone());
      }
    }
    else
    {
      roleList = null;
    }
  }
  
  public void setRolesUnresolved(RoleUnresolvedList paramRoleUnresolvedList)
  {
    if (paramRoleUnresolvedList != null)
    {
      unresolvedRoleList = new RoleUnresolvedList();
      Iterator localIterator = paramRoleUnresolvedList.iterator();
      while (localIterator.hasNext())
      {
        RoleUnresolved localRoleUnresolved = (RoleUnresolved)localIterator.next();
        unresolvedRoleList.add((RoleUnresolved)localRoleUnresolved.clone());
      }
    }
    else
    {
      unresolvedRoleList = null;
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      roleList = ((RoleList)localGetField.get("myRoleList", null));
      if (localGetField.defaulted("myRoleList")) {
        throw new NullPointerException("myRoleList");
      }
      unresolvedRoleList = ((RoleUnresolvedList)localGetField.get("myRoleUnresList", null));
      if (localGetField.defaulted("myRoleUnresList")) {
        throw new NullPointerException("myRoleUnresList");
      }
    }
    else
    {
      paramObjectInputStream.defaultReadObject();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (compat)
    {
      ObjectOutputStream.PutField localPutField = paramObjectOutputStream.putFields();
      localPutField.put("myRoleList", roleList);
      localPutField.put("myRoleUnresList", unresolvedRoleList);
      paramObjectOutputStream.writeFields();
    }
    else
    {
      paramObjectOutputStream.defaultWriteObject();
    }
  }
  
  static
  {
    try
    {
      GetPropertyAction localGetPropertyAction = new GetPropertyAction("jmx.serial.form");
      String str = (String)AccessController.doPrivileged(localGetPropertyAction);
      compat = (str != null) && (str.equals("1.0"));
    }
    catch (Exception localException) {}
    if (compat)
    {
      serialPersistentFields = oldSerialPersistentFields;
      serialVersionUID = 3786616013762091099L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -6304063118040985512L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RoleResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */