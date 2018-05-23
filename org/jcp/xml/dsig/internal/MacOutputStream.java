package org.jcp.xml.dsig.internal;

import java.io.ByteArrayOutputStream;
import javax.crypto.Mac;

public class MacOutputStream
  extends ByteArrayOutputStream
{
  private final Mac mac;
  
  public MacOutputStream(Mac paramMac)
  {
    mac = paramMac;
  }
  
  public void write(int paramInt)
  {
    super.write(paramInt);
    mac.update((byte)paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    mac.update(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\jcp\xml\dsig\internal\MacOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */