package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class InitializerHelper
{
  private static String _id = "IDL:omg.org/CORBA/Initializer:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public InitializerHelper() {}
  
  public static void insert(Any paramAny, Initializer paramInitializer)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInitializer);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Initializer extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      synchronized (TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active) {
            return ORB.init().create_recursive_tc(_id);
          }
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode localTypeCode = null;
          localTypeCode = StructMemberHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(StructMemberSeqHelper.id(), "StructMemberSeq", localTypeCode);
          arrayOfStructMember[0] = new StructMember("members", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          arrayOfStructMember[1] = new StructMember("name", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "Initializer", arrayOfStructMember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Initializer read(InputStream paramInputStream)
  {
    Initializer localInitializer = new Initializer();
    members = StructMemberSeqHelper.read(paramInputStream);
    name = paramInputStream.read_string();
    return localInitializer;
  }
  
  public static void write(OutputStream paramOutputStream, Initializer paramInitializer)
  {
    StructMemberSeqHelper.write(paramOutputStream, members);
    paramOutputStream.write_string(name);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\InitializerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */