package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class NamingContextExtHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NamingContextExt:1.0";
  private static TypeCode __typeCode = null;
  
  public NamingContextExtHelper() {}
  
  public static void insert(Any paramAny, NamingContextExt paramNamingContextExt)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNamingContextExt);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NamingContextExt extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "NamingContextExt");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NamingContextExt read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_NamingContextExtStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, NamingContextExt paramNamingContextExt)
  {
    paramOutputStream.write_Object(paramNamingContextExt);
  }
  
  public static NamingContextExt narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof NamingContextExt)) {
      return (NamingContextExt)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _NamingContextExtStub local_NamingContextExtStub = new _NamingContextExtStub();
    local_NamingContextExtStub._set_delegate(localDelegate);
    return local_NamingContextExtStub;
  }
  
  public static NamingContextExt unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof NamingContextExt)) {
      return (NamingContextExt)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _NamingContextExtStub local_NamingContextExtStub = new _NamingContextExtStub();
    local_NamingContextExtStub._set_delegate(localDelegate);
    return local_NamingContextExtStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */