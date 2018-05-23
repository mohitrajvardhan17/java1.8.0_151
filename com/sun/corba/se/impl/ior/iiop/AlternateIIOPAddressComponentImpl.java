package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.AlternateIIOPAddressComponent;
import com.sun.corba.se.spi.ior.iiop.IIOPAddress;
import org.omg.CORBA_2_3.portable.OutputStream;

public class AlternateIIOPAddressComponentImpl
  extends TaggedComponentBase
  implements AlternateIIOPAddressComponent
{
  private IIOPAddress addr;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AlternateIIOPAddressComponentImpl)) {
      return false;
    }
    AlternateIIOPAddressComponentImpl localAlternateIIOPAddressComponentImpl = (AlternateIIOPAddressComponentImpl)paramObject;
    return addr.equals(addr);
  }
  
  public int hashCode()
  {
    return addr.hashCode();
  }
  
  public String toString()
  {
    return "AlternateIIOPAddressComponentImpl[addr=" + addr + "]";
  }
  
  public AlternateIIOPAddressComponentImpl(IIOPAddress paramIIOPAddress)
  {
    addr = paramIIOPAddress;
  }
  
  public IIOPAddress getAddress()
  {
    return addr;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    addr.write(paramOutputStream);
  }
  
  public int getId()
  {
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\AlternateIIOPAddressComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */