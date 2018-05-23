package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ServerManagerHelper
{
  private static String _id = "IDL:activation/ServerManager:1.0";
  private static TypeCode __typeCode = null;
  
  public ServerManagerHelper() {}
  
  public static void insert(Any paramAny, ServerManager paramServerManager)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramServerManager);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ServerManager extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "ServerManager");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ServerManager read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_ServerManagerStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, ServerManager paramServerManager)
  {
    paramOutputStream.write_Object(paramServerManager);
  }
  
  public static ServerManager narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof ServerManager)) {
      return (ServerManager)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerManagerStub local_ServerManagerStub = new _ServerManagerStub();
    local_ServerManagerStub._set_delegate(localDelegate);
    return local_ServerManagerStub;
  }
  
  public static ServerManager unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof ServerManager)) {
      return (ServerManager)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ServerManagerStub local_ServerManagerStub = new _ServerManagerStub();
    local_ServerManagerStub._set_delegate(localDelegate);
    return local_ServerManagerStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerManagerHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */