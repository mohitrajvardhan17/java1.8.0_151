package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ULongSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/ULongSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ULongSeqHelper() {}
  
  public static void insert(Any paramAny, int[] paramArrayOfInt)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfInt);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static int[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ULongSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static int[] read(InputStream paramInputStream)
  {
    int[] arrayOfInt = null;
    int i = paramInputStream.read_long();
    arrayOfInt = new int[i];
    paramInputStream.read_ulong_array(arrayOfInt, 0, i);
    return arrayOfInt;
  }
  
  public static void write(OutputStream paramOutputStream, int[] paramArrayOfInt)
  {
    paramOutputStream.write_long(paramArrayOfInt.length);
    paramOutputStream.write_ulong_array(paramArrayOfInt, 0, paramArrayOfInt.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\ULongSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */