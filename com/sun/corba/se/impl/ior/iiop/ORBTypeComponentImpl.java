package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.ORBTypeComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBTypeComponentImpl
  extends TaggedComponentBase
  implements ORBTypeComponent
{
  private int ORBType;
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ORBTypeComponentImpl)) {
      return false;
    }
    ORBTypeComponentImpl localORBTypeComponentImpl = (ORBTypeComponentImpl)paramObject;
    return ORBType == ORBType;
  }
  
  public int hashCode()
  {
    return ORBType;
  }
  
  public String toString()
  {
    return "ORBTypeComponentImpl[ORBType=" + ORBType + "]";
  }
  
  public ORBTypeComponentImpl(int paramInt)
  {
    ORBType = paramInt;
  }
  
  public int getId()
  {
    return 0;
  }
  
  public int getORBType()
  {
    return ORBType;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    paramOutputStream.write_ulong(ORBType);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\ORBTypeComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */