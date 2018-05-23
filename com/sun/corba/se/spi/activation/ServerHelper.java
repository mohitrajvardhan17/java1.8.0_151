package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerHelper
{
  private static String _id = "IDL:activation/Server:1.0";
  private static TypeCode __typeCode = null;
  
  public ServerHelper() {}
  
  public static void insert(Any paramAny, Server paramServer)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServer);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Server extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Server");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Server read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_ServerStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, Server paramServer)
  {
    paramOutputStream.write_Object(paramServer);
  }
  
  public static Server narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Server)) {
      return (Server)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerStub local_ServerStub = new _ServerStub();
    local_ServerStub._set_delegate(localDelegate);
    return local_ServerStub;
  }
  
  public static Server unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Server)) {
      return (Server)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerStub local_ServerStub = new _ServerStub();
    local_ServerStub._set_delegate(localDelegate);
    return local_ServerStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */