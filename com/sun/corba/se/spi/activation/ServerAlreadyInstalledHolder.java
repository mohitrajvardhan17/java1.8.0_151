package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyInstalledHolder
  implements Streamable
{
  public ServerAlreadyInstalled value = null;
  
  public ServerAlreadyInstalledHolder() {}
  
  public ServerAlreadyInstalledHolder(ServerAlreadyInstalled paramServerAlreadyInstalled)
  {
    value = paramServerAlreadyInstalled;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerAlreadyInstalledHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerAlreadyInstalledHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerAlreadyInstalledHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyInstalledHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */