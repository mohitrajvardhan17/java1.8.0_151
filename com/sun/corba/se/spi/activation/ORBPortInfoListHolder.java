package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoListHolder
  implements Streamable
{
  public ORBPortInfo[] value = null;
  
  public ORBPortInfoListHolder() {}
  
  public ORBPortInfoListHolder(ORBPortInfo[] paramArrayOfORBPortInfo)
  {
    value = paramArrayOfORBPortInfo;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = ORBPortInfoListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    ORBPortInfoListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return ORBPortInfoListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */