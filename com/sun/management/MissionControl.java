package com.sun.management;

import com.sun.jmx.mbeanserver.Util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

public final class MissionControl
  extends StandardMBean
  implements MissionControlMXBean
{
  private static final ObjectName MBEAN_NAME = Util.newObjectName("com.sun.management:type=MissionControl");
  private MBeanServer server;
  
  public MissionControl()
  {
    super(MissionControlMXBean.class, true);
  }
  
  public ObjectName preRegister(MBeanServer paramMBeanServer, ObjectName paramObjectName)
    throws Exception
  {
    server = paramMBeanServer;
    return MBEAN_NAME;
  }
  
  public void unregisterMBeans()
  {
    doPrivileged(new PrivilegedExceptionAction()
    {
      public Void run()
      {
        MissionControl.FlightRecorderHelper.unregisterWithMBeanServer(server);
        return null;
      }
    });
  }
  
  public void registerMBeans()
  {
    doPrivileged(new PrivilegedExceptionAction()
    {
      public Void run()
        throws MalformedObjectNameException
      {
        if (!server.isRegistered(new ObjectName("com.oracle.jrockit:type=FlightRecorder"))) {
          try
          {
            MissionControl.FlightRecorderHelper.registerWithMBeanServer(server);
          }
          catch (IllegalStateException localIllegalStateException) {}
        }
        return null;
      }
    });
  }
  
  private void doPrivileged(PrivilegedExceptionAction<Void> paramPrivilegedExceptionAction)
  {
    try
    {
      AccessController.doPrivileged(paramPrivilegedExceptionAction);
    }
    catch (PrivilegedActionException localPrivilegedActionException) {}
  }
  
  private static class FlightRecorderHelper
  {
    static final String MBEAN_NAME = "com.oracle.jrockit:type=FlightRecorder";
    private static final Class<?> FLIGHTRECORDER_CLASS = getClass("com.oracle.jrockit.jfr.FlightRecorder");
    private static final Method REGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "registerWithMBeanServer", new Class[] { MBeanServer.class });
    private static final Method UNREGISTERWITHMBEANSERVER_METHOD = getMethod(FLIGHTRECORDER_CLASS, "unregisterWithMBeanServer", new Class[] { MBeanServer.class });
    
    private FlightRecorderHelper() {}
    
    private static Class<?> getClass(String paramString)
    {
      try
      {
        return Class.forName(paramString, true, FlightRecorderHelper.class.getClassLoader());
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new InternalError("jfr.jar missing?", localClassNotFoundException);
      }
    }
    
    private static Method getMethod(Class<?> paramClass, String paramString, Class<?>... paramVarArgs)
    {
      try
      {
        return paramClass.getMethod(paramString, paramVarArgs);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throw new InternalError(localNoSuchMethodException);
      }
    }
    
    private static Object invokeStatic(Method paramMethod, Object... paramVarArgs)
    {
      try
      {
        return paramMethod.invoke(null, paramVarArgs);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Throwable localThrowable = localInvocationTargetException.getCause();
        if ((localThrowable instanceof RuntimeException)) {
          throw ((RuntimeException)localThrowable);
        }
        if ((localThrowable instanceof Error)) {
          throw ((Error)localThrowable);
        }
        throw new InternalError(localThrowable);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new InternalError(localIllegalAccessException);
      }
    }
    
    static void registerWithMBeanServer(MBeanServer paramMBeanServer)
    {
      invokeStatic(REGISTERWITHMBEANSERVER_METHOD, new Object[] { paramMBeanServer });
    }
    
    static void unregisterWithMBeanServer(MBeanServer paramMBeanServer)
    {
      invokeStatic(UNREGISTERWITHMBEANSERVER_METHOD, new Object[] { paramMBeanServer });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\management\MissionControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */