package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BindingListHelper
{
  private static String _id = "IDL:omg.org/CosNaming/BindingList:1.0";
  private static TypeCode __typeCode = null;
  
  public BindingListHelper() {}
  
  public static void insert(Any paramAny, Binding[] paramArrayOfBinding)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfBinding);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Binding[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = BindingHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "BindingList", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Binding[] read(InputStream paramInputStream)
  {
    Binding[] arrayOfBinding = null;
    int i = paramInputStream.read_long();
    arrayOfBinding = new Binding[i];
    for (int j = 0; j < arrayOfBinding.length; j++) {
      arrayOfBinding[j] = BindingHelper.read(paramInputStream);
    }
    return arrayOfBinding;
  }
  
  public static void write(OutputStream paramOutputStream, Binding[] paramArrayOfBinding)
  {
    paramOutputStream.write_long(paramArrayOfBinding.length);
    for (int i = 0; i < paramArrayOfBinding.length; i++) {
      BindingHelper.write(paramOutputStream, paramArrayOfBinding[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */