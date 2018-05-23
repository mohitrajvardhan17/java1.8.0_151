package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.ConnectIOException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.StubNotFoundException;
import java.rmi.UnknownHostException;
import java.rmi.UnmarshalException;
import java.rmi.activation.ActivateFailedException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.Operation;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;

public class ActivatableRef
  implements RemoteRef
{
  private static final long serialVersionUID = 7579060052569229166L;
  protected ActivationID id;
  protected RemoteRef ref;
  transient boolean force = false;
  private static final int MAX_RETRIES = 3;
  private static final String versionComplaint = "activation requires 1.2 stubs";
  
  public ActivatableRef() {}
  
  public ActivatableRef(ActivationID paramActivationID, RemoteRef paramRemoteRef)
  {
    id = paramActivationID;
    ref = paramRemoteRef;
  }
  
  public static Remote getStub(ActivationDesc paramActivationDesc, ActivationID paramActivationID)
    throws StubNotFoundException
  {
    String str = paramActivationDesc.getClassName();
    try
    {
      Class localClass = RMIClassLoader.loadClass(paramActivationDesc.getLocation(), str);
      ActivatableRef localActivatableRef = new ActivatableRef(paramActivationID, null);
      return Util.createProxy(localClass, localActivatableRef, false);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new StubNotFoundException("class implements an illegal remote interface", localIllegalArgumentException);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new StubNotFoundException("unable to load class: " + str, localClassNotFoundException);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new StubNotFoundException("malformed URL", localMalformedURLException);
    }
  }
  
  public Object invoke(Remote paramRemote, Method paramMethod, Object[] paramArrayOfObject, long paramLong)
    throws Exception
  {
    boolean bool = false;
    Object localObject2 = null;
    Object localObject1;
    synchronized (this)
    {
      if (ref == null)
      {
        localObject1 = activate(bool);
        bool = true;
      }
      else
      {
        localObject1 = ref;
      }
    }
    for (int i = 3; i > 0; i--)
    {
      try
      {
        return ((RemoteRef)localObject1).invoke(paramRemote, paramMethod, paramArrayOfObject, paramLong);
      }
      catch (NoSuchObjectException localNoSuchObjectException)
      {
        localObject2 = localNoSuchObjectException;
      }
      catch (ConnectException localConnectException)
      {
        localObject2 = localConnectException;
      }
      catch (UnknownHostException localUnknownHostException)
      {
        localObject2 = localUnknownHostException;
      }
      catch (ConnectIOException localConnectIOException)
      {
        localObject2 = localConnectIOException;
      }
      catch (MarshalException localMarshalException)
      {
        throw localMarshalException;
      }
      catch (ServerError localServerError)
      {
        throw localServerError;
      }
      catch (ServerException localServerException)
      {
        throw localServerException;
      }
      catch (RemoteException localRemoteException)
      {
        synchronized (this)
        {
          if (localObject1 == ref) {
            ref = null;
          }
        }
        throw localRemoteException;
      }
      if (i > 1) {
        synchronized (this)
        {
          if ((((RemoteRef)localObject1).remoteEquals(ref)) || (ref == null))
          {
            ??? = activate(bool);
            if ((((RemoteRef)???).remoteEquals((RemoteRef)localObject1)) && ((localObject2 instanceof NoSuchObjectException)) && (!bool)) {
              ??? = activate(true);
            }
            localObject1 = ???;
            bool = true;
          }
          else
          {
            localObject1 = ref;
            bool = false;
          }
        }
      }
    }
    throw ((Throwable)localObject2);
  }
  
  private synchronized RemoteRef getRef()
    throws RemoteException
  {
    if (ref == null) {
      ref = activate(false);
    }
    return ref;
  }
  
  private RemoteRef activate(boolean paramBoolean)
    throws RemoteException
  {
    assert (Thread.holdsLock(this));
    ref = null;
    try
    {
      Remote localRemote = id.activate(paramBoolean);
      ActivatableRef localActivatableRef = null;
      if ((localRemote instanceof RemoteStub))
      {
        localActivatableRef = (ActivatableRef)((RemoteStub)localRemote).getRef();
      }
      else
      {
        RemoteObjectInvocationHandler localRemoteObjectInvocationHandler = (RemoteObjectInvocationHandler)Proxy.getInvocationHandler(localRemote);
        localActivatableRef = (ActivatableRef)localRemoteObjectInvocationHandler.getRef();
      }
      ref = ref;
      return ref;
    }
    catch (ConnectException localConnectException)
    {
      throw new ConnectException("activation failed", localConnectException);
    }
    catch (RemoteException localRemoteException)
    {
      throw new ConnectIOException("activation failed", localRemoteException);
    }
    catch (UnknownObjectException localUnknownObjectException)
    {
      throw new NoSuchObjectException("object not registered");
    }
    catch (ActivationException localActivationException)
    {
      throw new ActivateFailedException("activation failed", localActivationException);
    }
  }
  
  public synchronized RemoteCall newCall(RemoteObject paramRemoteObject, Operation[] paramArrayOfOperation, int paramInt, long paramLong)
    throws RemoteException
  {
    throw new UnsupportedOperationException("activation requires 1.2 stubs");
  }
  
  public void invoke(RemoteCall paramRemoteCall)
    throws Exception
  {
    throw new UnsupportedOperationException("activation requires 1.2 stubs");
  }
  
  public void done(RemoteCall paramRemoteCall)
    throws RemoteException
  {
    throw new UnsupportedOperationException("activation requires 1.2 stubs");
  }
  
  public String getRefClass(ObjectOutput paramObjectOutput)
  {
    return "ActivatableRef";
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    RemoteRef localRemoteRef = ref;
    paramObjectOutput.writeObject(id);
    if (localRemoteRef == null)
    {
      paramObjectOutput.writeUTF("");
    }
    else
    {
      paramObjectOutput.writeUTF(localRemoteRef.getRefClass(paramObjectOutput));
      localRemoteRef.writeExternal(paramObjectOutput);
    }
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    id = ((ActivationID)paramObjectInput.readObject());
    ref = null;
    String str = paramObjectInput.readUTF();
    if (str.equals("")) {
      return;
    }
    try
    {
      Class localClass = Class.forName("sun.rmi.server." + str);
      ref = ((RemoteRef)localClass.newInstance());
      ref.readExternal(paramObjectInput);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new UnmarshalException("Unable to create remote reference", localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new UnmarshalException("Illegal access creating remote reference");
    }
  }
  
  public String remoteToString()
  {
    return Util.getUnqualifiedName(getClass()) + " [remoteRef: " + ref + "]";
  }
  
  public int remoteHashCode()
  {
    return id.hashCode();
  }
  
  public boolean remoteEquals(RemoteRef paramRemoteRef)
  {
    if ((paramRemoteRef instanceof ActivatableRef)) {
      return id.equals(id);
    }
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\ActivatableRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */