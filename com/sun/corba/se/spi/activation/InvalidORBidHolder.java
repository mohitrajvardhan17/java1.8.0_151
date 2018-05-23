package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidORBidHolder
  implements Streamable
{
  public InvalidORBid value = null;
  
  public InvalidORBidHolder() {}
  
  public InvalidORBidHolder(InvalidORBid paramInvalidORBid)
  {
    value = paramInvalidORBid;
  }
  
  public void _read(InputStream paramInputStream)
  {
    value = InvalidORBidHelper.read(paramInputStream);
  }
  
  public void _write(OutputStream paramOutputStream)
  {
    InvalidORBidHelper.write(paramOutputStream, value);
  }
  
  public TypeCode _type()
  {
    return InvalidORBidHelper.type();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\InvalidORBidHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */