package com.sun.corba.se.spi.activation.LocatorPackage;

import com.sun.corba.se.spi.activation.EndPointInfoHelper;
import com.sun.corba.se.spi.activation.EndpointInfoListHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerLocationPerORBHelper
{
  private static String _id = "IDL:activation/Locator/ServerLocationPerORB:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ServerLocationPerORBHelper() {}
  
  public static void insert(Any paramAny, ServerLocationPerORB paramServerLocationPerORB)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServerLocationPerORB);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServerLocationPerORB extract(Any paramAny)
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
          localTypeCode = ORB.init().create_string_tc(0);
          arrayOfStructMember[0] = new StructMember("hostname", localTypeCode, null);
          localTypeCode = EndPointInfoHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(EndpointInfoListHelper.id(), "EndpointInfoList", localTypeCode);
          arrayOfStructMember[1] = new StructMember("ports", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ServerLocationPerORB", arrayOfStructMember);
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
  
  public static ServerLocationPerORB read(InputStream paramInputStream)
  {
    ServerLocationPerORB localServerLocationPerORB = new ServerLocationPerORB();
    hostname = paramInputStream.read_string();
    ports = EndpointInfoListHelper.read(paramInputStream);
    return localServerLocationPerORB;
  }
  
  public static void write(OutputStream paramOutputStream, ServerLocationPerORB paramServerLocationPerORB)
  {
    paramOutputStream.write_string(hostname);
    EndpointInfoListHelper.write(paramOutputStream, ports);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationPerORBHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */