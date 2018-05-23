package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InitialNameServiceHolder
  implements Streamable
{
  public InitialNameService value = null;
  
  public InitialNameServiceHolder() {}
  
  public InitialNameServiceHolder(InitialNameService paramInitialNameService)
  {
    value = paramInitialNameService;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = InitialNameServiceHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    InitialNameServiceHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return InitialNameServiceHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InitialNameServiceHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */