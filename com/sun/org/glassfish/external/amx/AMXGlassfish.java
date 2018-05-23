package com.sun.org.glassfish.external.amx;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

public final class AMXGlassfish
{
  public static final String DEFAULT_JMX_DOMAIN = "amx";
  public static final AMXGlassfish DEFAULT = new AMXGlassfish("amx");
  private final String mJMXDomain;
  private final ObjectName mDomainRoot;
  
  public AMXGlassfish(String paramString)
  {
    mJMXDomain = paramString;
    mDomainRoot = newObjectName("", "domain-root", null);
  }
  
  public static String getGlassfishVersion()
  {
    String str = System.getProperty("glassfish.version");
    return str;
  }
  
  public String amxJMXDomain()
  {
    return mJMXDomain;
  }
  
  public String amxSupportDomain()
  {
    return amxJMXDomain() + "-support";
  }
  
  public String dasName()
  {
    return "server";
  }
  
  public String dasConfig()
  {
    return dasName() + "-config";
  }
  
  public ObjectName domainRoot()
  {
    return mDomainRoot;
  }
  
  public ObjectName monitoringRoot()
  {
    return newObjectName("/", "mon", null);
  }
  
  public ObjectName serverMon(String paramString)
  {
    return newObjectName("/mon", "server-mon", paramString);
  }
  
  public ObjectName serverMonForDAS()
  {
    return serverMon("server");
  }
  
  public ObjectName newObjectName(String paramString1, String paramString2, String paramString3)
  {
    String str = prop("pp", paramString1) + "," + prop("type", paramString2);
    if (paramString3 != null) {
      str = str + "," + prop("name", paramString3);
    }
    return newObjectName(str);
  }
  
  public ObjectName newObjectName(String paramString)
  {
    String str = paramString;
    if (!str.startsWith(amxJMXDomain())) {
      str = amxJMXDomain() + ":" + str;
    }
    return AMXUtil.newObjectName(str);
  }
  
  private static String prop(String paramString1, String paramString2)
  {
    return paramString1 + "=" + paramString2;
  }
  
  public ObjectName getBootAMXMBeanObjectName()
  {
    return AMXUtil.newObjectName(amxSupportDomain() + ":type=boot-amx");
  }
  
  public void invokeBootAMX(MBeanServerConnection paramMBeanServerConnection)
  {
    try
    {
      paramMBeanServerConnection.invoke(getBootAMXMBeanObjectName(), "bootAMX", null, null);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      throw new RuntimeException(localException);
    }
  }
  
  private static void invokeWaitAMXReady(MBeanServerConnection paramMBeanServerConnection, ObjectName paramObjectName)
  {
    try
    {
      paramMBeanServerConnection.invoke(paramObjectName, "waitAMXReady", null, null);
    }
    catch (Exception localException)
    {
      throw new RuntimeException(localException);
    }
  }
  
  public <T extends MBeanListener.Callback> MBeanListener<T> listenForDomainRoot(MBeanServerConnection paramMBeanServerConnection, T paramT)
  {
    MBeanListener localMBeanListener = new MBeanListener(paramMBeanServerConnection, domainRoot(), paramT);
    localMBeanListener.startListening();
    return localMBeanListener;
  }
  
  public ObjectName waitAMXReady(MBeanServerConnection paramMBeanServerConnection)
  {
    WaitForDomainRootListenerCallback localWaitForDomainRootListenerCallback = new WaitForDomainRootListenerCallback(paramMBeanServerConnection);
    listenForDomainRoot(paramMBeanServerConnection, localWaitForDomainRootListenerCallback);
    localWaitForDomainRootListenerCallback.await();
    return localWaitForDomainRootListenerCallback.getRegistered();
  }
  
  public <T extends MBeanListener.Callback> MBeanListener<T> listenForBootAMX(MBeanServerConnection paramMBeanServerConnection, T paramT)
  {
    MBeanListener localMBeanListener = new MBeanListener(paramMBeanServerConnection, getBootAMXMBeanObjectName(), paramT);
    localMBeanListener.startListening();
    return localMBeanListener;
  }
  
  public ObjectName bootAMX(MBeanServerConnection paramMBeanServerConnection)
    throws IOException
  {
    ObjectName localObjectName = domainRoot();
    if (!paramMBeanServerConnection.isRegistered(localObjectName))
    {
      BootAMXCallback localBootAMXCallback = new BootAMXCallback(paramMBeanServerConnection);
      listenForBootAMX(paramMBeanServerConnection, localBootAMXCallback);
      localBootAMXCallback.await();
      invokeBootAMX(paramMBeanServerConnection);
      WaitForDomainRootListenerCallback localWaitForDomainRootListenerCallback = new WaitForDomainRootListenerCallback(paramMBeanServerConnection);
      listenForDomainRoot(paramMBeanServerConnection, localWaitForDomainRootListenerCallback);
      localWaitForDomainRootListenerCallback.await();
      invokeWaitAMXReady(paramMBeanServerConnection, localObjectName);
    }
    else
    {
      invokeWaitAMXReady(paramMBeanServerConnection, localObjectName);
    }
    return localObjectName;
  }
  
  public ObjectName bootAMX(MBeanServer paramMBeanServer)
  {
    try
    {
      return bootAMX(paramMBeanServer);
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public static class BootAMXCallback
    extends MBeanListener.CallbackImpl
  {
    private final MBeanServerConnection mConn;
    
    public BootAMXCallback(MBeanServerConnection paramMBeanServerConnection)
    {
      mConn = paramMBeanServerConnection;
    }
    
    public void mbeanRegistered(ObjectName paramObjectName, MBeanListener paramMBeanListener)
    {
      super.mbeanRegistered(paramObjectName, paramMBeanListener);
      mLatch.countDown();
    }
  }
  
  private static final class WaitForDomainRootListenerCallback
    extends MBeanListener.CallbackImpl
  {
    private final MBeanServerConnection mConn;
    
    public WaitForDomainRootListenerCallback(MBeanServerConnection paramMBeanServerConnection)
    {
      mConn = paramMBeanServerConnection;
    }
    
    public void mbeanRegistered(ObjectName paramObjectName, MBeanListener paramMBeanListener)
    {
      super.mbeanRegistered(paramObjectName, paramMBeanListener);
      AMXGlassfish.invokeWaitAMXReady(mConn, paramObjectName);
      mLatch.countDown();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\glassfish\external\amx\AMXGlassfish.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */