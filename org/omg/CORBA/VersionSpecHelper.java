package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class VersionSpecHelper
{
  private static String _id = "IDL:omg.org/CORBA/VersionSpec:1.0";
  private static TypeCode __typeCode = null;
  
  public VersionSpecHelper() {}
  
  public static void insert(Any paramAny, String paramString)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
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
    if (__typeCode == null)
    {
      __typeCode = ORB.init().create_string_tc(0);
      __typeCode = ORB.init().create_alias_tc(id(), "VersionSpec", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static String read(InputStream paramInputStream)
  {
    String str = null;
    str = paramInputStream.read_string();
    return str;
  }
  
  public static void write(OutputStream paramOutputStream, String paramString)
  {
    paramOutputStream.write_string(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\VersionSpecHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */