package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynValueHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynValue:1.0";
  private static TypeCode __typeCode = null;
  
  public DynValueHelper() {}
  
  public static void insert(Any paramAny, DynValue paramDynValue)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDynValue);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynValue extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "DynValue");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynValue read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, DynValue paramDynValue)
  {
    throw new MARSHAL();
  }
  
  public static DynValue narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynValue)) {
      return (DynValue)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynValueStub local_DynValueStub = new _DynValueStub();
    local_DynValueStub._set_delegate(localDelegate);
    return local_DynValueStub;
  }
  
  public static DynValue unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynValue)) {
      return (DynValue)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynValueStub local_DynValueStub = new _DynValueStub();
    local_DynValueStub._set_delegate(localDelegate);
    return local_DynValueStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */