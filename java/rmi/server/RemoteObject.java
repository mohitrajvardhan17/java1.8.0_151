package java.rmi.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import sun.rmi.server.Util;
import sun.rmi.transport.ObjectTable;

public abstract class RemoteObject
  implements Remote, Serializable
{
  protected transient RemoteRef ref;
  private static final long serialVersionUID = -3215090123894869218L;
  
  protected RemoteObject()
  {
    ref = null;
  }
  
  protected RemoteObject(RemoteRef paramRemoteRef)
  {
    ref = paramRemoteRef;
  }
  
  public RemoteRef getRef()
  {
    return ref;
  }
  
  public static Remote toStub(Remote paramRemote)
    throws NoSuchObjectException
  {
    if (((paramRemote instanceof RemoteStub)) || ((paramRemote != null) && (Proxy.isProxyClass(paramRemote.getClass())) && ((Proxy.getInvocationHandler(paramRemote) instanceof RemoteObjectInvocationHandler)))) {
      return paramRemote;
    }
    return ObjectTable.getStub(paramRemote);
  }
  
  public int hashCode()
  {
    return ref == null ? super.hashCode() : ref.remoteHashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RemoteObject))
    {
      if (ref == null) {
        return paramObject == this;
      }
      return ref.remoteEquals(ref);
    }
    if (paramObject != null) {
      return paramObject.equals(this);
    }
    return false;
  }
  
  public String toString()
  {
    String str = Util.getUnqualifiedName(getClass());
    return str + "[" + ref.remoteToString() + "]";
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException, ClassNotFoundException
  {
    if (ref == null) {
      throw new MarshalException("Invalid remote object");
    }
    String str = ref.getRefClass(paramObjectOutputStream);
    if ((str == null) || (str.length() == 0))
    {
      paramObjectOutputStream.writeUTF("");
      paramObjectOutputStream.writeObject(ref);
    }
    else
    {
      paramObjectOutputStream.writeUTF(str);
      ref.writeExternal(paramObjectOutputStream);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    String str1 = paramObjectInputStream.readUTF();
    if ((str1 == null) || (str1.length() == 0))
    {
      ref = ((RemoteRef)paramObjectInputStream.readObject());
    }
    else
    {
      String str2 = "sun.rmi.server." + str1;
      Class localClass = Class.forName(str2);
      try
      {
        ref = ((RemoteRef)localClass.newInstance());
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new ClassNotFoundException(str2, localInstantiationException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new ClassNotFoundException(str2, localIllegalAccessException);
      }
      catch (ClassCastException localClassCastException)
      {
        throw new ClassNotFoundException(str2, localClassCastException);
      }
      ref.readExternal(paramObjectInputStream);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */