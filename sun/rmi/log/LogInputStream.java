package sun.rmi.log;

import java.io.IOException;
import java.io.InputStream;

public class LogInputStream
  extends InputStream
{
  private InputStream in;
  private int length;
  
  public LogInputStream(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    in = paramInputStream;
    length = paramInt;
  }
  
  public int read()
    throws IOException
  {
    if (length == 0) {
      return -1;
    }
    int i = in.read();
    length = (i != -1 ? length - 1 : 0);
    return i;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (length == 0) {
      return -1;
    }
    paramInt2 = length < paramInt2 ? length : paramInt2;
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    length = (i != -1 ? length - i : 0);
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong > 2147483647L) {
      throw new IOException("Too many bytes to skip - " + paramLong);
    }
    if (length == 0) {
      return 0L;
    }
    paramLong = length < paramLong ? length : paramLong;
    paramLong = in.skip(paramLong);
    length = ((int)(length - paramLong));
    return paramLong;
  }
  
  public int available()
    throws IOException
  {
    int i = in.available();
    return length < i ? length : i;
  }
  
  public void close()
  {
    length = 0;
  }
  
  protected void finalize()
    throws IOException
  {
    close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\log\LogInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */