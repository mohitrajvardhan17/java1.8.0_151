package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class InitializerSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/InitializerSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public InitializerSeqHelper() {}
  
  public static void insert(Any paramAny, Initializer[] paramArrayOfInitializer)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfInitializer);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Initializer[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = InitializerHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "InitializerSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Initializer[] read(InputStream paramInputStream)
  {
    Initializer[] arrayOfInitializer = null;
    int i = paramInputStream.read_long();
    arrayOfInitializer = new Initializer[i];
    for (int j = 0; j < arrayOfInitializer.length; j++) {
      arrayOfInitializer[j] = InitializerHelper.read(paramInputStream);
    }
    return arrayOfInitializer;
  }
  
  public static void write(OutputStream paramOutputStream, Initializer[] paramArrayOfInitializer)
  {
    paramOutputStream.write_long(paramArrayOfInitializer.length);
    for (int i = 0; i < paramArrayOfInitializer.length; i++) {
      InitializerHelper.write(paramOutputStream, paramArrayOfInitializer[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\InitializerSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */