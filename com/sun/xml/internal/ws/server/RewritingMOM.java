package com.sun.xml.internal.ws.server;

import com.sun.org.glassfish.gmbal.AMXClient;
import com.sun.org.glassfish.gmbal.GmbalMBean;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.org.glassfish.gmbal.ManagedObjectManager.RegistrationDebugLevel;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ResourceBundle;
import javax.management.MBeanServer;
import javax.management.ObjectName;

class RewritingMOM
  implements ManagedObjectManager
{
  private final ManagedObjectManager mom;
  private static final String gmbalQuotingCharsRegex = "\n|\\|\"|\\*|\\?|:|=|,";
  private static final String replacementChar = "-";
  
  RewritingMOM(ManagedObjectManager paramManagedObjectManager)
  {
    mom = paramManagedObjectManager;
  }
  
  private String rewrite(String paramString)
  {
    return paramString.replaceAll("\n|\\|\"|\\*|\\?|:|=|,", "-");
  }
  
  public void suspendJMXRegistration()
  {
    mom.suspendJMXRegistration();
  }
  
  public void resumeJMXRegistration()
  {
    mom.resumeJMXRegistration();
  }
  
  public GmbalMBean createRoot()
  {
    return mom.createRoot();
  }
  
  public GmbalMBean createRoot(Object paramObject)
  {
    return mom.createRoot(paramObject);
  }
  
  public GmbalMBean createRoot(Object paramObject, String paramString)
  {
    return mom.createRoot(paramObject, rewrite(paramString));
  }
  
  public Object getRoot()
  {
    return mom.getRoot();
  }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2, String paramString)
  {
    return mom.register(paramObject1, paramObject2, rewrite(paramString));
  }
  
  public GmbalMBean register(Object paramObject1, Object paramObject2)
  {
    return mom.register(paramObject1, paramObject2);
  }
  
  public GmbalMBean registerAtRoot(Object paramObject, String paramString)
  {
    return mom.registerAtRoot(paramObject, rewrite(paramString));
  }
  
  public GmbalMBean registerAtRoot(Object paramObject)
  {
    return mom.registerAtRoot(paramObject);
  }
  
  public void unregister(Object paramObject)
  {
    mom.unregister(paramObject);
  }
  
  public ObjectName getObjectName(Object paramObject)
  {
    return mom.getObjectName(paramObject);
  }
  
  public AMXClient getAMXClient(Object paramObject)
  {
    return mom.getAMXClient(paramObject);
  }
  
  public Object getObject(ObjectName paramObjectName)
  {
    return mom.getObject(paramObjectName);
  }
  
  public void stripPrefix(String... paramVarArgs)
  {
    mom.stripPrefix(paramVarArgs);
  }
  
  public void stripPackagePrefix()
  {
    mom.stripPackagePrefix();
  }
  
  public String getDomain()
  {
    return mom.getDomain();
  }
  
  public void setMBeanServer(MBeanServer paramMBeanServer)
  {
    mom.setMBeanServer(paramMBeanServer);
  }
  
  public MBeanServer getMBeanServer()
  {
    return mom.getMBeanServer();
  }
  
  public void setResourceBundle(ResourceBundle paramResourceBundle)
  {
    mom.setResourceBundle(paramResourceBundle);
  }
  
  public ResourceBundle getResourceBundle()
  {
    return mom.getResourceBundle();
  }
  
  public void addAnnotation(AnnotatedElement paramAnnotatedElement, Annotation paramAnnotation)
  {
    mom.addAnnotation(paramAnnotatedElement, paramAnnotation);
  }
  
  public void setRegistrationDebug(ManagedObjectManager.RegistrationDebugLevel paramRegistrationDebugLevel)
  {
    mom.setRegistrationDebug(paramRegistrationDebugLevel);
  }
  
  public void setRuntimeDebug(boolean paramBoolean)
  {
    mom.setRuntimeDebug(paramBoolean);
  }
  
  public void setTypelibDebug(int paramInt)
  {
    mom.setTypelibDebug(paramInt);
  }
  
  public String dumpSkeleton(Object paramObject)
  {
    return mom.dumpSkeleton(paramObject);
  }
  
  public void suppressDuplicateRootReport(boolean paramBoolean)
  {
    mom.suppressDuplicateRootReport(paramBoolean);
  }
  
  public void close()
    throws IOException
  {
    mom.close();
  }
  
  public void setJMXRegistrationDebug(boolean paramBoolean)
  {
    mom.setJMXRegistrationDebug(paramBoolean);
  }
  
  public boolean isManagedObject(Object paramObject)
  {
    return mom.isManagedObject(paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\RewritingMOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */