package com.sun.corba.se.spi.activation;

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

public class _ServerStub
  extends ObjectImpl
  implements Server
{
  private static String[] __ids = { "IDL:activation/Server:1.0" };
  
  public _ServerStub() {}
  
  public void shutdown()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("shutdown", true);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      shutdown();
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void install()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("install", true);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      install();
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void uninstall()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("uninstall", true);
      localInputStream = _invoke(localOutputStream);
      return;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      uninstall();
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_ServerStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */