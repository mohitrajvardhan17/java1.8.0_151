package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoHolder
  implements Streamable
{
  public ORBPortInfo value = null;
  
  public ORBPortInfoHolder() {}
  
  public ORBPortInfoHolder(ORBPortInfo paramORBPortInfo)
  {
    value = paramORBPortInfo;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ORBPortInfoHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ORBPortInfoHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ORBPortInfoHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */