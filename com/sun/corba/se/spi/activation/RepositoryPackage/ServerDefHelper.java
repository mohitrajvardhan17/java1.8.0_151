package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerDefHelper
{
  private static String _id = "IDL:activation/Repository/ServerDef:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ServerDefHelper() {}
  
  public static void insert(Any paramAny, ServerDef paramServerDef)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServerDef);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServerDef extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[5];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("applicationName", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[1] = new StructMember("serverName", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[2] = new StructMember("serverClassPath", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[3] = new StructMember("serverArgs", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[4] = new StructMember("serverVmArgs", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServerDef", arrayOfStructMember);
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
  
  public static ServerDef read(InputStream paramInputStream)
  {
    ServerDef localServerDef = new ServerDef();
    applicationName = paramInputStream.read_string();
    serverName = paramInputStream.read_string();
    serverClassPath = paramInputStream.read_string();
    serverArgs = paramInputStream.read_string();
    serverVmArgs = paramInputStream.read_string();
    return localServerDef;
  }
  
  public static void write(OutputStream paramOutputStream, ServerDef paramServerDef)
  {
    paramOutputStream.write_string(applicationName);
    paramOutputStream.write_string(serverName);
    paramOutputStream.write_string(serverClassPath);
    paramOutputStream.write_string(serverArgs);
    paramOutputStream.write_string(serverVmArgs);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDefHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */