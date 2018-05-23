package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BadServerDefinitionHelper
{
  private static String _id = "IDL:activation/BadServerDefinition:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public BadServerDefinitionHelper() {}
  
  public static void insert(Any paramAny, BadServerDefinition paramBadServerDefinition)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramBadServerDefinition);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static BadServerDefinition extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("reason", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "BadServerDefinition", arrayOfStructMember);
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
  
  public static BadServerDefinition read(InputStream paramInputStream)
  {
    BadServerDefinition localBadServerDefinition = new BadServerDefinition();
    paramInputStream.read_string();
    reason = paramInputStream.read_string();
    return localBadServerDefinition;
  }
  
  public static void write(OutputStream paramOutputStream, BadServerDefinition paramBadServerDefinition)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_string(reason);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\BadServerDefinitionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */