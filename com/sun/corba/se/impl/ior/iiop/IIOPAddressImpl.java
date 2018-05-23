package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public final class IIOPAddressImpl
  extends IIOPAddressBase
{
  private ORB orb;
  private IORSystemException wrapper;
  private String host;
  private int port;
  
  public IIOPAddressImpl(ORB paramORB, String paramString, int paramInt)
  {
    orb = paramORB;
    wrapper = IORSystemException.get(paramORB, "oa.ior");
    if ((paramInt < 0) || (paramInt > 65535)) {
      throw wrapper.badIiopAddressPort(new Integer(paramInt));
    }
    host = paramString;
    port = paramInt;
  }
  
  public IIOPAddressImpl(InputStream paramInputStream)
  {
    host = paramInputStream.read_string();
    short s = paramInputStream.read_short();
    port = shortToInt(s);
  }
  
  public String getHost()
  {
    return host;
  }
  
  public int getPort()
  {
    return port;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\IIOPAddressImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */