package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerIdsHolder
  implements Streamable
{
  public int[] value = null;
  
  public ServerIdsHolder() {}
  
  public ServerIdsHolder(int[] paramArrayOfInt)
  {
    value = paramArrayOfInt;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ServerIdsHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ServerIdsHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ServerIdsHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerIdsHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */