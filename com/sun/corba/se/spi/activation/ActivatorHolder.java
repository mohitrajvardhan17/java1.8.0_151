package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ActivatorHolder
  implements Streamable
{
  public Activator value = null;
  
  public ActivatorHolder() {}
  
  public ActivatorHolder(Activator paramActivator)
  {
    value = paramActivator;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ActivatorHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ActivatorHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ActivatorHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ActivatorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */