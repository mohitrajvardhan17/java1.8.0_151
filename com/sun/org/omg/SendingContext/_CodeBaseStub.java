package com.sun.org.omg.SendingContext;

import com.sun.org.omg.CORBA.Repository;
import com.sun.org.omg.CORBA.RepositoryHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescription;
import com.sun.org.omg.CORBA.ValueDefPackage.FullValueDescriptionHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.URLSeqHelper;
import com.sun.org.omg.SendingContext.CodeBasePackage.ValueDescSeqHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.RemarshalException;

public class _CodeBaseStub
  extends ObjectImpl
  implements CodeBase
{
  private static String[] __ids = { "IDL:omg.org/SendingContext/CodeBase:1.0", "IDL:omg.org/SendingContext/RunTime:1.0" };
  
  public _CodeBaseStub() {}
  
  public _CodeBaseStub(Delegate paramDelegate)
  {
    _set_delegate(paramDelegate);
  }
  
  public Repository get_ir()
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("get_ir", true);
      localInputStream = _invoke(localOutputStream);
      localObject1 = RepositoryHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (Repository)localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = get_ir();
      return (Repository)localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String implementation(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("implementation", true);
      RepositoryIdHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      str1 = URLHelper.read(localInputStream);
      String str2 = str1;
      return str2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      str1 = localApplicationException.getId();
      throw new MARSHAL(str1);
    }
    catch (RemarshalException localRemarshalException)
    {
      String str1 = implementation(paramString);
      return str1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String[] implementations(String[] paramArrayOfString)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("implementations", true);
      RepositoryIdSeqHelper.write(localOutputStream, paramArrayOfString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = URLSeqHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (String[])localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = implementations(paramArrayOfString);
      return (String[])localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public FullValueDescription meta(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("meta", true);
      RepositoryIdHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = FullValueDescriptionHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (FullValueDescription)localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = meta(paramString);
      return (FullValueDescription)localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public FullValueDescription[] metas(String[] paramArrayOfString)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("metas", true);
      RepositoryIdSeqHelper.write(localOutputStream, paramArrayOfString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = ValueDescSeqHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (FullValueDescription[])localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = metas(paramArrayOfString);
      return (FullValueDescription[])localObject1;
    }
    finally
    {
      _releaseReply(localInputStream);
    }
  }
  
  public String[] bases(String paramString)
  {
    InputStream localInputStream = null;
    try
    {
      OutputStream localOutputStream = _request("bases", true);
      RepositoryIdHelper.write(localOutputStream, paramString);
      localInputStream = _invoke(localOutputStream);
      localObject1 = RepositoryIdSeqHelper.read(localInputStream);
      Object localObject2 = localObject1;
      return (String[])localObject2;
    }
    catch (ApplicationException localApplicationException)
    {
      localInputStream = localApplicationException.getInputStream();
      localObject1 = localApplicationException.getId();
      throw new MARSHAL((String)localObject1);
    }
    catch (RemarshalException localRemarshalException)
    {
      Object localObject1 = bases(paramString);
      return (String[])localObject1;
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
      org.omg.CORBA.Object localObject = ORB.init().string_to_object(str);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\SendingContext\_CodeBaseStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */