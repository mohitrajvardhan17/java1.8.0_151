package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TypeCodeImplHelper
{
  private static String _id = "IDL:omg.org/CORBA/TypeCode:1.0";
  private static TypeCode __typeCode = null;
  
  public TypeCodeImplHelper() {}
  
  public static void insert(Any paramAny, TypeCode paramTypeCode)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramTypeCode);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static TypeCode extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static TypeCode read(InputStream paramInputStream)
  {
    return paramInputStream.read_TypeCode();
  }
  
  public static void write(OutputStream paramOutputStream, TypeCode paramTypeCode)
  {
    paramOutputStream.write_TypeCode(paramTypeCode);
  }
  
  public static void write(OutputStream paramOutputStream, TypeCodeImpl paramTypeCodeImpl)
  {
    paramOutputStream.write_TypeCode(paramTypeCodeImpl);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\corba\TypeCodeImplHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */