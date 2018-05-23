package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationHolder
  implements Streamable
{
  public ServerLocation value = null;
  
  public ServerLocationHolder() {}
  
  public ServerLocationHolder(ServerLocation paramServerLocation)
  {
    value = paramServerLocation;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerLocationHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerLocationHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerLocationHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */