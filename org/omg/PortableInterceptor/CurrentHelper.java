package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CurrentHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/Current:1.0";
  private static TypeCode __typeCode = null;
  
  public CurrentHelper() {}
  
  public static void insert(Any paramAny, Current paramCurrent)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramCurrent);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Current extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Current");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Current read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, Current paramCurrent)
  {
    throw new MARSHAL();
  }
  
  public static Current narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Current)) {
      return (Current)paramObject;
    }
    throw new BAD_PARAM();
  }
  
  public static Current unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Current)) {
      return (Current)paramObject;
    }
    throw new BAD_PARAM();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\CurrentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */