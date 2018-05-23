package javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.RuntimeOperationsException;

public abstract interface ModelMBeanInfo
{
  public abstract Descriptor[] getDescriptors(String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void setDescriptors(Descriptor[] paramArrayOfDescriptor)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract Descriptor getDescriptor(String paramString1, String paramString2)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void setDescriptor(Descriptor paramDescriptor, String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract Descriptor getMBeanDescriptor()
    throws MBeanException, RuntimeOperationsException;
  
  public abstract void setMBeanDescriptor(Descriptor paramDescriptor)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract ModelMBeanAttributeInfo getAttribute(String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract ModelMBeanOperationInfo getOperation(String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract ModelMBeanNotificationInfo getNotification(String paramString)
    throws MBeanException, RuntimeOperationsException;
  
  public abstract Object clone();
  
  public abstract MBeanAttributeInfo[] getAttributes();
  
  public abstract String getClassName();
  
  public abstract MBeanConstructorInfo[] getConstructors();
  
  public abstract String getDescription();
  
  public abstract MBeanNotificationInfo[] getNotifications();
  
  public abstract MBeanOperationInfo[] getOperations();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\management\modelmbean\ModelMBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */