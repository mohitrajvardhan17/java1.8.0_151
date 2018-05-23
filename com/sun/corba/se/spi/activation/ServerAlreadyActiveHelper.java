package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerAlreadyActiveHelper
{
  private static String _id = "IDL:activation/ServerAlreadyActive:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ServerAlreadyActiveHelper() {}
  
  public static void insert(Any paramAny, ServerAlreadyActive paramServerAlreadyActive)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServerAlreadyActive);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServerAlreadyActive extract(Any paramAny)
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
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
          localTypeCode = ORB.init().create_alias_tc(ServerIdHelper.id(), "ServerId", localTypeCode);
          arrayOfStructMember[0] = new StructMember("serverId", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ServerAlreadyActive", arrayOfStructMember);
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
  
  public static ServerAlreadyActive read(InputStream paramInputStream)
  {
    ServerAlreadyActive localServerAlreadyActive = new ServerAlreadyActive();
    paramInputStream.read_string();
    serverId = paramInputStream.read_long();
    return localServerAlreadyActive;
  }
  
  public static void write(OutputStream paramOutputStream, ServerAlreadyActive paramServerAlreadyActive)
  {
    paramOutputStream.write_string(id());
    paramOutputStream.write_long(serverId);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyActiveHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */