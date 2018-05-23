package com.sun.org.glassfish.gmbal;

import java.util.Map;

@ManagedObject
@Description("Base interface for any MBean that works in the AMX framework")
public abstract interface AMXMBeanInterface
{
  public abstract Map<String, ?> getMeta();
  
  @ManagedAttribute(id="Name")
  @Description("Return the name of this MBean.")
  public abstract String getName();
  
  @ManagedAttribute(id="Parent")
  @Description("The container that contains this MBean")
  public abstract AMXMBeanInterface getParent();
  
  @ManagedAttribute(id="Children")
  @Description("All children of this AMX MBean")
  public abstract AMXMBeanInterface[] getChildren();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\AMXMBeanInterface.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */