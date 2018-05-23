package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orbutil.proxy.LinkedInvocationHandler;
import com.sun.corba.se.spi.presentation.rmi.DynamicMethodMarshaller;
import com.sun.corba.se.spi.presentation.rmi.IDLNameTranslator;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager.ClassData;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.rmi.CORBA.Util;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class StubInvocationHandlerImpl
  implements LinkedInvocationHandler
{
  private transient PresentationManager.ClassData classData;
  private transient PresentationManager pm;
  private transient org.omg.CORBA.Object stub;
  private transient Proxy self;
  
  public void setProxy(Proxy paramProxy)
  {
    self = paramProxy;
  }
  
  public Proxy getProxy()
  {
    return self;
  }
  
  public StubInvocationHandlerImpl(PresentationManager paramPresentationManager, PresentationManager.ClassData paramClassData, org.omg.CORBA.Object paramObject)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new DynamicAccessPermission("access"));
    }
    classData = paramClassData;
    pm = paramPresentationManager;
    stub = paramObject;
  }
  
  private boolean isLocal()
  {
    boolean bool = false;
    Delegate localDelegate = StubAdapter.getDelegate(stub);
    if ((localDelegate instanceof CorbaClientDelegate))
    {
      CorbaClientDelegate localCorbaClientDelegate = (CorbaClientDelegate)localDelegate;
      ContactInfoList localContactInfoList = localCorbaClientDelegate.getContactInfoList();
      if ((localContactInfoList instanceof CorbaContactInfoList))
      {
        CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localContactInfoList;
        LocalClientRequestDispatcher localLocalClientRequestDispatcher = localCorbaContactInfoList.getLocalClientRequestDispatcher();
        bool = localLocalClientRequestDispatcher.useLocalInvocation(null);
      }
    }
    return bool;
  }
  
  public Object invoke(Object paramObject, final Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    String str = classData.getIDLNameTranslator().getIDLName(paramMethod);
    DynamicMethodMarshaller localDynamicMethodMarshaller = pm.getDynamicMethodMarshaller(paramMethod);
    Delegate localDelegate = null;
    try
    {
      localDelegate = StubAdapter.getDelegate(stub);
    }
    catch (SystemException localSystemException1)
    {
      throw Util.mapSystemException(localSystemException1);
    }
    Object localObject1;
    if (!isLocal()) {
      try
      {
        InputStream localInputStream = null;
        try
        {
          OutputStream localOutputStream = (OutputStream)localDelegate.request(stub, str, true);
          localDynamicMethodMarshaller.writeArguments(localOutputStream, paramArrayOfObject);
          localInputStream = (InputStream)localDelegate.invoke(stub, localOutputStream);
          localObject1 = localDynamicMethodMarshaller.readResult(localInputStream);
          return localObject1;
        }
        catch (ApplicationException localApplicationException)
        {
          throw localDynamicMethodMarshaller.readException(localApplicationException);
        }
        catch (RemarshalException localRemarshalException)
        {
          localObject1 = invoke(paramObject, paramMethod, paramArrayOfObject);
          return localObject1;
        }
        finally
        {
          localDelegate.releaseReply(stub, localInputStream);
        }
        localORB = (ORB)localDelegate.orb(stub);
      }
      catch (SystemException localSystemException2)
      {
        throw Util.mapSystemException(localSystemException2);
      }
    }
    ORB localORB;
    ServantObject localServantObject = localDelegate.servant_preinvoke(stub, str, paramMethod.getDeclaringClass());
    if (localServantObject == null) {
      return invoke(stub, paramMethod, paramArrayOfObject);
    }
    try
    {
      localObject1 = localDynamicMethodMarshaller.copyArguments(paramArrayOfObject, localORB);
      if (!paramMethod.isAccessible()) {
        AccessController.doPrivileged(new PrivilegedAction()
        {
          public Object run()
          {
            paramMethod.setAccessible(true);
            return null;
          }
        });
      }
      localObject3 = paramMethod.invoke(servant, (Object[])localObject1);
      localObject4 = localDynamicMethodMarshaller.copyResult(localObject3, localORB);
      return localObject4;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      Object localObject3 = localInvocationTargetException.getCause();
      Object localObject4 = (Throwable)Util.copyObject(localObject3, localORB);
      if (localDynamicMethodMarshaller.isDeclaredException((Throwable)localObject4)) {
        throw ((Throwable)localObject4);
      }
      throw Util.wrapException((Throwable)localObject4);
    }
    catch (Throwable localThrowable)
    {
      if ((localThrowable instanceof ThreadDeath)) {
        throw ((ThreadDeath)localThrowable);
      }
      throw Util.wrapException(localThrowable);
    }
    finally
    {
      localDelegate.servant_postinvoke(stub, localServantObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\presentation\rmi\StubInvocationHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */