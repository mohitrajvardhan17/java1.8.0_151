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

public abstract class DynFixedHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynFixed:1.0";
  private static TypeCode __typeCode = null;
  
  public DynFixedHelper() {}
  
  public static void insert(Any paramAny, DynFixed paramDynFixed)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDynFixed);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynFixed extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "DynFixed");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynFixed read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, DynFixed paramDynFixed)
  {
    throw new MARSHAL();
  }
  
  public static DynFixed narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynFixed)) {
      return (DynFixed)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynFixedStub local_DynFixedStub = new _DynFixedStub();
    local_DynFixedStub._set_delegate(localDelegate);
    return local_DynFixedStub;
  }
  
  public static DynFixed unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof DynFixed)) {
      return (DynFixed)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _DynFixedStub local_DynFixedStub = new _DynFixedStub();
    local_DynFixedStub._set_delegate(localDelegate);
    return local_DynFixedStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynFixedHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */