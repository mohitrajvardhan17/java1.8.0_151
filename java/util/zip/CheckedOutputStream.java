package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CheckedOutputStream
  extends FilterOutputStream
{
  private Checksum cksum;
  
  public CheckedOutputStream(OutputStream paramOutputStream, Checksum paramChecksum)
  {
    super(paramOutputStream);
    cksum = paramChecksum;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    out.write(paramInt);
    cksum.update(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    out.write(paramArrayOfByte, paramInt1, paramInt2);
    cksum.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public Checksum getChecksum()
  {
    return cksum;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\CheckedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */