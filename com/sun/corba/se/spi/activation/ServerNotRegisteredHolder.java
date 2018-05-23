package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotRegisteredHolder
  implements Streamable
{
  public ServerNotRegistered value = null;
  
  public ServerNotRegisteredHolder() {}
  
  public ServerNotRegisteredHolder(ServerNotRegistered paramServerNotRegistered)
  {
    value = paramServerNotRegistered;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerNotRegisteredHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerNotRegisteredHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerNotRegisteredHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerNotRegisteredHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */