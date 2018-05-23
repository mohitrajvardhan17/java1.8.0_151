package com.sun.jndi.rmi.registry;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import javax.naming.NamingException;
import javax.naming.Reference;

public final class ReferenceWrapper_Stub
  extends RemoteStub
  implements RemoteReference, Remote
{
  private static final long serialVersionUID = 2L;
  private static Method $method_getReference_0;
  
  static
  {
    try
    {
      $method_getReference_0 = RemoteReference.class.getMethod("getReference", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError("stub class initialization failed");
    }
  }
  
  public ReferenceWrapper_Stub(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
  }
  
  public Reference getReference()
    throws RemoteException, NamingException
  {
    try
    {
      Object localObject = ref.invoke(this, $method_getReference_0, null, 3529874867989176284L);
      return (Reference)localObject;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException;
    }
    catch (NamingException localNamingException)
    {
      throw localNamingException;
    }
    catch (Exception localException)
    {
      throw new UnexpectedException("undeclared checked exception", localException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\rmi\registry\ReferenceWrapper_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       0.7.1
 */