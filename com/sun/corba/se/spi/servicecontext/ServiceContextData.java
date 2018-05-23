package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA_2_3.portable.InputStream;

public class ServiceContextData
{
  private Class scClass;
  private Constructor scConstructor;
  private int scId;
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint(this, paramString);
  }
  
  private void throwBadParam(String paramString, Throwable paramThrowable)
  {
    BAD_PARAM localBAD_PARAM = new BAD_PARAM(paramString);
    if (paramThrowable != null) {
      localBAD_PARAM.initCause(paramThrowable);
    }
    throw localBAD_PARAM;
  }
  
  public ServiceContextData(Class paramClass)
  {
    if (ORB.ORBInitDebug) {
      dprint("ServiceContextData constructor called for class " + paramClass);
    }
    scClass = paramClass;
    try
    {
      if (ORB.ORBInitDebug) {
        dprint("Finding constructor for " + paramClass);
      }
      Class[] arrayOfClass = new Class[2];
      arrayOfClass[0] = InputStream.class;
      arrayOfClass[1] = GIOPVersion.class;
      try
      {
        scConstructor = paramClass.getConstructor(arrayOfClass);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        throwBadParam("Class does not have an InputStream constructor", localNoSuchMethodException);
      }
      if (ORB.ORBInitDebug) {
        dprint("Finding SERVICE_CONTEXT_ID field in " + paramClass);
      }
      Field localField = null;
      try
      {
        localField = paramClass.getField("SERVICE_CONTEXT_ID");
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        throwBadParam("Class does not have a SERVICE_CONTEXT_ID member", localNoSuchFieldException);
      }
      catch (SecurityException localSecurityException)
      {
        throwBadParam("Could not access SERVICE_CONTEXT_ID member", localSecurityException);
      }
      if (ORB.ORBInitDebug) {
        dprint("Checking modifiers of SERVICE_CONTEXT_ID field in " + paramClass);
      }
      int i = localField.getModifiers();
      if ((!Modifier.isPublic(i)) || (!Modifier.isStatic(i)) || (!Modifier.isFinal(i))) {
        throwBadParam("SERVICE_CONTEXT_ID field is not public static final", null);
      }
      if (ORB.ORBInitDebug) {
        dprint("Getting value of SERVICE_CONTEXT_ID in " + paramClass);
      }
      try
      {
        scId = localField.getInt(null);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throwBadParam("SERVICE_CONTEXT_ID not convertible to int", localIllegalArgumentException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throwBadParam("Could not access value of SERVICE_CONTEXT_ID", localIllegalAccessException);
      }
    }
    catch (BAD_PARAM localBAD_PARAM)
    {
      if (ORB.ORBInitDebug) {
        dprint("Exception in ServiceContextData constructor: " + localBAD_PARAM);
      }
      throw localBAD_PARAM;
    }
    catch (Throwable localThrowable)
    {
      if (ORB.ORBInitDebug) {
        dprint("Unexpected Exception in ServiceContextData constructor: " + localThrowable);
      }
    }
    if (ORB.ORBInitDebug) {
      dprint("ServiceContextData constructor completed");
    }
  }
  
  public ServiceContext makeServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion)
  {
    Object[] arrayOfObject = new Object[2];
    arrayOfObject[0] = paramInputStream;
    arrayOfObject[1] = paramGIOPVersion;
    ServiceContext localServiceContext = null;
    try
    {
      localServiceContext = (ServiceContext)scConstructor.newInstance(arrayOfObject);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throwBadParam("InputStream constructor argument error", localIllegalArgumentException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throwBadParam("InputStream constructor argument error", localIllegalAccessException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throwBadParam("InputStream constructor called for abstract class", localInstantiationException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throwBadParam("InputStream constructor threw exception " + localInvocationTargetException.getTargetException(), localInvocationTargetException);
    }
    return localServiceContext;
  }
  
  int getId()
  {
    return scId;
  }
  
  public String toString()
  {
    return "ServiceContextData[ scClass=" + scClass + " scConstructor=" + scConstructor + " scId=" + scId + " ]";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContextData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */