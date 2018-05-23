package com.sun.security.jgss;

import jdk.Exported;
import sun.misc.HexDumpEncoder;

@Exported
public final class AuthorizationDataEntry
{
  private final int type;
  private final byte[] data;
  
  public AuthorizationDataEntry(int paramInt, byte[] paramArrayOfByte)
  {
    type = paramInt;
    data = ((byte[])paramArrayOfByte.clone());
  }
  
  public int getType()
  {
    return type;
  }
  
  public byte[] getData()
  {
    return (byte[])data.clone();
  }
  
  public String toString()
  {
    return "AuthorizationDataEntry: type=" + type + ", data=" + data.length + " bytes:\n" + new HexDumpEncoder().encodeBuffer(data);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\jgss\AuthorizationDataEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */