package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ValueMemberSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/ValueMemberSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public ValueMemberSeqHelper() {}
  
  public static void insert(Any paramAny, ValueMember[] paramArrayOfValueMember)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfValueMember);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ValueMember[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ValueMemberHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ValueMemberSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ValueMember[] read(InputStream paramInputStream)
  {
    ValueMember[] arrayOfValueMember = null;
    int i = paramInputStream.read_long();
    arrayOfValueMember = new ValueMember[i];
    for (int j = 0; j < arrayOfValueMember.length; j++) {
      arrayOfValueMember[j] = ValueMemberHelper.read(paramInputStream);
    }
    return arrayOfValueMember;
  }
  
  public static void write(OutputStream paramOutputStream, ValueMember[] paramArrayOfValueMember)
  {
    paramOutputStream.write_long(paramArrayOfValueMember.length);
    for (int i = 0; i < paramArrayOfValueMember.length; i++) {
      ValueMemberHelper.write(paramOutputStream, paramArrayOfValueMember[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\ValueMemberSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */