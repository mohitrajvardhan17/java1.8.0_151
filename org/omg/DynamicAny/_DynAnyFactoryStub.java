package org.omg.DynamicAny;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.ServantObject;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;

public class _DynAnyFactoryStub
  extends ObjectImpl
  implements DynAnyFactory
{
  public static final Class _opsClass = DynAnyFactoryOperations.class;
  private static String[] __ids = { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
  
  public _DynAnyFactoryStub() {}
  
  public DynAny create_dyn_any(Any paramAny)
    throws InconsistentTypeCode
  {
    ServantObject localServantObject = _servant_preinvoke("create_dyn_any", _opsClass);
    DynAnyFactoryOperations localDynAnyFactoryOperations = (DynAnyFactoryOperations)servant;
    try
    {
      DynAny localDynAny = localDynAnyFactoryOperations.create_dyn_any(paramAny);
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
    }
  }
  
  public DynAny create_dyn_any_from_type_code(TypeCode paramTypeCode)
    throws InconsistentTypeCode
  {
    ServantObject localServantObject = _servant_preinvoke("create_dyn_any_from_type_code", _opsClass);
    DynAnyFactoryOperations localDynAnyFactoryOperations = (DynAnyFactoryOperations)servant;
    try
    {
      DynAny localDynAny = localDynAnyFactoryOperations.create_dyn_any_from_type_code(paramTypeCode);
      return localDynAny;
    }
    finally
    {
      _servant_postinvoke(localServantObject);
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\_DynAnyFactoryStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */