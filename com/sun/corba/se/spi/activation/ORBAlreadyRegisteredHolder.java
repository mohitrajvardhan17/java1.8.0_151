package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBAlreadyRegisteredHolder
  implements Streamable
{
  public ORBAlreadyRegistered value = null;
  
  public ORBAlreadyRegisteredHolder() {}
  
  public ORBAlreadyRegisteredHolder(ORBAlreadyRegistered paramORBAlreadyRegistered)
  {
    value = paramORBAlreadyRegistered;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ORBAlreadyRegisteredHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ORBAlreadyRegisteredHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ORBAlreadyRegisteredHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBAlreadyRegisteredHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */