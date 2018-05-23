package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class LocatorHolder
  implements Streamable
{
  public Locator value = null;
  
  public LocatorHolder() {}
  
  public LocatorHolder(Locator paramLocator)
  {
    value = paramLocator;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = LocatorHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    LocatorHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return LocatorHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */