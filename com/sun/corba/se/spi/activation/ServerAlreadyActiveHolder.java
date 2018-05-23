package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyActiveHolder
  implements Streamable
{
  public ServerAlreadyActive value = null;
  
  public ServerAlreadyActiveHolder() {}
  
  public ServerAlreadyActiveHolder(ServerAlreadyActive paramServerAlreadyActive)
  {
    value = paramServerAlreadyActive;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerAlreadyActiveHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerAlreadyActiveHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerAlreadyActiveHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyActiveHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */