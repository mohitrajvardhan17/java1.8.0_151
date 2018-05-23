package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SetOverrideTypeHelper
{
  private static String _id = "IDL:omg.org/CORBA/SetOverrideType:1.0";
  private static TypeCode __typeCode = null;
  
  public SetOverrideTypeHelper() {}
  
  public static void insert(Any paramAny, SetOverrideType paramSetOverrideType)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramSetOverrideType);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static SetOverrideType extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_enum_tc(id(), "SetOverrideType", new String[] { "SET_OVERRIDE", "ADD_OVERRIDE" });
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static SetOverrideType read(InputStream paramInputStream)
  {
    return SetOverrideType.from_int(paramInputStream.read_long());
  }
  
  public static void write(OutputStream paramOutputStream, SetOverrideType paramSetOverrideType)
  {
    paramOutputStream.write_long(paramSetOverrideType.value());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\SetOverrideTypeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */