package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DoubleSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/DoubleSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public DoubleSeqHelper() {}
  
  public static void insert(Any paramAny, double[] paramArrayOfDouble)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfDouble);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static double[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_double);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "DoubleSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static double[] read(InputStream paramInputStream)
  {
    double[] arrayOfDouble = null;
    int i = paramInputStream.read_long();
    arrayOfDouble = new double[i];
    paramInputStream.read_double_array(arrayOfDouble, 0, i);
    return arrayOfDouble;
  }
  
  public static void write(OutputStream paramOutputStream, double[] paramArrayOfDouble)
  {
    paramOutputStream.write_long(paramArrayOfDouble.length);
    paramOutputStream.write_double_array(paramArrayOfDouble, 0, paramArrayOfDouble.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\DoubleSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */