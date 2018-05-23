package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndpointInfoListHolder
  implements Streamable
{
  public EndPointInfo[] value = null;
  
  public EndpointInfoListHolder() {}
  
  public EndpointInfoListHolder(EndPointInfo[] paramArrayOfEndPointInfo)
  {
    value = paramArrayOfEndPointInfo;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = EndpointInfoListHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    EndpointInfoListHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return EndpointInfoListHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\EndpointInfoListHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */