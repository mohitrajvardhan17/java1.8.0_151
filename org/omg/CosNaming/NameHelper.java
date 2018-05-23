package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameHelper
{
  private static String _id = "IDL:omg.org/CosNaming/Name:1.0";
  private static TypeCode __typeCode = null;
  
  public NameHelper() {}
  
  public static void insert(Any paramAny, NameComponent[] paramArrayOfNameComponent)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfNameComponent);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NameComponent[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = NameComponentHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "Name", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NameComponent[] read(InputStream paramInputStream)
  {
    NameComponent[] arrayOfNameComponent = null;
    int i = paramInputStream.read_long();
    arrayOfNameComponent = new NameComponent[i];
    for (int j = 0; j < arrayOfNameComponent.length; j++) {
      arrayOfNameComponent[j] = NameComponentHelper.read(paramInputStream);
    }
    return arrayOfNameComponent;
  }
  
  public static void write(OutputStream paramOutputStream, NameComponent[] paramArrayOfNameComponent)
  {
    paramOutputStream.write_long(paramArrayOfNameComponent.length);
    for (int i = 0; i < paramArrayOfNameComponent.length; i++) {
      NameComponentHelper.write(paramOutputStream, paramArrayOfNameComponent[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NameHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */