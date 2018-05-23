package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerManagerHolder
  implements Streamable
{
  public ServerManager value = null;
  
  public ServerManagerHolder() {}
  
  public ServerManagerHolder(ServerManager paramServerManager)
  {
    value = paramServerManager;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerManagerHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerManagerHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerManagerHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerManagerHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */