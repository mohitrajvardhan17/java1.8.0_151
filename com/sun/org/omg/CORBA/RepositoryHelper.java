package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class RepositoryHelper
{
  private static String _id = "IDL:com.sun.omg.org/CORBA/Repository:3.0";
  private static TypeCode __typeCode = null;
  
  public RepositoryHelper() {}
  
  public static void insert(Any paramAny, Repository paramRepository)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramRepository);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Repository extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().create_string_tc(0);
      __typeCode = ORB.init().create_alias_tc(id(), "Repository", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Repository read(InputStream paramInputStream)
  {
    String str = null;
    str = paramInputStream.read_string();
    return null;
  }
  
  public static void write(OutputStream paramOutputStream, Repository paramRepository)
  {
    paramOutputStream.write_string(null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\RepositoryHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */