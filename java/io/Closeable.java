package java.io;

public abstract interface Closeable
  extends AutoCloseable
{
  public abstract void close()
    throws IOException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\Closeable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */