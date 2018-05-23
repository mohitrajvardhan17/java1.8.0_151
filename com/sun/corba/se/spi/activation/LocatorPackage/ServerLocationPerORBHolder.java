package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationPerORBHolder
  implements Streamable
{
  public ServerLocationPerORB value = null;
  
  public ServerLocationPerORBHolder() {}
  
  public ServerLocationPerORBHolder(ServerLocationPerORB paramServerLocationPerORB)
  {
    value = paramServerLocationPerORB;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerLocationPerORBHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerLocationPerORBHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerLocationPerORBHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorPackage\ServerLocationPerORBHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */