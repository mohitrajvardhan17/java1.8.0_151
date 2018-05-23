package org.omg.stub.javax.management.remote.rmi;

import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.SerializablePermission;
import java.rmi.RemoteException;
import java.rmi.UnexpectedException;
import javax.management.remote.rmi.RMIConnection;
import javax.management.remote.rmi.RMIServer;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Util;
import javax.rmi.PortableRemoteObject;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.CORBA.portable.ServantObject;

public class _RMIServer_Stub
  extends Stub
  implements RMIServer
{
  private static final String[] _type_ids = { "RMI:javax.management.remote.rmi.RMIServer:0000000000000000" };
  private transient boolean _instantiated = false;
  
  public _RMIServer_Stub()
  {
    this(checkPermission());
  }
  
  private _RMIServer_Stub(Void paramVoid) {}
  
  public String[] _ids()
  {
    return (String[])_type_ids.clone();
  }
  
  private static Void checkPermission()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SerializablePermission("enableSubclassImplementation"));
    }
    return null;
  }
  
  public String getVersion()
    throws RemoteException
  {
    if ((System.getSecurityManager() != null) && (!_instantiated)) {
      throw new IOError(new IOException("InvalidObject "));
    }
    String str;
    Object localObject5;
    if (!Util.isLocal(this)) {
      try
      {
        org.omg.CORBA_2_3.portable.InputStream localInputStream = null;
        try
        {
          OutputStream localOutputStream = _request("_get_version", true);
          localInputStream = (org.omg.CORBA_2_3.portable.InputStream)_invoke(localOutputStream);
          str = (String)localInputStream.read_value(String.class);
          return str;
        }
        catch (ApplicationException localApplicationException)
        {
          localInputStream = (org.omg.CORBA_2_3.portable.InputStream)localApplicationException.getInputStream();
          localObject5 = localInputStream.read_string();
          throw new UnexpectedException((String)localObject5);
        }
        catch (RemarshalException localRemarshalException)
        {
          str = getVersion();
          return str;
        }
        finally
        {
          _releaseReply(localInputStream);
        }
      }
      catch (SystemException localSystemException)
      {
        throw Util.mapSystemException(localSystemException);
      }
    }
    ServantObject localServantObject = _servant_preinvoke("_get_version", RMIServer.class);
    if (localServantObject == null) {
      return getVersion();
    }
    try
    {
      str = ((RMIServer)servant).getVersion();
      return str;
    }
    catch (Throwable localThrowable)
    {
      localObject5 = (Throwable)Util.copyObject(localThrowable, _orb());
      throw Util.wrapException((Throwable)localObject5);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public RMIConnection newClient(Object paramObject)
    throws IOException
  {
    if ((System.getSecurityManager() != null) && (!_instantiated)) {
      throw new IOError(new IOException("InvalidObject "));
    }
    RMIConnection localRMIConnection;
    Object localObject6;
    if (!Util.isLocal(this)) {
      try
      {
        org.omg.CORBA_2_3.portable.InputStream localInputStream = null;
        try
        {
          OutputStream localOutputStream = _request("newClient", true);
          Util.writeAny(localOutputStream, paramObject);
          localInputStream = (org.omg.CORBA_2_3.portable.InputStream)_invoke(localOutputStream);
          localRMIConnection = (RMIConnection)PortableRemoteObject.narrow(localInputStream.read_Object(), RMIConnection.class);
          return localRMIConnection;
        }
        catch (ApplicationException localApplicationException)
        {
          localInputStream = (org.omg.CORBA_2_3.portable.InputStream)localApplicationException.getInputStream();
          localObject6 = localInputStream.read_string();
          if (((String)localObject6).equals("IDL:java/io/IOEx:1.0")) {
            throw ((IOException)localInputStream.read_value(IOException.class));
          }
          throw new UnexpectedException((String)localObject6);
        }
        catch (RemarshalException localRemarshalException)
        {
          localRMIConnection = newClient(paramObject);
          return localRMIConnection;
        }
        finally
        {
          _releaseReply(localInputStream);
        }
      }
      catch (SystemException localSystemException)
      {
        throw Util.mapSystemException(localSystemException);
      }
    }
    ServantObject localServantObject = _servant_preinvoke("newClient", RMIServer.class);
    if (localServantObject == null) {
      return newClient(paramObject);
    }
    try
    {
      Object localObject5 = Util.copyObject(paramObject, _orb());
      localObject6 = ((RMIServer)servant).newClient(localObject5);
      localRMIConnection = (RMIConnection)Util.copyObject(localObject6, _orb());
      return localRMIConnection;
    }
    catch (Throwable localThrowable)
    {
      localObject6 = (Throwable)Util.copyObject(localThrowable, _orb());
      if ((localObject6 instanceof IOException)) {
        throw ((IOException)localObject6);
      }
      throw Util.wrapException((Throwable)localObject6);
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    checkPermission();
    paramObjectInputStream.defaultReadObject();
    _instantiated = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\stub\javax\management\remote\rmi\_RMIServer_Stub.class
 * Java compiler version: 1 (45.3)
 * JD-Core Version:       0.7.1
 */