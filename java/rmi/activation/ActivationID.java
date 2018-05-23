package java.rmi.activation;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.rmi.server.RemoteObject;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteRef;
import java.rmi.server.UID;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;

public class ActivationID
  implements Serializable
{
  private transient Activator activator;
  private transient UID uid = new UID();
  private static final long serialVersionUID = -4608673054848209235L;
  private static final AccessControlContext NOPERMS_ACC;
  
  public ActivationID(Activator paramActivator)
  {
    activator = paramActivator;
  }
  
  public Remote activate(boolean paramBoolean)
    throws ActivationException, UnknownObjectException, RemoteException
  {
    try
    {
      final MarshalledObject localMarshalledObject = activator.activate(this, paramBoolean);
      (Remote)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Remote run()
          throws IOException, ClassNotFoundException
        {
          return (Remote)localMarshalledObject.get();
        }
      }, NOPERMS_ACC);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      Exception localException = localPrivilegedActionException.getException();
      if ((localException instanceof RemoteException)) {
        throw ((RemoteException)localException);
      }
      throw new UnmarshalException("activation failed", localException);
    }
  }
  
  public int hashCode()
  {
    return uid.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ActivationID))
    {
      ActivationID localActivationID = (ActivationID)paramObject;
      return (uid.equals(uid)) && (activator.equals(activator));
    }
    return false;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectOutputStream.writeObject(uid);
    RemoteRef localRemoteRef;
    if ((activator instanceof RemoteObject))
    {
      localRemoteRef = ((RemoteObject)activator).getRef();
    }
    else if (Proxy.isProxyClass(activator.getClass()))
    {
      InvocationHandler localInvocationHandler = Proxy.getInvocationHandler(activator);
      if (!(localInvocationHandler instanceof RemoteObjectInvocationHandler)) {
        throw new InvalidObjectException("unexpected invocation handler");
      }
      localRemoteRef = ((RemoteObjectInvocationHandler)localInvocationHandler).getRef();
    }
    else
    {
      throw new InvalidObjectException("unexpected activator type");
    }
    paramObjectOutputStream.writeUTF(localRemoteRef.getRefClass(paramObjectOutputStream));
    localRemoteRef.writeExternal(paramObjectOutputStream);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    uid = ((UID)paramObjectInputStream.readObject());
    try
    {
      Class localClass = Class.forName("sun.rmi.server." + paramObjectInputStream.readUTF()).asSubclass(RemoteRef.class);
      RemoteRef localRemoteRef = (RemoteRef)localClass.newInstance();
      localRemoteRef.readExternal(paramObjectInputStream);
      activator = ((Activator)Proxy.newProxyInstance(null, new Class[] { Activator.class }, new RemoteObjectInvocationHandler(localRemoteRef)));
    }
    catch (InstantiationException localInstantiationException)
    {
      throw ((IOException)new InvalidObjectException("Unable to create remote reference").initCause(localInstantiationException));
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw ((IOException)new InvalidObjectException("Unable to create remote reference").initCause(localIllegalAccessException));
    }
  }
  
  static
  {
    Permissions localPermissions = new Permissions();
    ProtectionDomain[] arrayOfProtectionDomain = { new ProtectionDomain(null, localPermissions) };
    NOPERMS_ACC = new AccessControlContext(arrayOfProtectionDomain);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\activation\ActivationID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */