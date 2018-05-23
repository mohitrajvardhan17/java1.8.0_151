package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyRegisteredHolder
  implements Streamable
{
  public ServerAlreadyRegistered value = null;
  
  public ServerAlreadyRegisteredHolder() {}
  
  public ServerAlreadyRegisteredHolder(ServerAlreadyRegistered paramServerAlreadyRegistered)
  {
    value = paramServerAlreadyRegistered;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerAlreadyRegisteredHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerAlreadyRegisteredHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerAlreadyRegisteredHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyRegisteredHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */