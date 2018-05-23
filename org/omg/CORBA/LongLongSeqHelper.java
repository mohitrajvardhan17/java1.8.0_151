package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class LongLongSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/LongLongSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public LongLongSeqHelper() {}
  
  public static void insert(Any paramAny, long[] paramArrayOfLong)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfLong);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static long[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_longlong);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "LongLongSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static long[] read(InputStream paramInputStream)
  {
    long[] arrayOfLong = null;
    int i = paramInputStream.read_long();
    arrayOfLong = new long[i];
    paramInputStream.read_longlong_array(arrayOfLong, 0, i);
    return arrayOfLong;
  }
  
  public static void write(OutputStream paramOutputStream, long[] paramArrayOfLong)
  {
    paramOutputStream.write_long(paramArrayOfLong.length);
    paramOutputStream.write_longlong_array(paramArrayOfLong, 0, paramArrayOfLong.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\LongLongSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */