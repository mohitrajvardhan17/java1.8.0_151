package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHolder
  implements Streamable
{
  public Server value = null;
  
  public ServerHolder() {}
  
  public ServerHolder(Server paramServer)
  {
    value = paramServer;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */