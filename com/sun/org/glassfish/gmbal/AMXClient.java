package com.sun.org.glassfish.gmbal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;

public class AMXClient
  implements AMXMBeanInterface
{
  public static final ObjectName NULL_OBJECTNAME = makeObjectName("null:type=Null,name=Null");
  private MBeanServerConnection server;
  private ObjectName oname;
  
  private static ObjectName makeObjectName(String paramString)
  {
    try
    {
      return new ObjectName(paramString);
    }
    catch (MalformedObjectNameException localMalformedObjectNameException) {}
    return null;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AMXClient)) {
      return false;
    }
    AMXClient localAMXClient = (AMXClient)paramObject;
    return oname.equals(oname);
  }
  
  public int hashCode()
  {
    int i = 5;
    i = 47 * i + (oname != null ? oname.hashCode() : 0);
    return i;
  }
  
  public String toString()
  {
    return "AMXClient[" + oname + "]";
  }
  
  private <T> T fetchAttribute(String paramString, Class<T> paramClass)
  {
    try
    {
      Object localObject = server.getAttribute(oname, paramString);
      if (NULL_OBJECTNAME.equals(localObject)) {
        return null;
      }
      return (T)paramClass.cast(localObject);
    }
    catch (JMException localJMException)
    {
      throw new GmbalException("Exception in fetchAttribute", localJMException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in fetchAttribute", localIOException);
    }
  }
  
  public AMXClient(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName)
  {
    server = paramMBeanServerConnection;
    oname = paramObjectName;
  }
  
  private AMXClient makeAMX(ObjectName paramObjectName)
  {
    if (paramObjectName == null) {
      return null;
    }
    return new AMXClient(server, paramObjectName);
  }
  
  public String getName()
  {
    return (String)fetchAttribute("Name", String.class);
  }
  
  public Map<String, ?> getMeta()
  {
    try
    {
      ModelMBeanInfo localModelMBeanInfo = (ModelMBeanInfo)server.getMBeanInfo(oname);
      Descriptor localDescriptor = localModelMBeanInfo.getMBeanDescriptor();
      HashMap localHashMap = new HashMap();
      for (String str : localDescriptor.getFieldNames()) {
        localHashMap.put(str, localDescriptor.getFieldValue(str));
      }
      return localHashMap;
    }
    catch (MBeanException localMBeanException)
    {
      throw new GmbalException("Exception in getMeta", localMBeanException);
    }
    catch (RuntimeOperationsException localRuntimeOperationsException)
    {
      throw new GmbalException("Exception in getMeta", localRuntimeOperationsException);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in getMeta", localInstanceNotFoundException);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw new GmbalException("Exception in getMeta", localIntrospectionException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in getMeta", localReflectionException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in getMeta", localIOException);
    }
  }
  
  public AMXClient getParent()
  {
    ObjectName localObjectName = (ObjectName)fetchAttribute("Parent", ObjectName.class);
    return makeAMX(localObjectName);
  }
  
  public AMXClient[] getChildren()
  {
    ObjectName[] arrayOfObjectName = (ObjectName[])fetchAttribute("Children", ObjectName[].class);
    return makeAMXArray(arrayOfObjectName);
  }
  
  private AMXClient[] makeAMXArray(ObjectName[] paramArrayOfObjectName)
  {
    AMXClient[] arrayOfAMXClient = new AMXClient[paramArrayOfObjectName.length];
    int i = 0;
    for (ObjectName localObjectName : paramArrayOfObjectName) {
      arrayOfAMXClient[(i++)] = makeAMX(localObjectName);
    }
    return arrayOfAMXClient;
  }
  
  public Object getAttribute(String paramString)
  {
    try
    {
      return server.getAttribute(oname, paramString);
    }
    catch (MBeanException localMBeanException)
    {
      throw new GmbalException("Exception in getAttribute", localMBeanException);
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw new GmbalException("Exception in getAttribute", localAttributeNotFoundException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in getAttribute", localReflectionException);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in getAttribute", localInstanceNotFoundException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in getAttribute", localIOException);
    }
  }
  
  public void setAttribute(String paramString, Object paramObject)
  {
    Attribute localAttribute = new Attribute(paramString, paramObject);
    setAttribute(localAttribute);
  }
  
  public void setAttribute(Attribute paramAttribute)
  {
    try
    {
      server.setAttribute(oname, paramAttribute);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in setAttribute", localInstanceNotFoundException);
    }
    catch (AttributeNotFoundException localAttributeNotFoundException)
    {
      throw new GmbalException("Exception in setAttribute", localAttributeNotFoundException);
    }
    catch (InvalidAttributeValueException localInvalidAttributeValueException)
    {
      throw new GmbalException("Exception in setAttribute", localInvalidAttributeValueException);
    }
    catch (MBeanException localMBeanException)
    {
      throw new GmbalException("Exception in setAttribute", localMBeanException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in setAttribute", localReflectionException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in setAttribute", localIOException);
    }
  }
  
  public AttributeList getAttributes(String[] paramArrayOfString)
  {
    try
    {
      return server.getAttributes(oname, paramArrayOfString);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in getAttributes", localInstanceNotFoundException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in getAttributes", localReflectionException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in getAttributes", localIOException);
    }
  }
  
  public AttributeList setAttributes(AttributeList paramAttributeList)
  {
    try
    {
      return server.setAttributes(oname, paramAttributeList);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in setAttributes", localInstanceNotFoundException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in setAttributes", localReflectionException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in setAttributes", localIOException);
    }
  }
  
  public Object invoke(String paramString, Object[] paramArrayOfObject, String[] paramArrayOfString)
    throws MBeanException, ReflectionException
  {
    try
    {
      return server.invoke(oname, paramString, paramArrayOfObject, paramArrayOfString);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in invoke", localInstanceNotFoundException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in invoke", localIOException);
    }
  }
  
  public MBeanInfo getMBeanInfo()
  {
    try
    {
      return server.getMBeanInfo(oname);
    }
    catch (InstanceNotFoundException localInstanceNotFoundException)
    {
      throw new GmbalException("Exception in invoke", localInstanceNotFoundException);
    }
    catch (IntrospectionException localIntrospectionException)
    {
      throw new GmbalException("Exception in invoke", localIntrospectionException);
    }
    catch (ReflectionException localReflectionException)
    {
      throw new GmbalException("Exception in invoke", localReflectionException);
    }
    catch (IOException localIOException)
    {
      throw new GmbalException("Exception in invoke", localIOException);
    }
  }
  
  public ObjectName objectName()
  {
    return oname;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\AMXClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */