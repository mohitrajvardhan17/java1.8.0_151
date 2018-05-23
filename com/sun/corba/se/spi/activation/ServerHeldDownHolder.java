package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHeldDownHolder
  implements Streamable
{
  public ServerHeldDown value = null;
  
  public ServerHeldDownHolder() {}
  
  public ServerHeldDownHolder(ServerHeldDown paramServerHeldDown)
  {
    value = paramServerHeldDown;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerHeldDownHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerHeldDownHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerHeldDownHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerHeldDownHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */