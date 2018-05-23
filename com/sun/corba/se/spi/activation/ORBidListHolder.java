package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBidListHolder
  implements Streamable
{
  public String[] value = null;
  
  public ORBidListHolder() {}
  
  public ORBidListHolder(String[] paramArrayOfString)
  {
    value = paramArrayOfString;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ORBidListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ORBidListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ORBidListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBidListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */