package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerDefHolder
  implements Streamable
{
  public ServerDef value = null;
  
  public ServerDefHolder() {}
  
  public ServerDefHolder(ServerDef paramServerDef)
  {
    value = paramServerDef;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerDefHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerDefHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerDefHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\RepositoryPackage\ServerDefHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */