package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class StructMemberSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/StructMemberSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public StructMemberSeqHelper() {}
  
  public static void insert(Any paramAny, StructMember[] paramArrayOfStructMember)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfStructMember);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static StructMember[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = StructMemberHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "StructMemberSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static StructMember[] read(InputStream paramInputStream)
  {
    StructMember[] arrayOfStructMember = null;
    int i = paramInputStream.read_long();
    arrayOfStructMember = new StructMember[i];
    for (int j = 0; j < arrayOfStructMember.length; j++) {
      arrayOfStructMember[j] = StructMemberHelper.read(paramInputStream);
    }
    return arrayOfStructMember;
  }
  
  public static void write(OutputStream paramOutputStream, StructMember[] paramArrayOfStructMember)
  {
    paramOutputStream.write_long(paramArrayOfStructMember.length);
    for (int i = 0; i < paramArrayOfStructMember.length; i++) {
      StructMemberHelper.write(paramOutputStream, paramArrayOfStructMember[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\StructMemberSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */