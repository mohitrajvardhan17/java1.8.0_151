package com.sun.org.glassfish.gmbal;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public abstract interface ManagedObjectManager
  extends Closeable
{
  public abstract void suspendJMXRegistration();
  
  public abstract void resumeJMXRegistration();
  
  public abstract boolean isManagedObject(Object paramObject);
  
  public abstract GmbalMBean createRoot();
  
  public abstract GmbalMBean createRoot(Object paramObject);
  
  public abstract GmbalMBean createRoot(Object paramObject, String paramString);
  
  public abstract Object getRoot();
  
  public abstract GmbalMBean register(Object paramObject1, Object paramObject2, String paramString);
  
  public abstract GmbalMBean register(Object paramObject1, Object paramObject2);
  
  public abstract GmbalMBean registerAtRoot(Object paramObject, String paramString);
  
  public abstract GmbalMBean registerAtRoot(Object paramObject);
  
  public abstract void unregister(Object paramObject);
  
  public abstract ObjectName getObjectName(Object paramObject);
  
  public abstract AMXClient getAMXClient(Object paramObject);
  
  public abstract Object getObject(ObjectName paramObjectName);
  
  public abstract void stripPrefix(String... paramVarArgs);
  
  public abstract void stripPackagePrefix();
  
  public abstract String getDomain();
  
  public abstract void setMBeanServer(MBeanServer paramMBeanServer);
  
  public abstract MBeanServer getMBeanServer();
  
  public abstract void setResourceBundle(ResourceBundle paramResourceBundle);
  
  public abstract ResourceBundle getResourceBundle();
  
  public abstract void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation);
  
  public abstract void setRegistrationDebug(RegistrationDebugLevel paramRegistrationDebugLevel);
  
  public abstract void setRuntimeDebug(boolean paramBoolean);
  
  public abstract void setTypelibDebug(int paramInt);
  
  public abstract void setJMXRegistrationDebug(boolean paramBoolean);
  
  public abstract String dumpSkeleton(Object paramObject);
  
  public abstract void suppressDuplicateRootReport(boolean paramBoolean);
  
  public static enum RegistrationDebugLevel
  {
    NONE,  NORMAL,  FINE;
    
    private RegistrationDebugLevel() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\gmbal\ManagedObjectManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */