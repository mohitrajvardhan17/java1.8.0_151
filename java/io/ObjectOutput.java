package java.io;

public abstract interface ObjectOutput
  extends DataOutput, AutoCloseable
{
  public abstract void writeObject(Object paramObject)
    throws IOException;
  
  public abstract void write(int paramInt)
    throws IOException;
  
  public abstract void write(byte[] paramArrayOfByte)
    throws IOException;
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public abstract void flush()
    throws IOException;
  
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ObjectOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */