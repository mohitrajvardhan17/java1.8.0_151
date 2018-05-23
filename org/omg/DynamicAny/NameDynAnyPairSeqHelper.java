package org.omg.DynamicAny;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameDynAnyPairSeqHelper
{
  private static String _id = "IDL:omg.org/DynamicAny/NameDynAnyPairSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public NameDynAnyPairSeqHelper() {}
  
  public static void insert(Any paramAny, NameDynAnyPair[] paramArrayOfNameDynAnyPair)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfNameDynAnyPair);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NameDynAnyPair[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = NameDynAnyPairHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "NameDynAnyPairSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static NameDynAnyPair[] read(InputStream paramInputStream)
  {
    NameDynAnyPair[] arrayOfNameDynAnyPair = null;
    int i = paramInputStream.read_long();
    arrayOfNameDynAnyPair = new NameDynAnyPair[i];
    for (int j = 0; j < arrayOfNameDynAnyPair.length; j++) {
      arrayOfNameDynAnyPair[j] = NameDynAnyPairHelper.read(paramInputStream);
    }
    return arrayOfNameDynAnyPair;
  }
  
  public static void write(OutputStream paramOutputStream, NameDynAnyPair[] paramArrayOfNameDynAnyPair)
  {
    paramOutputStream.write_long(paramArrayOfNameDynAnyPair.length);
    for (int i = 0; i < paramArrayOfNameDynAnyPair.length; i++) {
      NameDynAnyPairHelper.write(paramOutputStream, paramArrayOfNameDynAnyPair[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\DynamicAny\NameDynAnyPairSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */