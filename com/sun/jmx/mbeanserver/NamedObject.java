package com.sun.jmx.mbeanserver;

import javax.management.DynamicMBean;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

public class NamedObject
{
  private final ObjectName name;
  private final DynamicMBean object;
  
  public NamedObject(ObjectName paramObjectName, DynamicMBean paramDynamicMBean)
  {
    if (paramObjectName.isPattern()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + paramObjectName.toString()));
    }
    name = paramObjectName;
    object = paramDynamicMBean;
  }
  
  public NamedObject(String paramString, DynamicMBean paramDynamicMBean)
    throws MalformedObjectNameException
  {
    ObjectName localObjectName = new ObjectName(paramString);
    if (localObjectName.isPattern()) {
      throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + localObjectName.toString()));
    }
    name = localObjectName;
    object = paramDynamicMBean;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof NamedObject)) {
      return false;
    }
    NamedObject localNamedObject = (NamedObject)paramObject;
    return name.equals(localNamedObject.getName());
  }
  
  public int hashCode()
  {
    return name.hashCode();
  }
  
  public ObjectName getName()
  {
    return name;
  }
  
  public DynamicMBean getObject()
  {
    return object;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\NamedObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */