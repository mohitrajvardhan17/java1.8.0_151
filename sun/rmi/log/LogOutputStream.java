package sun.rmi.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class LogOutputStream
  extends OutputStream
{
  private RandomAccessFile raf;
  
  public LogOutputStream(RandomAccessFile paramRandomAccessFile)
    throws IOException
  {
    raf = paramRandomAccessFile;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    raf.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    raf.write(paramArrayOfByte);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    raf.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public final void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\log\LogOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */