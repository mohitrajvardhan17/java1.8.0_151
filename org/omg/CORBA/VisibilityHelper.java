package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class VisibilityHelper
{
  private static String _id = "IDL:omg.org/CORBA/Visibility:1.0";
  private static TypeCode __typeCode = null;
  
  public VisibilityHelper() {}
  
  public static void insert(Any paramAny, short paramShort)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramShort);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static short extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
      __typeCode = ORB.init().create_alias_tc(id(), "Visibility", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static short read(InputStream paramInputStream)
  {
    short s = 0;
    s = paramInputStream.read_short();
    return s;
  }
  
  public static void write(OutputStream paramOutputStream, short paramShort)
  {
    paramOutputStream.write_short(paramShort);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\VisibilityHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */