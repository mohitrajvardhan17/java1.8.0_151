package org.omg.PortableServer;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServantActivatorHelper
{
  private static String _id = "IDL:omg.org/PortableServer/ServantActivator:2.3";
  private static TypeCode __typeCode = null;
  
  public ServantActivatorHelper() {}
  
  public static void insert(Any paramAny, ServantActivator paramServantActivator)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServantActivator);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServantActivator extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "ServantActivator");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ServantActivator read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, ServantActivator paramServantActivator)
  {
    throw new MARSHAL();
  }
  
  public static ServantActivator narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof ServantActivator)) {
      return (ServantActivator)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServantActivatorStub local_ServantActivatorStub = new _ServantActivatorStub();
    local_ServantActivatorStub._set_delegate(localDelegate);
    return local_ServantActivatorStub;
  }
  
  public static ServantActivator unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof ServantActivator)) {
      return (ServantActivator)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServantActivatorStub local_ServantActivatorStub = new _ServantActivatorStub();
    local_ServantActivatorStub._set_delegate(localDelegate);
    return local_ServantActivatorStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\ServantActivatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */