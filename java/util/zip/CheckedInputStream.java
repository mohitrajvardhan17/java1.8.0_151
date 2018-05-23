package java.util.zip;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CheckedInputStream
  extends FilterInputStream
{
  private Checksum cksum;
  
  public CheckedInputStream(InputStream paramInputStream, Checksum paramChecksum)
  {
    super(paramInputStream);
    cksum = paramChecksum;
  }
  
  public int read()
    throws IOException
  {
    int i = in.read();
    if (i != -1) {
      cksum.update(i);
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 != -1) {
      cksum.update(paramArrayOfByte, paramInt1, paramInt2);
    }
    return paramInt2;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    byte[] arrayOfByte = new byte['Ȁ'];
    long l2;
    for (long l1 = 0L; l1 < paramLong; l1 += l2)
    {
      l2 = paramLong - l1;
      l2 = read(arrayOfByte, 0, l2 < arrayOfByte.length ? (int)l2 : arrayOfByte.length);
      if (l2 == -1L) {
        return l1;
      }
    }
    return l1;
  }
  
  public Checksum getChecksum()
  {
    return cksum;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\CheckedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */