package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.BoxedValueHelper;

public class StringValueHelper
  implements BoxedValueHelper
{
  private static String _id = "IDL:omg.org/CORBA/StringValue:1.0";
  private static StringValueHelper _instance = new StringValueHelper();
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public StringValueHelper() {}
  
  public static void insert(Any paramAny, String paramString)
  {
    org.omg.CORBA.portable.OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramString);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static String extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      synchronized (TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active) {
            return ORB.init().create_recursive_tc(_id);
          }
          __active = true;
          __typeCode = ORB.init().create_string_tc(0);
          __typeCode = ORB.init().create_value_box_tc(_id, "StringValue", __typeCode);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static String read(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    if (!(paramInputStream instanceof org.omg.CORBA_2_3.portable.InputStream)) {
      throw new BAD_PARAM();
    }
    return (String)((org.omg.CORBA_2_3.portable.InputStream)paramInputStream).read_value(_instance);
  }
  
  public Serializable read_value(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    String str = paramInputStream.read_string();
    return str;
  }
  
  public static void write(org.omg.CORBA.portable.OutputStream paramOutputStream, String paramString)
  {
    if (!(paramOutputStream instanceof org.omg.CORBA_2_3.portable.OutputStream)) {
      throw new BAD_PARAM();
    }
    ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_value(paramString, _instance);
  }
  
  public void write_value(org.omg.CORBA.portable.OutputStream paramOutputStream, Serializable paramSerializable)
  {
    if (!(paramSerializable instanceof String)) {
      throw new MARSHAL();
    }
    String str = (String)paramSerializable;
    paramOutputStream.write_string(str);
  }
  
  public String get_id()
  {
    return _id;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\StringValueHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */