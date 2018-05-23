package javax.management.relation;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.management.ObjectName;

public class RoleUnresolved
  implements Serializable
{
  private static final long oldSerialVersionUID = -9026457686611660144L;
  private static final long newSerialVersionUID = -48350262537070138L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myRoleValue", ArrayList.class), new ObjectStreamField("myPbType", Integer.TYPE) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("roleName", String.class), new ObjectStreamField("roleValue", List.class), new ObjectStreamField("problemType", Integer.TYPE) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private String roleName = null;
  private List<ObjectName> roleValue = null;
  private int problemType;
  
  public RoleUnresolved(String paramString, List<ObjectName> paramList, int paramInt)
    throws IllegalArgumentException
  {
    if (paramString == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    setRoleName(paramString);
    setRoleValue(paramList);
    setProblemType(paramInt);
  }
  
  public String getRoleName()
  {
    return roleName;
  }
  
  public List<ObjectName> getRoleValue()
  {
    return roleValue;
  }
  
  public int getProblemType()
  {
    return problemType;
  }
  
  public void setRoleName(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    roleName = paramString;
  }
  
  public void setRoleValue(List<ObjectName> paramList)
  {
    if (paramList != null) {
      roleValue = new ArrayList(paramList);
    } else {
      roleValue = null;
    }
  }
  
  public void setProblemType(int paramInt)
    throws IllegalArgumentException
  {
    if (!RoleStatus.isRoleStatus(paramInt))
    {
      String str = "Incorrect problem type.";
      throw new IllegalArgumentException(str);
    }
    problemType = paramInt;
  }
  
  public Object clone()
  {
    try
    {
      return new RoleUnresolved(roleName, roleValue, problemType);
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return null;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("role name: " + roleName);
    if (roleValue != null)
    {
      localStringBuilder.append("; value: ");
      Iterator localIterator = roleValue.iterator();
      while (localIterator.hasNext())
      {
        ObjectName localObjectName = (ObjectName)localIterator.next();
        localStringBuilder.append(localObjectName.toString());
        if (localIterator.hasNext()) {
          localStringBuilder.append(", ");
        }
      }
    }
    localStringBuilder.append("; problem type: " + problemType);
    return localStringBuilder.toString();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      roleName = ((String)localGetField.get("myRoleName", null));
      if (localGetField.defaulted("myRoleName")) {
        throw new NullPointerException("myRoleName");
      }
      roleValue = ((List)Util.cast(localGetField.get("myRoleValue", null)));
      if (localGetField.defaulted("myRoleValue")) {
        throw new NullPointerException("myRoleValue");
      }
      problemType = localGetField.get("myPbType", 0);
      if (localGetField.defaulted("myPbType")) {
        throw new NullPointerException("myPbType");
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
      localPutField.put("myRoleName", roleName);
      localPutField.put("myRoleValue", roleValue);
      localPutField.put("myPbType", problemType);
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
      serialVersionUID = -9026457686611660144L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = -48350262537070138L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RoleUnresolved.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */