package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _ServerManagerStub
  extends ObjectImpl
  implements ServerManager
{
  private static String[] __ids = { "IDL:activation/ServerManager:1.0", "IDL:activation/Activator:1.0", "IDL:activation/Locator:1.0" };
  
  public _ServerManagerStub() {}
  
  public void active(int paramInt, Server paramServer)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("active", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      ServerHelper.write(localOutputStream, paramServer);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      active(paramInt, paramServer);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
    throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("registerEndpoints", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      ORBidHelper.write(localOutputStream, paramString);
      EndpointInfoListHelper.write(localOutputStream, paramArrayOfEndPointInfo);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0")) {
        throw NoSuchEndPointHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ORBAlreadyRegistered:1.0")) {
        throw ORBAlreadyRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      registerEndpoints(paramInt, paramString, paramArrayOfEndPointInfo);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public int[] getActiveServers()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getActiveServers", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ServerIdsHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (int[])localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = getActiveServers();
      return (int[])localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void activate(int paramInt)
    throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("activate", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerAlreadyActive:1.0")) {
        throw ServerAlreadyActiveHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerHeldDown:1.0")) {
        throw ServerHeldDownHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      activate(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void shutdown(int paramInt)
    throws ServerNotActive, ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("shutdown", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotActive:1.0")) {
        throw ServerNotActiveHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      shutdown(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void install(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("install", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerHeldDown:1.0")) {
        throw ServerHeldDownHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerAlreadyInstalled:1.0")) {
        throw ServerAlreadyInstalledHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      install(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String[] getORBNames(int paramInt)
    throws ServerNotRegistered
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getORBNames", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ORBidListHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (String[])localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = getORBNames(paramInt);
      return (String[])localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void uninstall(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("uninstall", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerHeldDown:1.0")) {
        throw ServerHeldDownHelper.read(localInputStream);
      }
      if (str.equals("IDL:activation/ServerAlreadyUninstalled:1.0")) {
        throw ServerAlreadyUninstalledHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      uninstall(paramInt);
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public ServerLocation locateServer(int paramInt, String paramString)
    throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("locateServer", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      localOutputStream.write_string(paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ServerLocationHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (ServerLocation)localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:activation/NoSuchEndPoint:1.0")) {
        throw NoSuchEndPointHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:activation/ServerHeldDown:1.0")) {
        throw ServerHeldDownHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = locateServer(paramInt, paramString);
      return (ServerLocation)localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public ServerLocationPerORB locateServerForORB(int paramInt, String paramString)
    throws InvalidORBid, ServerNotRegistered, ServerHeldDown
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("locateServerForORB", true);
      ServerIdHelper.write(localOutputStream, paramInt);
      ORBidHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ServerLocationPerORBHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (ServerLocationPerORB)localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      if (((String)localObject1).equals("IDL:activation/InvalidORBid:1.0")) {
        throw InvalidORBidHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:activation/ServerNotRegistered:1.0")) {
        throw ServerNotRegisteredHelper.read(localInputStream);
      }
      if (((String)localObject1).equals("IDL:activation/ServerHeldDown:1.0")) {
        throw ServerHeldDownHelper.read(localInputStream);
      }
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = locateServerForORB(paramInt, paramString);
      return (ServerLocationPerORB)localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public int getEndpoint(String paramString)
    throws NoSuchEndPoint
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getEndpoint", true);
      localOutputStream.write_string(paramString);
      localInputStream = _invoke(localOutputStream);
      int i = TCPPortHelper.read(localInputStream);
      int k = i;
      return k;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0")) {
        throw NoSuchEndPointHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      int j = getEndpoint(paramString);
      return j;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString)
    throws NoSuchEndPoint
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("getServerPortForType", true);
      ServerLocationPerORBHelper.write(localOutputStream, paramServerLocationPerORB);
      localOutputStream.write_string(paramString);
      localInputStream = _invoke(localOutputStream);
      int i = TCPPortHelper.read(localInputStream);
      int k = i;
      return k;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      if (str.equals("IDL:activation/NoSuchEndPoint:1.0")) {
        throw NoSuchEndPointHelper.read(localInputStream);
      }
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      int j = getServerPortForType(paramServerLocationPerORB, paramString);
      return j;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException
  {
    String str = paramObjectInputStream.readUTF();
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      org.omg.CORBA.Object localObject = localORB.string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    finally
    {
      localORB.destroy();
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    String[] arrayOfString = null;
    Properties localProperties = null;
    ORB localORB = ORB.init(arrayOfString, localProperties);
    try
    {
      String str = localORB.object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    finally
    {
      localORB.destroy();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_ServerManagerStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */