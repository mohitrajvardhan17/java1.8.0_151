package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotActiveHolder
  implements Streamable
{
  public ServerNotActive value = null;
  
  public ServerNotActiveHolder() {}
  
  public ServerNotActiveHolder(ServerNotActive paramServerNotActive)
  {
    value = paramServerNotActive;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerNotActiveHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerNotActiveHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerNotActiveHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerNotActiveHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */