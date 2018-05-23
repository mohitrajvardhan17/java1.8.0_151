package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class Encoding
  implements IDLEntity
{
  public short format = 0;
  public byte major_version = 0;
  public byte minor_version = 0;
  
  public Encoding() {}
  
  public Encoding(short paramShort, byte paramByte1, byte paramByte2)
  {
    format = paramShort;
    major_version = paramByte1;
    minor_version = paramByte2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\Encoding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */