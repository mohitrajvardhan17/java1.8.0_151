package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DynAnySeqHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/DynAnySeq:1.0";
  private static TypeCode __typeCode = null;
  
  public DynAnySeqHelper() {}
  
  public static void insert(Any paramAny, DynAny[] paramArrayOfDynAny)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfDynAny);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DynAny[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = DynAnyHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "DynAnySeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DynAny[] read(InputStream paramInputStream)
  {
    DynAny[] arrayOfDynAny = null;
    int i = paramInputStream.read_long();
    arrayOfDynAny = new DynAny[i];
    for (int j = 0; j < arrayOfDynAny.length; j++) {
      arrayOfDynAny[j] = DynAnyHelper.read(paramInputStream);
    }
    return arrayOfDynAny;
  }
  
  public static void write(OutputStream paramOutputStream, DynAny[] paramArrayOfDynAny)
  {
    paramOutputStream.write_long(paramArrayOfDynAny.length);
    for (int i = 0; i < paramArrayOfDynAny.length; i++) {
      DynAnyHelper.write(paramOutputStream, paramArrayOfDynAny[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\DynAnySeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */