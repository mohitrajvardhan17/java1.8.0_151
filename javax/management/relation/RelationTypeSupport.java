package javax.management.relation;

import com.sun.jmx.defaults.JmxProperties;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputStream.GetField;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class RelationTypeSupport
  implements RelationType
{
  private static final long oldSerialVersionUID = -8179019472410837190L;
  private static final long newSerialVersionUID = 4611072955724144607L;
  private static final ObjectStreamField[] oldSerialPersistentFields = { new ObjectStreamField("myTypeName", String.class), new ObjectStreamField("myRoleName2InfoMap", HashMap.class), new ObjectStreamField("myIsInRelServFlg", Boolean.TYPE) };
  private static final ObjectStreamField[] newSerialPersistentFields = { new ObjectStreamField("typeName", String.class), new ObjectStreamField("roleName2InfoMap", Map.class), new ObjectStreamField("isInRelationService", Boolean.TYPE) };
  private static final long serialVersionUID;
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat = false;
  private String typeName = null;
  private Map<String, RoleInfo> roleName2InfoMap = new HashMap();
  private boolean isInRelationService = false;
  
  public RelationTypeSupport(String paramString, RoleInfo[] paramArrayOfRoleInfo)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if ((paramString == null) || (paramArrayOfRoleInfo == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", paramString);
    initMembers(paramString, paramArrayOfRoleInfo);
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
  }
  
  protected RelationTypeSupport(String paramString)
  {
    if (paramString == null)
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", paramString);
    typeName = paramString;
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
  }
  
  public String getRelationTypeName()
  {
    return typeName;
  }
  
  public List<RoleInfo> getRoleInfos()
  {
    return new ArrayList(roleName2InfoMap.values());
  }
  
  public RoleInfo getRoleInfo(String paramString)
    throws IllegalArgumentException, RoleInfoNotFoundException
  {
    if (paramString == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "getRoleInfo", paramString);
    Object localObject = (RoleInfo)roleName2InfoMap.get(paramString);
    if (localObject == null)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String str = "No role info for role ";
      localStringBuilder.append(str);
      localStringBuilder.append(paramString);
      throw new RoleInfoNotFoundException(localStringBuilder.toString());
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "getRoleInfo");
    return (RoleInfo)localObject;
  }
  
  protected void addRoleInfo(RoleInfo paramRoleInfo)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if (paramRoleInfo == null)
    {
      str1 = "Invalid parameter.";
      throw new IllegalArgumentException(str1);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "addRoleInfo", paramRoleInfo);
    if (isInRelationService)
    {
      str1 = "Relation type cannot be updated as it is declared in the Relation Service.";
      throw new RuntimeException(str1);
    }
    String str1 = paramRoleInfo.getName();
    if (roleName2InfoMap.containsKey(str1))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      String str2 = "Two role infos provided for role ";
      localStringBuilder.append(str2);
      localStringBuilder.append(str1);
      throw new InvalidRelationTypeException(localStringBuilder.toString());
    }
    roleName2InfoMap.put(str1, new RoleInfo(paramRoleInfo));
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "addRoleInfo");
  }
  
  void setRelationServiceFlag(boolean paramBoolean)
  {
    isInRelationService = paramBoolean;
  }
  
  private void initMembers(String paramString, RoleInfo[] paramArrayOfRoleInfo)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if ((paramString == null) || (paramArrayOfRoleInfo == null))
    {
      String str = "Invalid parameter.";
      throw new IllegalArgumentException(str);
    }
    JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "initMembers", paramString);
    typeName = paramString;
    checkRoleInfos(paramArrayOfRoleInfo);
    for (int i = 0; i < paramArrayOfRoleInfo.length; i++)
    {
      RoleInfo localRoleInfo = paramArrayOfRoleInfo[i];
      roleName2InfoMap.put(localRoleInfo.getName(), new RoleInfo(localRoleInfo));
    }
    JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "initMembers");
  }
  
  static void checkRoleInfos(RoleInfo[] paramArrayOfRoleInfo)
    throws IllegalArgumentException, InvalidRelationTypeException
  {
    if (paramArrayOfRoleInfo == null)
    {
      localObject = "Invalid parameter.";
      throw new IllegalArgumentException((String)localObject);
    }
    if (paramArrayOfRoleInfo.length == 0)
    {
      localObject = "No role info provided.";
      throw new InvalidRelationTypeException((String)localObject);
    }
    Object localObject = new HashSet();
    for (int i = 0; i < paramArrayOfRoleInfo.length; i++)
    {
      RoleInfo localRoleInfo = paramArrayOfRoleInfo[i];
      if (localRoleInfo == null)
      {
        str1 = "Null role info provided.";
        throw new InvalidRelationTypeException(str1);
      }
      String str1 = localRoleInfo.getName();
      if (((Set)localObject).contains(str1))
      {
        StringBuilder localStringBuilder = new StringBuilder();
        String str2 = "Two role infos provided for role ";
        localStringBuilder.append(str2);
        localStringBuilder.append(str1);
        throw new InvalidRelationTypeException(localStringBuilder.toString());
      }
      ((Set)localObject).add(str1);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    if (compat)
    {
      ObjectInputStream.GetField localGetField = paramObjectInputStream.readFields();
      typeName = ((String)localGetField.get("myTypeName", null));
      if (localGetField.defaulted("myTypeName")) {
        throw new NullPointerException("myTypeName");
      }
      roleName2InfoMap = ((Map)Util.cast(localGetField.get("myRoleName2InfoMap", null)));
      if (localGetField.defaulted("myRoleName2InfoMap")) {
        throw new NullPointerException("myRoleName2InfoMap");
      }
      isInRelationService = localGetField.get("myIsInRelServFlg", false);
      if (localGetField.defaulted("myIsInRelServFlg")) {
        throw new NullPointerException("myIsInRelServFlg");
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
      localPutField.put("myTypeName", typeName);
      localPutField.put("myRoleName2InfoMap", roleName2InfoMap);
      localPutField.put("myIsInRelServFlg", isInRelationService);
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
      serialVersionUID = -8179019472410837190L;
    }
    else
    {
      serialPersistentFields = newSerialPersistentFields;
      serialVersionUID = 4611072955724144607L;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\relation\RelationTypeSupport.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */