package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class POANameHolder
  implements Streamable
{
  public String[] value = null;
  
  public POANameHolder() {}
  
  public POANameHolder(String[] paramArrayOfString)
  {
    value = paramArrayOfString;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = POANameHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    POANameHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return POANameHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\POANameHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */