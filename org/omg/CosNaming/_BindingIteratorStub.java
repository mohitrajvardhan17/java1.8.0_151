package org.omg.CosNaming;

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

public class _BindingIteratorStub
  extends ObjectImpl
  implements BindingIterator
{
  private static String[] __ids = { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  
  public _BindingIteratorStub() {}
  
  public boolean next_one(BindingHolder paramBindingHolder)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("next_one", true);
      localInputStream = _invoke(localOutputStream);
      boolean bool1 = localInputStream.read_boolean();
      value = BindingHelper.read(localInputStream);
      boolean bool3 = bool1;
      return bool3;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      boolean bool2 = next_one(paramBindingHolder);
      return bool2;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public boolean next_n(int paramInt, BindingListHolder paramBindingListHolder)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("next_n", true);
      localOutputStream.write_ulong(paramInt);
      localInputStream = _invoke(localOutputStream);
      boolean bool1 = localInputStream.read_boolean();
      value = BindingListHelper.read(localInputStream);
      boolean bool3 = bool1;
      return bool3;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      String str = localApplicationException.getId();
      throw new MARSHAL(str);
    }
    catch (RemarshalException localRemarshalException)
    {
      boolean bool2 = next_n(paramInt, paramBindingListHolder);
      return bool2;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\_BindingIteratorStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */