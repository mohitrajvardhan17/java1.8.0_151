package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class IORInterceptor_3_0Helper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/IORInterceptor_3_0:1.0";
  private static TypeCode __typeCode = null;
  
  public IORInterceptor_3_0Helper() {}
  
  public static void insert(Any paramAny, IORInterceptor_3_0 paramIORInterceptor_3_0)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramIORInterceptor_3_0);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static IORInterceptor_3_0 extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "IORInterceptor_3_0");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static IORInterceptor_3_0 read(InputStream paramInputStream)
  {
    throw new MARSHAL();
  }
  
  public static void write(OutputStream paramOutputStream, IORInterceptor_3_0 paramIORInterceptor_3_0)
  {
    throw new MARSHAL();
  }
  
  public static IORInterceptor_3_0 narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof IORInterceptor_3_0)) {
      return (IORInterceptor_3_0)paramObject;
    }
    throw new BAD_PARAM();
  }
  
  public static IORInterceptor_3_0 unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof IORInterceptor_3_0)) {
      return (IORInterceptor_3_0)paramObject;
    }
    throw new BAD_PARAM();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\IORInterceptor_3_0Helper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */