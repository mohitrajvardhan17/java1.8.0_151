package javax.management;

import java.io.Serializable;

public class ObjectInstance
  implements Serializable
{
  private static final long serialVersionUID = -4099952623687795850L;
  private ObjectName name;
  private String className;
  
  public ObjectInstance(String paramString1, String paramString2)
    throws MalformedObjectNameException
  {
    this(new ObjectName(paramString1), paramString2);
  }
  
  public ObjectInstance(ObjectName paramObjectName, String paramString)
  {
    if (paramObjectName.isPattern())
    {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("Invalid name->" + paramObjectName.toString());
      throw new RuntimeOperationsException(localIllegalArgumentException);
    }
    name = paramObjectName;
    className = paramString;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ObjectInstance)) {
      return false;
    }
    ObjectInstance localObjectInstance = (ObjectInstance)paramObject;
    if (!name.equals(localObjectInstance.getObjectName())) {
      return false;
    }
    if (className == null) {
      return localObjectInstance.getClassName() == null;
    }
    return className.equals(localObjectInstance.getClassName());
  }
  
  public int hashCode()
  {
    int i = className == null ? 0 : className.hashCode();
    return name.hashCode() ^ i;
  }
  
  public ObjectName getObjectName()
  {
    return name;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public String toString()
  {
    return getClassName() + "[" + getObjectName() + "]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\ObjectInstance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */