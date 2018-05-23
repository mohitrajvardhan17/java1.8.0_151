package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class EndPointInfoHelper
{
  private static String _id = "IDL:activation/EndPointInfo:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public EndPointInfoHelper() {}
  
  public static void insert(Any paramAny, EndPointInfo paramEndPointInfo)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramEndPointInfo);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static EndPointInfo extract(Any paramAny)
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
          arrayOfStructMember[0] = new StructMember("endpointType", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
          localTypeCode = ORB.init().create_alias_tc(TCPPortHelper.id(), "TCPPort", localTypeCode);
          arrayOfStructMember[1] = new StructMember("port", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "EndPointInfo", arrayOfStructMember);
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
  
  public static EndPointInfo read(InputStream paramInputStream)
  {
    EndPointInfo localEndPointInfo = new EndPointInfo();
    endpointType = paramInputStream.read_string();
    port = paramInputStream.read_long();
    return localEndPointInfo;
  }
  
  public static void write(OutputStream paramOutputStream, EndPointInfo paramEndPointInfo)
  {
    paramOutputStream.write_string(endpointType);
    paramOutputStream.write_long(port);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\EndPointInfoHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */