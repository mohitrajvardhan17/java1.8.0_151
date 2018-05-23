package com.sun.corba.se.impl.ior.iiop;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.TaggedComponentBase;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionComponentImpl
  extends TaggedComponentBase
  implements MaxStreamFormatVersionComponent
{
  private byte version;
  public static final MaxStreamFormatVersionComponentImpl singleton = new MaxStreamFormatVersionComponentImpl();
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MaxStreamFormatVersionComponentImpl)) {
      return false;
    }
    MaxStreamFormatVersionComponentImpl localMaxStreamFormatVersionComponentImpl = (MaxStreamFormatVersionComponentImpl)paramObject;
    return version == version;
  }
  
  public int hashCode()
  {
    return version;
  }
  
  public String toString()
  {
    return "MaxStreamFormatVersionComponentImpl[version=" + version + "]";
  }
  
  public MaxStreamFormatVersionComponentImpl()
  {
    version = ORBUtility.getMaxStreamFormatVersion();
  }
  
  public MaxStreamFormatVersionComponentImpl(byte paramByte)
  {
    version = paramByte;
  }
  
  public byte getMaxStreamFormatVersion()
  {
    return version;
  }
  
  public void writeContents(OutputStream paramOutputStream)
  {
    paramOutputStream.write_octet(version);
  }
  
  public int getId()
  {
    return 38;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\iiop\MaxStreamFormatVersionComponentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */