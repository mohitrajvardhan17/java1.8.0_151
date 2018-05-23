package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BooleanSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/BooleanSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public BooleanSeqHelper() {}
  
  public static void insert(Any paramAny, boolean[] paramArrayOfBoolean)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfBoolean);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static boolean[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "BooleanSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static boolean[] read(InputStream paramInputStream)
  {
    boolean[] arrayOfBoolean = null;
    int i = paramInputStream.read_long();
    arrayOfBoolean = new boolean[i];
    paramInputStream.read_boolean_array(arrayOfBoolean, 0, i);
    return arrayOfBoolean;
  }
  
  public static void write(OutputStream paramOutputStream, boolean[] paramArrayOfBoolean)
  {
    paramOutputStream.write_long(paramArrayOfBoolean.length);
    paramOutputStream.write_boolean_array(paramArrayOfBoolean, 0, paramArrayOfBoolean.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\BooleanSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */