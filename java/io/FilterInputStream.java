package java.io;

public class FilterInputStream
  extends InputStream
{
  protected volatile InputStream in;
  
  protected FilterInputStream(InputStream paramInputStream)
  {
    in = paramInputStream;
  }
  
  public int read()
    throws IOException
  {
    return in.read();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return in.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return in.skip(paramLong);
  }
  
  public int available()
    throws IOException
  {
    return in.available();
  }
  
  public void close()
    throws IOException
  {
    in.close();
  }
  
  public synchronized void mark(int paramInt)
  {
    in.mark(paramInt);
  }
  
  public synchronized void reset()
    throws IOException
  {
    in.reset();
  }
  
  public boolean markSupported()
  {
    return in.markSupported();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FilterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */