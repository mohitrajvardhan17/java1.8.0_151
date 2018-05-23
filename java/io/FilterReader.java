package java.io;

public abstract class FilterReader
  extends Reader
{
  protected Reader in;
  
  protected FilterReader(Reader paramReader)
  {
    super(paramReader);
    in = paramReader;
  }
  
  public int read()
    throws IOException
  {
    return in.read();
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    return in.read(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return in.skip(paramLong);
  }
  
  public boolean ready()
    throws IOException
  {
    return in.ready();
  }
  
  public boolean markSupported()
  {
    return in.markSupported();
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    in.mark(paramInt);
  }
  
  public void reset()
    throws IOException
  {
    in.reset();
  }
  
  public void close()
    throws IOException
  {
    in.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FilterReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */