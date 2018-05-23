package org.omg.CORBA;

import java.io.Serializable;

public abstract class ValueBaseHelper
{
  private static String _id = "IDL:omg.org/CORBA/ValueBase:1.0";
  private static TypeCode __typeCode = null;
  
  public ValueBaseHelper() {}
  
  public static void insert(Any paramAny, Serializable paramSerializable)
  {
    org.omg.CORBA.portable.OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramSerializable);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Serializable extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_value);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Serializable read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    return ((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_value();
  }
  
  public static void write(org.omg.CORBA.portable.OutputStream paramOutputStream, Serializable paramSerializable)
  {
    ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_value(paramSerializable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ValueBaseHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */