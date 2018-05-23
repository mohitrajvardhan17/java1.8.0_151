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

public abstract class DynEnumHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynEnum:1.0";
  private static TypeCode __typeCode = null;
  
  public DynEnumHelper() {}
  
  public static void insert(Any paramAny, DynEnum paramDynEnum)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDynEnum);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynEnum extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "DynEnum");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynEnum read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, DynEnum paramDynEnum)
  {
    throw new MARSHAL();
  }
  
  public static DynEnum narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynEnum)) {
      return (DynEnum)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynEnumStub local_DynEnumStub = new _DynEnumStub();
    local_DynEnumStub._set_delegate(localDelegate);
    return local_DynEnumStub;
  }
  
  public static DynEnum unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynEnum)) {
      return (DynEnum)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynEnumStub local_DynEnumStub = new _DynEnumStub();
    local_DynEnumStub._set_delegate(localDelegate);
    return local_DynEnumStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynEnumHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */