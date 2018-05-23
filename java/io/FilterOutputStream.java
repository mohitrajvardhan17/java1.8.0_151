package java.io;

public class FilterOutputStream
  extends OutputStream
{
  protected OutputStream out;
  
  public FilterOutputStream(OutputStream paramOutputStream)
  {
    out = paramOutputStream;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    out.write(paramInt);
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 | paramInt2 | paramArrayOfByte.length - (paramInt2 + paramInt1) | paramInt1 + paramInt2) < 0) {
      throw new IndexOutOfBoundsException();
    }
    for (int i = 0; i < paramInt2; i++) {
      write(paramArrayOfByte[(paramInt1 + i)]);
    }
  }
  
  public void flush()
    throws IOException
  {
    out.flush();
  }
  
  public void close()
    throws IOException
  {
    OutputStream localOutputStream = out;
    Object localObject1 = null;
    try
    {
      flush();
    }
    catch (Throwable localThrowable2)
    {
      localObject1 = localThrowable2;
      throw localThrowable2;
    }
    finally
    {
      if (localOutputStream != null) {
        if (localObject1 != null) {
          try
          {
            localOutputStream.close();
          }
          catch (Throwable localThrowable3)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable3);
          }
        } else {
          localOutputStream.close();
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\FilterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */