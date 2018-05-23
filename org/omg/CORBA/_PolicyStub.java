package org.omg.CORBA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _PolicyStub
  extends ObjectImpl
  implements Policy
{
  private static String[] __ids = { "IDL:omg.org/CORBA/Policy:1.0" };
  
  public _PolicyStub() {}
  
  public _PolicyStub(Delegate paramDelegate)
  {
    _set_delegate(paramDelegate);
  }
  
  public int policy_type()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("_get_policy_type", true);
      localInputStream = _invoke(localOutputStream);
      int i = PolicyTypeHelper.read(localInputStream);
      int k = i;
      return k;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      int j = policy_type();
      return j;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public Policy copy()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("copy", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = PolicyHelper.read(localInputStream);
      java.lang.Object localObject2 = localObject1;
      return (Policy)localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      java.lang.Object localObject1 = copy();
      return (Policy)localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public void destroy()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("destroy", true);
      localInputStream = _invoke(localOutputStream);
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      destroy();
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
  {
    try
    {
      String str = paramObjectInputStream.readUTF();
      Object localObject = ORB.init().string_to_object(str);
      Delegate localDelegate = ((ObjectImpl)localObject)._get_delegate();
      _set_delegate(localDelegate);
    }
    catch (IOException localIOException) {}
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
  {
    try
    {
      String str = ORB.init().object_to_string(this);
      paramObjectOutputStream.writeUTF(str);
    }
    catch (IOException localIOException) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\_PolicyStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */