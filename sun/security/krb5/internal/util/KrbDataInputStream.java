package sun.security.krb5.internal.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

public class KrbDataInputStream
  extends BufferedInputStream
{
  private boolean bigEndian = true;
  
  public void setNativeByteOrder()
  {
    if (ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)) {
      bigEndian = true;
    } else {
      bigEndian = false;
    }
  }
  
  public KrbDataInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  public final int readLength4()
    throws IOException
  {
    int i = read(4);
    if (i < 0) {
      throw new IOException("Invalid encoding");
    }
    return i;
  }
  
  public int read(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    if (read(arrayOfByte, 0, paramInt) != paramInt) {
      throw new IOException("Premature end of stream reached");
    }
    int i = 0;
    for (int j = 0; j < paramInt; j++) {
      if (bigEndian) {
        i |= (arrayOfByte[j] & 0xFF) << (paramInt - j - 1) * 8;
      } else {
        i |= (arrayOfByte[j] & 0xFF) << j * 8;
      }
    }
    return i;
  }
  
  public int readVersion()
    throws IOException
  {
    int i = (read() & 0xFF) << 8;
    return i | read() & 0xFF;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\util\KrbDataInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */