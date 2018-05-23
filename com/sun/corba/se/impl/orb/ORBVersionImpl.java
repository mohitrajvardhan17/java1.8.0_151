package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.ORBVersion;
import org.omg.CORBA.portable.OutputStream;

public class ORBVersionImpl
  implements ORBVersion
{
  private byte orbType;
  public static final ORBVersion FOREIGN = new ORBVersionImpl((byte)0);
  public static final ORBVersion OLD = new ORBVersionImpl((byte)1);
  public static final ORBVersion NEW = new ORBVersionImpl((byte)2);
  public static final ORBVersion JDK1_3_1_01 = new ORBVersionImpl((byte)3);
  public static final ORBVersion NEWER = new ORBVersionImpl((byte)10);
  public static final ORBVersion PEORB = new ORBVersionImpl((byte)20);
  
  public ORBVersionImpl(byte paramByte)
  {
    orbType = paramByte;
  }
  
  public byte getORBType()
  {
    return orbType;
  }
  
  public void write(OutputStream paramOutputStream)
  {
    paramOutputStream.write_octet(orbType);
  }
  
  public String toString()
  {
    return "ORBVersionImpl[" + Byte.toString(orbType) + "]";
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ORBVersion)) {
      return false;
    }
    ORBVersion localORBVersion = (ORBVersion)paramObject;
    return localORBVersion.getORBType() == orbType;
  }
  
  public int hashCode()
  {
    return orbType;
  }
  
  public boolean lessThan(ORBVersion paramORBVersion)
  {
    return orbType < paramORBVersion.getORBType();
  }
  
  public int compareTo(Object paramObject)
  {
    return getORBType() - ((ORBVersion)paramObject).getORBType();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\ORBVersionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */