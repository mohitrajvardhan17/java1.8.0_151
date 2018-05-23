package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NoSuchEndPointHolder
  implements Streamable
{
  public NoSuchEndPoint value = null;
  
  public NoSuchEndPointHolder() {}
  
  public NoSuchEndPointHolder(NoSuchEndPoint paramNoSuchEndPoint)
  {
    value = paramNoSuchEndPoint;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NoSuchEndPointHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NoSuchEndPointHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NoSuchEndPointHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\NoSuchEndPointHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */