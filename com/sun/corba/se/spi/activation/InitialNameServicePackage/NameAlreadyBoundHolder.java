package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameAlreadyBoundHolder
  implements Streamable
{
  public NameAlreadyBound value = null;
  
  public NameAlreadyBoundHolder() {}
  
  public NameAlreadyBoundHolder(NameAlreadyBound paramNameAlreadyBound)
  {
    value = paramNameAlreadyBound;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = NameAlreadyBoundHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    NameAlreadyBoundHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return NameAlreadyBoundHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServicePackage\NameAlreadyBoundHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */