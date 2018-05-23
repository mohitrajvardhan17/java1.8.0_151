package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBAlreadyRegisteredHelper
{
  private static String _id = "IDL:activation/ORBAlreadyRegistered:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ORBAlreadyRegisteredHelper() {}
  
  public static void insert(Any paramAny, ORBAlreadyRegistered paramORBAlreadyRegistered)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramORBAlreadyRegistered);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ORBAlreadyRegistered extract(Any paramAny)
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
          localTypeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", localTypeCode);
          arrayOfStructMember[0] = new StructMember("orbId", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ORBAlreadyRegistered", arrayOfStructMember);
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
  
  public static ORBAlreadyRegistered read(InputStream paramInputStream)
  {
    ORBAlreadyRegistered localORBAlreadyRegistered = new ORBAlreadyRegistered();
    paramInputStream.read_string();
    orbId = paramInputStream.read_string();
    return localORBAlreadyRegistered;
  }
  
  public static void write(OutputStream paramOutputStream, ORBAlreadyRegistered paramORBAlreadyRegistered)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_string(orbId);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegisteredHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */