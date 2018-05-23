package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyUninstalledHolder
  implements Streamable
{
  public ServerAlreadyUninstalled value = null;
  
  public ServerAlreadyUninstalledHolder() {}
  
  public ServerAlreadyUninstalledHolder(ServerAlreadyUninstalled paramServerAlreadyUninstalled)
  {
    value = paramServerAlreadyUninstalled;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerAlreadyUninstalledHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerAlreadyUninstalledHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerAlreadyUninstalledHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyUninstalledHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */