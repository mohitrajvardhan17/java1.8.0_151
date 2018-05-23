package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class CharSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/CharSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public CharSeqHelper() {}
  
  public static void insert(Any paramAny, char[] paramArrayOfChar)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfChar);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static char[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().get_primitive_tc(TCKind.tk_char);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "CharSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static char[] read(InputStream paramInputStream)
  {
    char[] arrayOfChar = null;
    int i = paramInputStream.read_long();
    arrayOfChar = new char[i];
    paramInputStream.read_char_array(arrayOfChar, 0, i);
    return arrayOfChar;
  }
  
  public static void write(OutputStream paramOutputStream, char[] paramArrayOfChar)
  {
    paramOutputStream.write_long(paramArrayOfChar.length);
    paramOutputStream.write_char_array(paramArrayOfChar, 0, paramArrayOfChar.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\CharSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */