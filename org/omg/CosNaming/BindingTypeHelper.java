package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BindingTypeHelper
{
  private static String _id = "IDL:omg.org/CosNaming/BindingType:1.0";
  private static TypeCode __typeCode = null;
  
  public BindingTypeHelper() {}
  
  public static void insert(Any paramAny, BindingType paramBindingType)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramBindingType);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static BindingType extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_enum_tc(id(), "BindingType", new String[] { "nobject", "ncontext" });
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static BindingType read(InputStream paramInputStream)
  {
    return BindingType.from_int(paramInputStream.read_long());
  }
  
  public static void write(OutputStream paramOutputStream, BindingType paramBindingType)
  {
    paramOutputStream.write_long(paramBindingType.value());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingTypeHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */