package java.io;

import java.util.Arrays;

public class ByteArrayOutputStream
  extends OutputStream
{
  protected byte[] buf;
  protected int count;
  private static final int MAX_ARRAY_SIZE = 2147483639;
  
  public ByteArrayOutputStream()
  {
    this(32);
  }
  
  public ByteArrayOutputStream(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Negative initial size: " + paramInt);
    }
    buf = new byte[paramInt];
  }
  
  private void ensureCapacity(int paramInt)
  {
    if (paramInt - buf.length > 0) {
      grow(paramInt);
    }
  }
  
  private void grow(int paramInt)
  {
    int i = buf.length;
    int j = i << 1;
    if (j - paramInt < 0) {
      j = paramInt;
    }
    if (j - 2147483639 > 0) {
      j = hugeCapacity(paramInt);
    }
    buf = Arrays.copyOf(buf, j);
  }
  
  private static int hugeCapacity(int paramInt)
  {
    if (paramInt < 0) {
      throw new OutOfMemoryError();
    }
    return paramInt > 2147483639 ? Integer.MAX_VALUE : 2147483639;
  }
  
  public synchronized void write(int paramInt)
  {
    ensureCapacity(count + 1);
    buf[count] = ((byte)paramInt);
    count += 1;
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 - paramArrayOfByte.length > 0)) {
      throw new IndexOutOfBoundsException();
    }
    ensureCapacity(count + paramInt2);
    System.arraycopy(paramArrayOfByte, paramInt1, buf, count, paramInt2);
    count += paramInt2;
  }
  
  public synchronized void writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    paramOutputStream.write(buf, 0, count);
  }
  
  public synchronized void reset()
  {
    count = 0;
  }
  
  public synchronized byte[] toByteArray()
  {
    return Arrays.copyOf(buf, count);
  }
  
  public synchronized int size()
  {
    return count;
  }
  
  public synchronized String toString()
  {
    return new String(buf, 0, count);
  }
  
  public synchronized String toString(String paramString)
    throws UnsupportedEncodingException
  {
    return new String(buf, 0, count, paramString);
  }
  
  @Deprecated
  public synchronized String toString(int paramInt)
  {
    return new String(buf, paramInt, 0, count);
  }
  
  public void close()
    throws IOException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\ByteArrayOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */