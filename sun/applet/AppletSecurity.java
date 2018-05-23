package sun.applet;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import sun.awt.AWTSecurityManager;
import sun.awt.AppContext;
import sun.security.util.SecurityConstants;
import sun.security.util.SecurityConstants.AWT;

public class AppletSecurity
  extends AWTSecurityManager
{
  private static Field facc = null;
  private static Field fcontext = null;
  private HashSet restrictedPackages = new HashSet();
  private boolean inThreadGroupCheck = false;
  
  public AppletSecurity()
  {
    reset();
  }
  
  public void reset()
  {
    restrictedPackages.clear();
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        Enumeration localEnumeration = System.getProperties().propertyNames();
        while (localEnumeration.hasMoreElements())
        {
          String str1 = (String)localEnumeration.nextElement();
          if ((str1 != null) && (str1.startsWith("package.restrict.access.")))
          {
            String str2 = System.getProperty(str1);
            if ((str2 != null) && (str2.equalsIgnoreCase("true")))
            {
              String str3 = str1.substring(24);
              restrictedPackages.add(str3);
            }
          }
        }
        return null;
      }
    });
  }
  
  private AppletClassLoader currentAppletClassLoader()
  {
    ClassLoader localClassLoader1 = currentClassLoader();
    if ((localClassLoader1 == null) || ((localClassLoader1 instanceof AppletClassLoader))) {
      return (AppletClassLoader)localClassLoader1;
    }
    Class[] arrayOfClass = getClassContext();
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      localClassLoader1 = arrayOfClass[i].getClassLoader();
      if ((localClassLoader1 instanceof AppletClassLoader)) {
        return (AppletClassLoader)localClassLoader1;
      }
    }
    for (i = 0; i < arrayOfClass.length; i++)
    {
      final ClassLoader localClassLoader2 = arrayOfClass[i].getClassLoader();
      if ((localClassLoader2 instanceof URLClassLoader))
      {
        localClassLoader1 = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            AccessControlContext localAccessControlContext = null;
            ProtectionDomain[] arrayOfProtectionDomain = null;
            try
            {
              localAccessControlContext = (AccessControlContext)AppletSecurity.facc.get(localClassLoader2);
              if (localAccessControlContext == null) {
                return null;
              }
              arrayOfProtectionDomain = (ProtectionDomain[])AppletSecurity.fcontext.get(localAccessControlContext);
              if (arrayOfProtectionDomain == null) {
                return null;
              }
            }
            catch (Exception localException)
            {
              throw new UnsupportedOperationException(localException);
            }
            for (int i = 0; i < arrayOfProtectionDomain.length; i++)
            {
              ClassLoader localClassLoader = arrayOfProtectionDomain[i].getClassLoader();
              if ((localClassLoader instanceof AppletClassLoader)) {
                return localClassLoader;
              }
            }
            return null;
          }
        });
        if (localClassLoader1 != null) {
          return (AppletClassLoader)localClassLoader1;
        }
      }
    }
    localClassLoader1 = Thread.currentThread().getContextClassLoader();
    if ((localClassLoader1 instanceof AppletClassLoader)) {
      return (AppletClassLoader)localClassLoader1;
    }
    return (AppletClassLoader)null;
  }
  
  protected boolean inThreadGroup(ThreadGroup paramThreadGroup)
  {
    if (currentAppletClassLoader() == null) {
      return false;
    }
    return getThreadGroup().parentOf(paramThreadGroup);
  }
  
  protected boolean inThreadGroup(Thread paramThread)
  {
    return inThreadGroup(paramThread.getThreadGroup());
  }
  
  public void checkAccess(Thread paramThread)
  {
    if ((paramThread.getState() != Thread.State.TERMINATED) && (!inThreadGroup(paramThread))) {
      checkPermission(SecurityConstants.MODIFY_THREAD_PERMISSION);
    }
  }
  
  /* Error */
  public synchronized void checkAccess(ThreadGroup paramThreadGroup)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 188	sun/applet/AppletSecurity:inThreadGroupCheck	Z
    //   4: ifeq +13 -> 17
    //   7: aload_0
    //   8: getstatic 192	sun/security/util/SecurityConstants:MODIFY_THREADGROUP_PERMISSION	Ljava/lang/RuntimePermission;
    //   11: invokevirtual 223	sun/applet/AppletSecurity:checkPermission	(Ljava/security/Permission;)V
    //   14: goto +39 -> 53
    //   17: aload_0
    //   18: iconst_1
    //   19: putfield 188	sun/applet/AppletSecurity:inThreadGroupCheck	Z
    //   22: aload_0
    //   23: aload_1
    //   24: invokevirtual 222	sun/applet/AppletSecurity:inThreadGroup	(Ljava/lang/ThreadGroup;)Z
    //   27: ifne +10 -> 37
    //   30: aload_0
    //   31: getstatic 192	sun/security/util/SecurityConstants:MODIFY_THREADGROUP_PERMISSION	Ljava/lang/RuntimePermission;
    //   34: invokevirtual 223	sun/applet/AppletSecurity:checkPermission	(Ljava/security/Permission;)V
    //   37: aload_0
    //   38: iconst_0
    //   39: putfield 188	sun/applet/AppletSecurity:inThreadGroupCheck	Z
    //   42: goto +11 -> 53
    //   45: astore_2
    //   46: aload_0
    //   47: iconst_0
    //   48: putfield 188	sun/applet/AppletSecurity:inThreadGroupCheck	Z
    //   51: aload_2
    //   52: athrow
    //   53: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	this	AppletSecurity
    //   0	54	1	paramThreadGroup	ThreadGroup
    //   45	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   17	37	45	finally
  }
  
  public void checkPackageAccess(String paramString)
  {
    super.checkPackageAccess(paramString);
    Iterator localIterator = restrictedPackages.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((paramString.equals(str)) || (paramString.startsWith(str + "."))) {
        checkPermission(new RuntimePermission("accessClassInPackage." + paramString));
      }
    }
  }
  
  public void checkAwtEventQueueAccess()
  {
    AppContext localAppContext = AppContext.getAppContext();
    AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
    if ((AppContext.isMainContext(localAppContext)) && (localAppletClassLoader != null)) {
      super.checkPermission(SecurityConstants.AWT.CHECK_AWT_EVENTQUEUE_PERMISSION);
    }
  }
  
  public ThreadGroup getThreadGroup()
  {
    AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
    ThreadGroup localThreadGroup = localAppletClassLoader == null ? null : localAppletClassLoader.getThreadGroup();
    if (localThreadGroup != null) {
      return localThreadGroup;
    }
    return super.getThreadGroup();
  }
  
  public AppContext getAppContext()
  {
    AppletClassLoader localAppletClassLoader = currentAppletClassLoader();
    if (localAppletClassLoader == null) {
      return null;
    }
    AppContext localAppContext = localAppletClassLoader.getAppContext();
    if (localAppContext == null) {
      throw new SecurityException("Applet classloader has invalid AppContext");
    }
    return localAppContext;
  }
  
  static
  {
    try
    {
      facc = URLClassLoader.class.getDeclaredField("acc");
      facc.setAccessible(true);
      fcontext = AccessControlContext.class.getDeclaredField("context");
      fcontext.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new UnsupportedOperationException(localNoSuchFieldException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletSecurity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */