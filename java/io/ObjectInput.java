package java.io;

public abstract interface ObjectInput
  extends DataInput, AutoCloseable
{
  public abstract Object readObject()
    throws ClassNotFoundException, IOException;
  
  public abstract int read()
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract long skip(long paramLong)
    throws IOException;
  
  public abstract int available()
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ObjectInput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */