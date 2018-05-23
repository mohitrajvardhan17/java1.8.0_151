package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttributeModeHelper
{
  private static String _id = "IDL:omg.org/CORBA/AttributeMode:1.0";
  private static TypeCode __typeCode = null;
  
  public AttributeModeHelper() {}
  
  public static void insert(Any paramAny, AttributeMode paramAttributeMode)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramAttributeMode);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static AttributeMode extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_enum_tc(id(), "AttributeMode", new String[] { "ATTR_NORMAL", "ATTR_READONLY" });
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static AttributeMode read(InputStream paramInputStream)
  {
    return AttributeMode.from_int(paramInputStream.read_long());
  }
  
  public static void write(OutputStream paramOutputStream, AttributeMode paramAttributeMode)
  {
    paramOutputStream.write_long(paramAttributeMode.value());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\AttributeModeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */